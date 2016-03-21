package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientActivityProvider
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
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
    case (WizardStateActionFilter, Some(`stateFailure`)) => showUser.run
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) => showWizard.run
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    (showUser ~ initUi(presenter.getAccounts)).run
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
    backToUser(R.string.errorConnectingGoogle).run

  override def onResolveConnectionError(): Unit = backToUser(R.string.errorConnectingGoogle).run

  override def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  override def onConnected(bundle: Bundle): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), Some(username), Some(userPermissions)) =>
        presenter.loadDevices(client, username, userPermissions)
      case _ =>
        backToUser(R.string.errorConnectingGoogle).run
    }

  private[this] def storeCloudDevice(): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), Some(username), _) =>
        presenter.storeCurrentDevice(client, username)
      case _ => finishProcess.run
    }

  override def onResultLoadAccount(userPermissions: UserPermissions): Ui[Any] = {
    clientStatuses = clientStatuses.copy(userPermissions = Some(userPermissions))
    clientStatuses.apiClient foreach(_.connect())
    showLoading
  }

  override def onExceptionLoadAccount(exception: Throwable): Ui[Any] = exception match {
    case ex: AuthTokenOperationCancelledException => backToUser(R.string.canceledGooglePermission)
    case _ => backToUser(R.string.errorConnectingGoogle)
  }

  override def onResultLoadUser(account: Account): Ui[Any] = Ui {
    val client = createGoogleDriveClient(account.name)
    clientStatuses = clientStatuses.copy(
      apiClient = Some(client),
      username = Some(account.name))
    presenter.loadAccount(account, client)
  }

  override def onExceptionLoadUser(): Ui[Any] = backToUser(R.string.errorConnectingGoogle)

  override def onResultLoadDevices(devices: UserCloudDevices): Ui[Any] = showLoading ~ searchDevices(devices)

  override def onExceptionLoadDevices(exception: Throwable): Ui[Any] = exception match {
    case ex: UserException => backToUser(R.string.errorLoginUser)
    case ex: UserConfigException => backToUser(R.string.errorLoginUser)
    case _ => backToUser(R.string.errorConnectingGoogle)
  }

  override def onResultStoreCurrentDevice(unit: Unit): Ui[Any] = finishProcess

  override def onExceptionStoreCurrentDevice(exception: Throwable): Ui[Any] = finishProcess

  override def onResultWizard(): Ui[Any] = Ui {
    setResult(Activity.RESULT_OK)
    finish()
  }

}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None,
  userPermissions: Option[UserPermissions] = None)