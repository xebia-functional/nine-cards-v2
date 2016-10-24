package cards.nine.app.ui.profile

import java.util.Date

import android.app.Activity
import android.content.Intent
import cards.nine.app.commons.Conversions
import cards.nine.app.services.sync.SynchronizeDeviceService
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.action_filters.{CollectionAddedActionFilter, SyncAskActionFilter}
import cards.nine.app.ui.profile.models.AccountSync
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{RawCloudStorageDevice, CloudStorageDeviceSummary, SharedCollection, User}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import play.api.libs.json.Json

class ProfileJobs(actions: ProfileUiActions)(implicit contextWrapper: ActivityContextWrapper)
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
        _ <- actions.showLoading()
        _ <- tryToConnect()
      } yield ()
  }

  def driveConnectionFailed(connectionResult: ConnectionResult): TaskService[Unit] =
    if (connectionResult.hasResolution) {
      withActivityTask { activity =>
        connectionResult.startResolutionForResult(activity, resolveGooglePlayConnection)
      }
    } else {
      actions.showEmptyAccountsContent(error = true)
    }

  def initialize(): TaskService[Unit] = {

    def showUserAndConnect(user: User): TaskService[Unit] = user.email match {
      case Some(email) =>
        for {
          apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
          _ <- TaskService.right(statuses = statuses.copy(apiClient = Some(apiClient)))
          _ <- actions.userProfile(user.userProfile.name, email, user.userProfile.avatar)
          _ <- tryToConnect()
        } yield ()
      case None => TaskService.left(JobException("User without email"))
    }


    for {
      theme <- getThemeTask
      _ <- actions.initialize(theme)
      user <- di.userProcess.getUser
      _ <- showUserAndConnect(user)
    } yield ()
  }

  def resume(): TaskService[Unit] =
    askBroadCastTask(BroadAction(SyncAskActionFilter.action))

  def stop(): TaskService[Unit] =
    TaskService(CatchAll[JobException](statuses.apiClient foreach(_.disconnect())))

  def onOffsetChanged(percentage: Float): TaskService[Unit] =
    for {
      _ <- actions.handleToolbarVisibility(percentage)
      _ <- actions.handleProfileVisibility(percentage)
    } yield ()

  def accountSynced(): TaskService[Unit] =
    for {
      _ <- loadUserAccounts()
      _ <- actions.showMessageAccountSynced()
      _ <- TaskService.right(syncEnabled = false)
    } yield ()

  def errorSyncing(): TaskService[Unit] =
    for {
      _ <- actions.showSyncingError()
      _ <- TaskService.right(syncEnabled = true)
    } yield ()

  def stateSyncing(): TaskService[Unit] = TaskService.right(syncEnabled = false).map(_ => (): Unit)

  def loadUserAccounts(): TaskService[Unit] = withConnectedClient(loadUserAccounts(_))

  def saveSharedCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      collection <- addSharedCollection(sharedCollection)
      _ <- actions.showAddCollectionMessage(sharedCollection.sharedCollectionId)
      _ <- sendBroadCastTask(BroadAction(CollectionAddedActionFilter.action, Some(collection.id.toString)))
    } yield ()

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))

  def loadPublications(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      collections <- di.sharedCollectionsProcess.getPublishedCollections()
      _ <- if (collections.isEmpty) {
        actions.showEmptyPublicationsContent(error = false)
      } else actions.loadPublications(collections)
    } yield ()

  def loadSubscriptions(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      subscriptions <- di.sharedCollectionsProcess.getSubscriptions()
      _ <- if (subscriptions.isEmpty) {
        actions.showEmptySubscriptionsContent(error = false)
      } else actions.setSubscriptionsAdapter(subscriptions)
    } yield ()

  def changeSubscriptionStatus(sharedCollectionId: String, subscribeStatus: Boolean): TaskService[Unit] = {

    val service = if (subscribeStatus) {
      di.sharedCollectionsProcess.subscribe(_)
    } else {
      di.sharedCollectionsProcess.unsubscribe(_)
    }

    for {
      _ <- service(sharedCollectionId)
      _ <- actions.showUpdatedSubscriptions(sharedCollectionId, subscribeStatus)
    } yield ()
  }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): TaskService[Unit] =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
      case (`resolveGooglePlayConnection`, _) =>
        actions.showEmptyAccountsContent(error = true)
    }

  def quit(): TaskService[Unit] =
    for {
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
      withActivityTask { activity =>
        syncEnabled = false
        activity.startService(new Intent(activity, classOf[SynchronizeDeviceService]))
      }
    } else TaskService.empty

  def deleteDevice(cloudId: String): TaskService[Unit] =
    withConnectedClient { client =>
      for {
        _ <- actions.showLoading()
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
        device <- di.cloudStorageProcess.getRawCloudStorageDevice(client , cloudId)
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
        _ <- di.cloudStorageProcess.createOrUpdateCloudStorageDevice(client, maybeCloudId, device.data.copy(deviceName = name))
      } yield ()


    maybeName match {
      case Some(name) if name.nonEmpty =>
        withConnectedClient { client =>
          for {
            _ <- actions.showLoading()
            _ <- createOrUpdate(name, client, cloudId)
            _ <- loadUserAccounts(client)
          } yield ()
        }
      case _ => actions.showInvalidConfigurationNameError()
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
            Seq(AccountSync.header(resGetString(R.string.syncCurrent)), toAccountSync(device, current = true))
          } getOrElse Seq.empty
          val otherDevices = order(other ++ currentDevices.drop(1)) match {
            case seq if seq.isEmpty => Seq.empty
            case seq => AccountSync.header(resGetString(R.string.syncHeaderDevices)) +:
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
      _ <- actions.showLoading()
      accountsSync <- loadAccounts(client, filterOutResourceIds)
      _ <- TaskService.right(syncEnabled = true)
      _ <- if (accountsSync.isEmpty) {
        actions.showEmptyAccountsContent(error = false)
      } else {
        actions.setAccountsAdapter(accountsSync)
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
        _ <- actions.showLoading()
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
          _ <- actions.showLoading()
          _ <- tryToConnect()
        } yield ()
      case _ =>
        loadUserInfo()
    }
  }
}