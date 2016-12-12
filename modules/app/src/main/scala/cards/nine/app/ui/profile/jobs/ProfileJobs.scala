package cards.nine.app.ui.profile.jobs

import java.util.Date

import android.app.Activity
import android.content.Intent
import cards.nine.app.commons.Conversions
import cards.nine.app.services.sync.SynchronizeDeviceService
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters.{CollectionAddedActionFilter, SyncAskActionFilter}
import cards.nine.app.ui.profile.ProfileActivity
import cards.nine.app.ui.profile.models.AccountSync
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{CloudStorageDeviceSummary, RawCloudStorageDevice, SharedCollection, User}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import play.api.libs.json.Json

class ProfileJobs(val profileUiActions: ProfileUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with CollectionJobs {

  import ProfileActivity._

  val tagDialog = "dialog"

  var syncEnabled: Boolean = false

  def driveConnected(): TaskService[Unit] = statuses.apiClient match {
    case Some(client) if client.isConnected => loadUserAccounts(client)
    case _ =>
      for {
        _ <- profileUiActions.showLoading()
        _ <- tryToConnect()
      } yield ()
  }

  def driveConnectionFailed(connectionResult: ConnectionResult): TaskService[Unit] =
    if (connectionResult.hasResolution) {
      withActivityTask { activity =>
        connectionResult.startResolutionForResult(activity, resolveGooglePlayConnection)
      }
    } else {
      profileUiActions.showEmptyAccountsContent(error = true)
    }

  def initialize(): TaskService[Unit] = {

    def showUserAndConnect(user: User): TaskService[Unit] = user.email match {
      case Some(email) =>
        for {
          apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
          _ <- TaskService.right(statuses = statuses.copy(apiClient = Some(apiClient)))
          _ <- profileUiActions.userProfile(user.userProfile.name, email, user.userProfile.avatar)
          _ <- tryToConnect()
        } yield ()
      case None => TaskService.left(JobException("User without email"))
    }

    for {
      theme <- getThemeTask
      _ <- profileUiActions.initialize(theme)
      user <- di.userProcess.getUser
      _ <- showUserAndConnect(user)
    } yield ()
  }

  def resume(): TaskService[Unit] =
    askBroadCastTask(BroadAction(SyncAskActionFilter.action))

  def stop(): TaskService[Unit] =
    TaskService(CatchAll[JobException](statuses.apiClient foreach (_.disconnect())))

  def onOffsetChanged(percentage: Float): TaskService[Unit] =
    for {
      _ <- profileUiActions.handleToolbarVisibility(percentage)
      _ <- profileUiActions.handleProfileVisibility(percentage)
    } yield ()

  def accountSynced(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.synchronizeConfiguration()
      _ <- loadUserAccounts()
      _ <- profileUiActions.showMessageAccountSynced()
      _ <- TaskService.right(syncEnabled = false)
    } yield ()

  def errorSyncing(): TaskService[Unit] =
    for {
      _ <- profileUiActions.showSyncingError()
      _ <- TaskService.right(syncEnabled = true)
    } yield ()

  def stateSyncing(): TaskService[Unit] = TaskService.right(syncEnabled = false).map(_ => (): Unit)

  def loadUserAccounts(): TaskService[Unit] = withConnectedClient(loadUserAccounts(_))

  def saveSharedCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.addToMyCollectionsFromProfile(sharedCollection.name)
      collection <- addSharedCollection(sharedCollection)
      _ <- profileUiActions.showAddCollectionMessage(sharedCollection.sharedCollectionId)
      _ <- sendBroadCastTask(BroadAction(CollectionAddedActionFilter.action, Some(collection.id.toString)))
    } yield ()

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.shareCollectionFromProfile(sharedCollection.name)
      _ <- di.launcherExecutorProcess.launchShare(getString(R.string.shared_collection_url, sharedCollection.id))
    } yield ()

  def loadPublications(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.showPublicationsContent()
      _ <- profileUiActions.showLoading()
      collections <- di.sharedCollectionsProcess.getPublishedCollections()
      _ <- if (collections.isEmpty) {
        profileUiActions.showEmptyPublicationsContent(error = false)
      } else profileUiActions.loadPublications(collections)
    } yield ()

  def loadSubscriptions(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.showSubscriptionsContent()
      _ <- profileUiActions.showLoading()
      subscriptions <- di.sharedCollectionsProcess.getSubscriptions()
      _ <- if (subscriptions.isEmpty) {
        profileUiActions.showEmptySubscriptionsContent(error = false)
      } else profileUiActions.setSubscriptionsAdapter(subscriptions)
    } yield ()

  def changeSubscriptionStatus(sharedCollectionId: String, subscribeStatus: Boolean): TaskService[Unit] = {

    val subscribeService =
      for {
        _ <- di.trackEventProcess.subscribeToCollection(sharedCollectionId)
        _ <- di.sharedCollectionsProcess.subscribe(sharedCollectionId)
      } yield ()

    val unsubscribeService =
      for {
        _ <- di.trackEventProcess.unsubscribeFromCollection(sharedCollectionId)
        _ <- di.sharedCollectionsProcess.unsubscribe(sharedCollectionId)
      } yield ()

    for {
      _ <- if (subscribeStatus) subscribeService else unsubscribeService
      _ <- profileUiActions.showUpdatedSubscriptions(sharedCollectionId, subscribeStatus)
    } yield ()
  }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): TaskService[Unit] =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
      case (`resolveGooglePlayConnection`, _) =>
        profileUiActions.showEmptyAccountsContent(error = true)
    }

  def quit(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.logout()
      _ <- di.collectionProcess.cleanCollections()
      _ <- di.deviceProcess.deleteAllDockApps()
      _ <- di.momentProcess.deleteAllMoments()
      _ <- di.widgetsProcess.deleteAllWidgets()
      _ <- di.userProcess.unregister
      _ <- withActivityTask { activity =>
        activity.setResult(ResultCodes.logoutSuccessful)
        activity.finish()
      }
    } yield ()

  def launchService(): TaskService[Unit] =
    if (syncEnabled) {
      for {
        _ <- profileUiActions.showMessageSyncingAccount
        _ <- withActivityTask { activity =>
          syncEnabled = false
          activity.startService(new Intent(activity, classOf[SynchronizeDeviceService]))
        }
      } yield ()
    } else TaskService.empty

  def deleteDevice(cloudId: String): TaskService[Unit] =
    withConnectedClient { client =>
      for {
        _ <- di.trackEventProcess.deleteConfiguration()
        _ <- profileUiActions.showLoading()
        _ <- di.cloudStorageProcess.deleteCloudStorageDevice(client, cloudId)
        _ <- loadUserAccounts(client, Seq(cloudId))
      } yield ()
    }

  def copyDevice(maybeName: Option[String], cloudId: String, actualName: String): TaskService[Unit] =
    copyOrRenameDevice(maybeName, cloudId, actualName, copy = true)

  def renameDevice(maybeName: Option[String], cloudId: String, actualName: String): TaskService[Unit] =
    copyOrRenameDevice(maybeName, cloudId, actualName, copy = false)

  def printDeviceInfo(cloudId: String): TaskService[Unit] = {

    def printDeviceInfo(device: RawCloudStorageDevice, prettyJson: String): Unit = {
      AppLog.info(s"----------------------------- Device Info -----------------------------")
      AppLog.info(s" Cloud id: ${device.cloudId}")
      AppLog.info(s" UUID: ${device.uuid}")
      AppLog.info(s" Device name: ${device.title}")
      AppLog.info(s" Device id: ${device.deviceId}")
      AppLog.info(s" Created date: ${device.createdDate}")
      AppLog.info(s" Modified date: ${device.modifiedDate}")
      AppLog.info(s" JSON")
      AppLog.info(prettyJson)
      AppLog.info(s"----------------------------- Device Info -----------------------------")
    }

    withConnectedClient { client =>
      for {
        device <- di.cloudStorageProcess.getRawCloudStorageDevice(client, cloudId)
        prettyJson <- TaskService(CatchAll[JobException](Json.prettyPrint(Json.parse(device.json))))
      } yield printDeviceInfo(device, prettyJson)
    }

  }

  private[this] def copyOrRenameDevice(
    maybeName: Option[String],
    cloudId: String,
    actualName: String,
    copy: Boolean): TaskService[Unit] = {

    def createOrUpdate(name: String, client: GoogleApiClient, cloudId: String) =
      for {
        device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
        maybeCloudId = if (copy) None else Some(cloudId)
        _ <- if (copy) di.trackEventProcess.copyConfiguration() else di.trackEventProcess.changeConfigurationName()
        _ <- di.cloudStorageProcess.createOrUpdateCloudStorageDevice(client, maybeCloudId, device.data.copy(deviceName = name))
      } yield ()

    maybeName match {
      case Some(name) if name.nonEmpty =>
        withConnectedClient { client =>
          for {
            _ <- profileUiActions.showLoading()
            _ <- createOrUpdate(name, client, cloudId)
            _ <- loadUserAccounts(client)
          } yield ()
        }
      case _ => profileUiActions.showInvalidConfigurationNameError()
    }
  }

  private[this] def tryToConnect(): TaskService[Unit] =
    TaskService(CatchAll[JobException](statuses.apiClient foreach (_.connect())))

  private[this] def loadUserAccounts(
    client: GoogleApiClient,
    filterOutResourceIds: Seq[String] = Seq.empty): TaskService[Unit] = {

    def toAccountSync(d: CloudStorageDeviceSummary, current: Boolean = false): AccountSync =
      AccountSync.syncDevice(title = d.deviceName, syncDate = d.modifiedDate, current = current, cloudId = d.cloudId)

    def order(seq: Seq[CloudStorageDeviceSummary]): Seq[CloudStorageDeviceSummary] =
      seq.sortBy(_.modifiedDate)(Ordering[Date].reverse)

    def createSync(devices: Seq[CloudStorageDeviceSummary]) =
      devices.partition(_.currentDevice) match {
        case (current, other) =>
          val currentDevices = order(current)
          val currentDevicesWithHeader = currentDevices.headOption map { device =>
            Seq(AccountSync.header(getString(R.string.syncCurrent)), toAccountSync(device, current = true))
          } getOrElse Seq.empty
          val otherDevices = order(other ++ currentDevices.drop(1)) match {
            case seq if seq.isEmpty => Seq.empty
            case seq => AccountSync.header(getString(R.string.syncHeaderDevices)) +:
              (seq map (toAccountSync(_)))
          }
          currentDevicesWithHeader ++ otherDevices
      }

    def loadAccounts(
      client: GoogleApiClient,
      filterOutResourceIds: Seq[String] = Seq.empty) = {
      di.cloudStorageProcess.getCloudStorageDevices(client).map { devices =>
        createSync(devices.filterNot(d => filterOutResourceIds.contains(d.cloudId)))
      }
    }

    for {
      _ <- di.trackEventProcess.showAccountsContent()
      _ <- profileUiActions.showLoading()
      accountsSync <- loadAccounts(client, filterOutResourceIds)
      _ <- TaskService.right(syncEnabled = true)
      _ <- if (accountsSync.isEmpty) {
        profileUiActions.showEmptyAccountsContent(error = false)
      } else {
        profileUiActions.setAccountsAdapter(accountsSync)
      }
    } yield ()
  }

  private[this] def withConnectedClient(f: (GoogleApiClient) => TaskService[Unit]): TaskService[Unit] = {

    def loadUserEmail() = di.userProcess.getUser.resolveRight(
      mapRight = _.email match {
        case Some(email) => Right(email)
        case None => Left(JobException("User without email"))
      }
    )

    def loadUserInfo(): TaskService[Unit] =
      for {
        _ <- profileUiActions.showLoading()
        email <- loadUserEmail()
        apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
        _ <- TaskService.right(statuses = statuses.copy(apiClient = Some(apiClient)))
        _ <- tryToConnect()
      } yield ()

    statuses.apiClient match {
      case Some(client) if client.isConnected =>
        f(client)
      case Some(client) =>
        for {
          _ <- profileUiActions.showLoading()
          _ <- tryToConnect()
        } yield ()
      case _ =>
        loadUserInfo()
    }
  }

  protected def getString(res: Int): String = resGetString(res)

  protected def getString(res: Int, args: AnyRef*): String = resGetString(res, args)

}