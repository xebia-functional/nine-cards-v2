package com.fortysevendeg.ninecardslauncher.app.ui.profile

import java.util.Date

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cats.data.EitherT
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions}
import com.fortysevendeg.ninecardslauncher.app.services.SynchronizeDeviceService
import com.fortysevendeg.ninecardslauncher.app.ui.collections.tasks.CollectionJobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.CollectionAddedActionFilter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.{ConnectionSuspendedCause, GoogleDriveApiClientProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Jobs, ResultCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, Subscription}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task

import scala.util.{Failure, Try}

class ProfilePresenter(actions: ProfileUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with CollectionJobs
  with GoogleDriveApiClientProvider {

  import Statuses._

  val tagDialog = "dialog"

  var clientStatuses = GoogleApiClientStatuses()

  var syncEnabled: Boolean = false

  override def onDriveConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit = {}

  override def onDriveConnected(bundle: Bundle): Unit = clientStatuses match {
    case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
      loadUserAccounts(client)
    case _ => actions.showConnectingGoogleError(() => tryToConnect()).run
  }

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    if (connectionResult.hasResolution) {
      contextWrapper.original.get match {
        case Some(activity) =>
          Try(connectionResult.startResolutionForResult(activity, resolveGooglePlayConnection)) match {
            case Failure(e) => showError()
            case _ =>
          }
        case _ =>
      }
    } else {
      showError()
    }

  def initialize(): Unit = {
    di.userProcess.getUser.resolveAsync2(
      onResult = user => {
        (user.userProfile.name, user.email) match {
          case (Some(name), Some(email)) =>
            actions.userProfile(name, email, user.userProfile.avatar).run
          case _ =>
        }
      })
    actions.initialize().run
  }

  def stop(): Unit = clientStatuses match {
    case GoogleApiClientStatuses(Some(client)) => Try(client.disconnect())
    case _ =>
  }

  def onOffsetChanged(percentage: Float) =
    (actions.handleToolbarVisibility(percentage) ~ actions.handleProfileVisibility(percentage)).run

  def accountSynced(): Unit = {
    loadUserAccounts()
    actions.showMessageAccountSynced().run
    syncEnabled = false
  }

  def errorSyncing(): Unit = {
    actions.showSyncingError().run
    syncEnabled = true
  }

  def stateSyncing(): Unit = {
    // Nothing now. We can show a loading here if it's necessary
    syncEnabled = false
  }

  def loadUserAccounts(): Unit = withConnectedClient(loadUserAccounts(_))

  def saveSharedCollection(sharedCollection: SharedCollection): Unit = {
    addSharedCollection(sharedCollection).resolveAsyncUi2(
      onResult = (c) => actions.showAddCollectionMessage(sharedCollection.sharedCollectionId) ~ Ui(sendBroadCast(BroadAction(CollectionAddedActionFilter.action, Some(c.id.toString)))),
      onException = (ex) => actions.showErrorSavingCollectionInScreen(() => loadPublications()))
  }

  def shareCollection(sharedCollection: SharedCollection): Unit =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))
      .resolveAsyncUi2(onException = _ => actions.showContactUsError())

  def loadPublications(): Unit =
    di.sharedCollectionsProcess.getPublishedCollections().resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = (sharedCollections) => {
        if (sharedCollections.isEmpty) {
          actions.showEmptyPublicationsMessageInScreen(() => loadPublications())
        } else {
          actions.loadPublications(sharedCollections, saveSharedCollection, shareCollection)
        }
      },
      onException = (ex: Throwable) => actions.showErrorLoadingCollectionInScreen(() => loadPublications()))

  def loadSubscriptions(): Unit = {

    def getSubscriptions: EitherT[Task, NineCardException, (Seq[Subscription])] =
      for {
        subscriptions <- di.sharedCollectionsProcess.getSubscriptions()
      } yield subscriptions

    getSubscriptions.resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = {
        case subscriptions if subscriptions.isEmpty =>
          actions.showEmptySubscriptionsMessageInScreen() ~
            actions.hideLoading()
        case subscriptions =>
          actions.setSubscriptionsAdapter(subscriptions, onSubscribe)
      },
      onException = (ex: Throwable) => actions.showErrorLoadingSubscriptionsInScreen())
  }

  def onSubscribe(sharedCollectionId: String, subscribeStatus: Boolean): Unit = {

    def subscribe(sharedCollectionId: String): EitherT[Task, NineCardException, Unit] =
      for {
        _ <- di.sharedCollectionsProcess.subscribe(sharedCollectionId)
      } yield ()

    def unsubscribe(originalSharedCollectionId: String): EitherT[Task, NineCardException, Unit] =
      for {
        _ <- di.sharedCollectionsProcess.unsubscribe(originalSharedCollectionId)
      } yield ()

      (if (subscribeStatus) subscribe(sharedCollectionId) else unsubscribe(sharedCollectionId)).resolveAsyncUi2(
        onResult = (_) => actions.showUpdatedSubscriptions(sharedCollectionId, subscribeStatus),
        onException = (ex) => actions.showErrorSubscribing(() => loadSubscriptions()))
  }

  def showError(): Unit = actions.showConnectingGoogleError(() => tryToConnect()).run

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
        true
      case (`resolveGooglePlayConnection`, _) =>
        showError()
        true
      case _ => false
    }

  def quit(): Unit = {

    def logout =
      for {
        _ <- di.collectionProcess.cleanCollections()
        _ <- di.deviceProcess.deleteAllDockApps()
        _ <- di.momentProcess.deleteAllMoments()
        _ <- di.widgetsProcess.deleteAllWidgets()
        _ <- di.userProcess.unregister
      } yield ()

    logout.resolveAsyncUi2(
      onResult = (_) => Ui {
        contextWrapper.original.get foreach { activity =>
          activity.setResult(ResultCodes.logoutSuccessful)
          activity.finish()
        }
      },
      onException = (_) => actions.showContactUsError(quit))
  }

  def launchService(): Unit = if (syncEnabled) {
    contextWrapper.original.get map { activity =>
      syncEnabled = false
      activity.startService(new Intent(activity, classOf[SynchronizeDeviceService]))
    }
  }

  def deleteDevice(cloudId: String): Unit = {

    def deleteAccountDevice(client: GoogleApiClient, cloudId: String) =  {
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      cloudStorageProcess.deleteCloudStorageDevice(cloudId)
    }

    withConnectedClient { client =>
      deleteAccountDevice(client, cloudId).resolveAsyncUi2(
        onResult = (_) => Ui(loadUserAccounts(client, Seq(cloudId))),
        onException = (_) => actions.showContactUsError(() => deleteDevice(cloudId)),
        onPreTask = () => actions.showLoading())
    }
  }

  def copyDevice(maybeName: Option[String], cloudId: String): Unit = {

    def copyAccountDevice(name: String, client: GoogleApiClient, cloudId: String) = {
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      for {
        device <- cloudStorageProcess.getCloudStorageDevice(cloudId)
        _ <- cloudStorageProcess.createCloudStorageDevice(device.data.copy(deviceName = name))
      } yield ()
    }

    maybeName match {
      case Some(name) if name.length > 0 =>
        withConnectedClient { client =>
          copyAccountDevice(name, client, cloudId).resolveAsyncUi2(
            onResult = (_) => Ui(loadUserAccounts(client)),
            onException = (_) => actions.showContactUsError(() => copyDevice(maybeName, cloudId)),
            onPreTask = () => actions.showLoading())
        }
      case _ => actions.showInvalidConfigurationNameError(cloudId).run
    }
  }

  private[this] def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  private[this] def loadUserAccounts(
    client: GoogleApiClient,
    filterOutResourceIds: Seq[String] = Seq.empty): Unit = {

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
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      cloudStorageProcess.getCloudStorageDevices.map { devices =>
        val filteredDevices = if (filterOutResourceIds.isEmpty) {
          devices
        } else {
          devices.filterNot(d => filterOutResourceIds.contains(d.cloudId))
        }
        createSync(filteredDevices)
      }
    }

    loadAccounts(client, filterOutResourceIds).resolveAsyncUi2(
      onResult = accountSyncs => {
        syncEnabled = true
        if (accountSyncs.isEmpty) {
          launchService()
          actions.showLoading()
        } else {
          actions.setAccountsAdapter(accountSyncs)
        }
      },
      onException = (_) => actions.showConnectingGoogleError(() => loadUserAccounts(client)),
      onPreTask = () => actions.showLoading()
    )
  }

  private[this] def withConnectedClient[R](f: (GoogleApiClient) => R) = {

    def loadUserEmail() = di.userProcess.getUser.map(_.email)

    def loadUserInfo(): Unit =
      loadUserEmail().resolveAsyncUi2(
        onResult = email => Ui {
          val client = email map createGoogleDriveClient
          clientStatuses = clientStatuses.copy(apiClient = client)
          client foreach (_.connect())
        },
        onException = (_) => actions.showLoadingUserError(loadUserInfo),
        onPreTask = () => actions.showLoading()
      )

    clientStatuses match {
      case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
        f(client)
      case GoogleApiClientStatuses(Some(client)) =>
        tryToConnect()
        actions.showLoading().run
      case _ =>
        loadUserInfo()
    }
  }
}

