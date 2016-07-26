package com.fortysevendeg.ninecardslauncher.app.ui.profile

import java.util.Date

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Presenter, ResultCodes}
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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.{ConnectionSuspendedCause, GoogleDriveApiClientProvider}
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
    Task.fork(di.userProcess.getUser.run).resolveAsync(
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

  def showDialogForDeleteDevice(cloudId: String): Unit = {
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        val dialog = new RemoveAccountDeviceDialogFragment(() => deleteDevice(cloudId))
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

  private[this] def deleteDevice(cloudId: String): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client)) if client.isConnected =>
        Task.fork(deleteAccountDevice(client, cloudId).run).resolveAsyncUi(
          onResult = (_) => Ui(loadUserAccounts(client, Seq(cloudId))),
          onException = (_) => actions.showContactUsError(() => deleteDevice(cloudId)),
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
        devices.filterNot(d => filterOutResourceIds.contains(d.cloudId))
      }
      createSync(filteredDevices)
    }
  }

  private[this] def deleteAccountDevice(client: GoogleApiClient, cloudId: String): ServiceDef2[Unit, CloudStorageProcessException] =  {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    cloudStorageProcess.deleteCloudStorageDevice(cloudId)
  }

  private[this] def logout(): ServiceDef2[Unit, CollectionException with DockAppException with MomentException with UserException] =
    for {
      _ <- di.collectionProcess.cleanCollections()
      _ <- di.deviceProcess.deleteAllDockApps()
      _ <- di.momentProcess.deleteAllMoments()
      _ <- di.userProcess.unregister
    } yield ()

  private[this] def createSync(devices: Seq[CloudStorageDeviceSummary]): Seq[AccountSync] = {

    def toAccountSync(d: CloudStorageDeviceSummary, current: Boolean = false): AccountSync =
      AccountSync.syncDevice(title = d.title, syncDate = d.modifiedDate, current = current, cloudId = d.cloudId)

    def order(seq: Seq[CloudStorageDeviceSummary]): Seq[CloudStorageDeviceSummary] =
      seq.sortBy(_.modifiedDate)(Ordering[Date].reverse)

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

  def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[_]

  def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any]

  def setPublicationsAdapter(items: Seq[String]): Ui[Any]

  def setSubscriptionsAdapter(items: Seq[String]): Ui[Any]

  def handleToolbarVisibility(percentage: Float): Ui[Any]

  def handleProfileVisibility(percentage: Float): Ui[Any]

}