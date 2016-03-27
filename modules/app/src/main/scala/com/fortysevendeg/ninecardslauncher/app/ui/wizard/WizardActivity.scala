package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientActivityProvider
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{Contexts, Ui}

import scala.util.Try

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GoogleApiClientActivityProvider
  with WizardActions
  with TypedFindView
  with WizardComposer
  with BroadcastDispatcher { self =>

  var clientStatuses = GoogleApiClientStatuses()

  implicit lazy val presenter = new WizardPresenter(self)

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (WizardActionFilter(action), data) match {
    case (WizardStateActionFilter, Some(`stateSuccess`)) => storeCloudDevice()
    case (WizardStateActionFilter, Some(`stateFailure`)) => showUserView.run
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) => showWizardView.run
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    (showUserView ~ initUi(presenter.getAccounts.get)).run
  }

  override def onResume(): Unit = {
    super.onResume()
    registerDispatchers
    self ? WizardAskActionFilter.action
  }

  override def onPause(): Unit = {
    super.onPause()
    unregisterDispatcher
  }

  override def onStop(): Unit = {
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), _, _) => Try(client.disconnect())
      case _ =>
    }
    super.onStop()
  }

  override def onBackPressed(): Unit = {}

  // TODO - Implement error code
  override def onRequestConnectionError(errorCode: Int): Unit =
    showErrorConnectingGoogle().run

  override def onResolveConnectionError(): Unit = showErrorConnectingGoogle().run

  override def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  override def onConnected(bundle: Bundle): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), Some(username), Some(userPermissions)) =>
        presenter.getDevices(client, username, userPermissions).run
      case _ =>
        showErrorConnectingGoogle().run
    }

  private[this] def storeCloudDevice(): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), Some(username), _) =>
        presenter.saveCurrentDevice(client, username).run
      case _ => finishProcess.run
    }

  override def showLoading(): Ui[Any] = showLoadingView

  override def createGoogleApiClient(account: Account): Ui[GoogleApiClient] = Ui {
    val client = createGoogleDriveClient(account.name)
    clientStatuses = clientStatuses.copy(
      apiClient = Some(client),
      username = Some(account.name))
    client
  }

  override def connectGoogleApiClient(userPermissions: UserPermissions): Ui[Any] = Ui {
    clientStatuses = clientStatuses.copy(userPermissions = Some(userPermissions))
    clientStatuses.apiClient foreach (_.connect())
  }

  override def showErrorLoginUser(): Ui[Any] = backToUser(R.string.errorLoginUser)

  override def showErrorConnectingGoogle(): Ui[Any] = backToUser(R.string.errorConnectingGoogle)

  override def showDevices(devices: UserCloudDevices): Ui[Any] = loadDevicesView(devices)

  override def showDiveIn(): Ui[Any] = finishProcess

  override def navigateToLauncher(): Ui[Any] = Ui {
    setResult(Activity.RESULT_OK)
    finish()
  }

  override def navigateToWizard(): Ui[Any] = showWizardView

  override def startCreateCollectionsService(maybeKey: Option[String]): Ui[Any] = Ui {
    val intent = new Intent(this, classOf[CreateCollectionService])
    maybeKey foreach (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    startService(intent)
  }
}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None,
  userPermissions: Option[UserPermissions] = None)