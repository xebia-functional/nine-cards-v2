package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Presenter, ResultCodes, UserProfileProvider, UserProfileStatuses}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDeviceSummary
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.services.SynchronizeDeviceService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.process.device.DockAppException
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog.RemoveAccountDeviceDialogFragment
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{ActivityContextWrapper, Ui}

import scala.util.{Failure, Try}
import scalaz.concurrent.Task

class ProfilePresenter(actions: ProfileUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with GoogleApiClientProvider {

  import Statuses._

  val tagDialog = "dialog"

  var clientStatuses = GoogleApiClientStatuses()

  var userProfileStatuses = UserProfileStatuses()

  var syncEnabled: Boolean = false

  override def onConnectionFailed(connectionResult: ConnectionResult): Unit =
    if (connectionResult.hasResolution) {
      Try(connectionResult.startResolutionForResult(actions.getActivityForIntent, resolveGooglePlayConnection)) match {
        case Failure(e) => showError()
        case _ =>
      }
    } else {
      showError()
    }

  override def onConnected(bundle: Bundle): Unit = clientStatuses match {
    case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
      loadUserAccounts(client)
    case _ => actions.showConnectingGoogleError(() => tryToConnect()).run
  }

  def initialize(): Unit = {
    Task.fork(loadUserEmail().run).resolveAsyncUi(
      onResult = email => Ui {
        val userProfile = email map { email =>
          new UserProfileProvider(
            account = email,
            onConnectedUserProfile = (name: String, email: String, avatarUrl: Option[String]) => {
              actions.userProfile(name, email, avatarUrl).run
            },
            onConnectedPlusProfile = (_) => {})
        }
        userProfileStatuses = userProfileStatuses.copy(userProfile = userProfile)
        userProfileStatuses.userProfile foreach (_.connect())
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
    // TODO - Reload adapter
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

  def loadUserAccounts(): Unit = clientStatuses match {
    case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
      loadUserAccounts(client)
    case GoogleApiClientStatuses(Some(client)) =>
      tryToConnect()
      actions.showLoading().run
    case _ =>
      loadUserInfo()
  }

  def loadPublications(): Unit = {
    // TODO - Load publications and set adapter
    actions.setPublicationsAdapter(sampleItems("Publication")).run
  }

  def loadSubscriptions(): Unit = {
    // TODO - Load publications and set adapter
    actions.setSubscriptionsAdapter(sampleItems("Subscription")).run
  }

  private[this] def sampleItems(tab: String) = 1 to 20 map (i => s"$tab Item $i")

  def showError(): Unit = actions.showConnectingGoogleError(() => tryToConnect()).run

  def showDialogForDeleteDevice(resourceId: String): Unit = {
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        val dialog = new RemoveAccountDeviceDialogFragment(() => deleteDevice(resourceId))
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
        true
      case (`resolveGooglePlayConnection`, _) =>
        showError()
        true
      case (`resolveConnectedUser`, _) =>
        userProfileStatuses.userProfile foreach (_.connectUserProfile(requestCode, resultCode, data))
        true
      case _ => false
    }


  def quit(): Unit =
    Task.fork(logout().run).resolveAsyncUi(
      onResult = (_) => Ui {
        contextWrapper.original.get foreach { activity =>
          activity.setResult(ResultCodes.logoutSuccessful)
          activity.finish()
        }
      },
      onException = (_) => actions.showContactUsError(quit))

  def launchService(): Unit = if (syncEnabled) {
    contextWrapper.original.get map { activity =>
      syncEnabled = false
      activity.startService(new Intent(activity, classOf[SynchronizeDeviceService]))
    }
  }

  private[this] def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  private[this] def loadUserInfo(): Unit =
    Task.fork(loadUserEmail().run).resolveAsyncUi(
      onResult = email => Ui {
        val client = email map createGoogleDriveClient
        clientStatuses = clientStatuses.copy(apiClient = client)
        client foreach (_.connect())
      },
      onException = (_) => actions.showLoadingUserError(loadUserInfo),
      onPreTask = () => actions.showLoading()
    )

  private[this] def loadUserAccounts(
    client: GoogleApiClient,
    filterOutResourceIds: Seq[String] = Seq.empty): Unit =
    Task.fork(loadAccounts(client, filterOutResourceIds).run).resolveAsyncUi(
      onResult = accountSyncs => {
        syncEnabled = true
        actions.setAccountsAdapter(accountSyncs)
      },
      onException = (_) => actions.showConnectingGoogleError(() => loadUserAccounts(client)),
      onPreTask = () => actions.showLoading()
    )

  private[this] def deleteDevice(resourceId: String): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
        Task.fork(deleteAccountDevice(client, resourceId).run).resolveAsyncUi(
          onResult = (_) => Ui(loadUserAccounts(client, Seq(resourceId))),
          onException = (_) => actions.showContactUsError(() => deleteDevice(resourceId)),
          onPreTask = () => actions.showLoading())
      case _ => actions.showConnectingGoogleError(() => tryToConnect())
    }

  private[this] def loadUserEmail(): ServiceDef2[Option[String], UserException] = di.userProcess.getUser.map(_.email)

  private[this] def loadAccounts(
    client: GoogleApiClient,
    filterOutResourceIds: Seq[String] = Seq.empty): ServiceDef2[Seq[AccountSync], CloudStorageProcessException] =  {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    cloudStorageProcess.getCloudStorageDevices.map { devices =>
      val filteredDevices = if (filterOutResourceIds.isEmpty) {
        devices
      } else {
        devices.filterNot(d => filterOutResourceIds.contains(d.resourceId))
      }
      createSync(filteredDevices)
    }
  }

  private[this] def deleteAccountDevice(client: GoogleApiClient, driveId: String): ServiceDef2[Unit, CloudStorageProcessException] =  {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    cloudStorageProcess.deleteCloudStorageDevice(driveId)
  }

  private[this] def logout(): ServiceDef2[Unit, CollectionException with DockAppException with MomentException with UserException] =
    for {
      _ <- di.collectionProcess.cleanCollections()
      _ <- di.deviceProcess.deleteAllDockApps
      _ <- di.momentProcess.deleteAllMoments()
      _ <- di.userProcess.unregister
    } yield ()

  private[this] def createSync(devices: Seq[CloudStorageDeviceSummary]): Seq[AccountSync] = {
    val currentDevice = devices.find(_.currentDevice) map { d =>
      AccountSync.syncDevice(title = d.title, syncDate = d.modifiedDate, current = true, resourceId = d.resourceId)
    }
    val otherDevices = devices.filterNot(_.currentDevice) map { d =>
      AccountSync.syncDevice(title = d.title, syncDate = d.modifiedDate, resourceId = d.resourceId)
    }
    val otherDevicesWithHeader = if (otherDevices.isEmpty) {
      Seq.empty
    } else {
      AccountSync.header(resGetString(R.string.syncHeaderDevices)) +:
        otherDevices
    }
    (AccountSync.header(resGetString(R.string.syncCurrent)) +:
      currentDevice.toSeq) ++ otherDevicesWithHeader
  }

}

object Statuses {

  case class GoogleApiClientStatuses(apiClient: Option[GoogleApiClient] = None)

}

trait ProfileUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showContactUsError(clickAction: () => Unit): Ui[Any]

  def showConnectingGoogleError(clickAction: () => Unit): Ui[Any]

  def showLoadingUserError(clickAction: () => Unit): Ui[Any]

  def showSyncingError(): Ui[Any]

  def showMessageAccountSynced(): Ui[Any]

  def getActivityForIntent: Activity

//  def createGoogleDriveClient(account: String): GoogleApiClient

  def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[_]

  def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any]

  def setPublicationsAdapter(items: Seq[String]): Ui[Any]

  def setSubscriptionsAdapter(items: Seq[String]): Ui[Any]

  def handleToolbarVisibility(percentage: Float): Ui[Any]

  def handleProfileVisibility(percentage: Float): Ui[Any]

}