object Statuses {

  case class GoogleApiClientStatuses(apiClient: Option[GoogleApiClient] = None)

}

trait ProfileUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def hideLoading(): Ui[Any]

  def showAddCollectionMessage(mySharedCollectionId: String): Ui[Any]

  def showErrorLoadingCollectionInScreen(clickAction: () => Unit): Ui[Any]

  def showEmptyPublicationsMessageInScreen(clickAction: () => Unit): Ui[Any]

  def showErrorLoadingSubscriptionsInScreen(): Ui[Any]

  def showEmptySubscriptionsMessageInScreen(): Ui[Any]

  def showUpdatedSubscriptions(originalSharedCollectionId: String, subscribed: Boolean): Ui[Any]

  def showErrorSubscribing(clickAction: () => Unit): Ui[Any]

  def showContactUsError(clickAction: () => Unit): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showConnectingGoogleError(clickAction: () => Unit): Ui[Any]

  def showLoadingUserError(clickAction: () => Unit): Ui[Any]

  def showSyncingError(): Ui[Any]

  def showInvalidConfigurationNameError(resourceId: String): Ui[Any]

  def showErrorSavingCollectionInScreen(clickAction: () => Unit): Ui[Any]

  def showMessageAccountSynced(): Ui[Any]

  def showDialogForDeleteDevice(resourceId: String): Unit

  def showDialogForCopyDevice(resourceId: String): Unit

  def loadPublications(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit): Ui[Any]

  def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[Any]

  def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any]

  def setSubscriptionsAdapter(
    items: Seq[Subscription],
    onSubscribe: (String, Boolean) => Unit): Ui[Any]

  def handleToolbarVisibility(percentage: Float): Ui[Any]

  def handleProfileVisibility(percentage: Float): Ui[Any]

}