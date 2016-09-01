package com.fortysevendeg.ninecardslauncher.app.ui.profile

import java.util.Date

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Presenter, ResultCodes}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.services.SynchronizeDeviceService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.{ConnectionSuspendedCause, GoogleDriveApiClientProvider}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

import scala.util.{Failure, Try}
import scalaz.concurrent.Task

class ProfilePresenter(actions: ProfileUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
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
    Task.fork(di.userProcess.getUser.value).resolveAsync(
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

  def loadPublications(): Unit = {
    Task.fork(di.sharedCollectionsProcess.getPublishedCollections().value).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (sharedCollections: Seq[SharedCollection]) => {
        if (sharedCollections.isEmpty) {
          actions.showEmptyMessageInScreen(() => loadPublications())
        } else {
          actions.loadPublications(sharedCollections)
        }
      },
      onException = (ex: Throwable) => actions.showErrorLoadingCollectionInScreen(() => loadPublications()))
  }

  def loadSubscriptions(): Unit = {
    // TODO - Load publications and set adapter
    actions.setSubscriptionsAdapter(sampleItems("Subscription")).run
  }

  private[this] def sampleItems(tab: String) = 1 to 20 map (i => s"$tab Item $i")

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

    Task.fork(logout.value).resolveAsyncUi(
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
      Task.fork(deleteAccountDevice(client, cloudId).value).resolveAsyncUi(
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
          Task.fork(copyAccountDevice(name, client, cloudId).value).resolveAsyncUi(
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

    Task.fork(loadAccounts(client, filterOutResourceIds).value).resolveAsyncUi(
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
      Task.fork(loadUserEmail().value).resolveAsyncUi(
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

  def showErrorLoadingCollectionInScreen(clickAction: () => Unit): Ui[Any]

  def showEmptyMessageInScreen(clickAction: () => Unit): Ui[Any]

  def showContactUsError(clickAction: () => Unit): Ui[Any]

  def showConnectingGoogleError(clickAction: () => Unit): Ui[Any]

  def showLoadingUserError(clickAction: () => Unit): Ui[Any]

  def showSyncingError(): Ui[Any]

  def showInvalidConfigurationNameError(resourceId: String): Ui[Any]

  def showMessageAccountSynced(): Ui[Any]

  def showDialogForDeleteDevice(resourceId: String): Unit

  def showDialogForCopyDevice(resourceId: String): Unit

  def loadPublications(sharedCollections: Seq[SharedCollection]): Ui[Any]

  def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[_]

  def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any]

  def setSubscriptionsAdapter(items: Seq[String]): Ui[Any]

  def handleToolbarVisibility(percentage: Float): Ui[Any]

  def handleProfileVisibility(percentage: Float): Ui[Any]

}