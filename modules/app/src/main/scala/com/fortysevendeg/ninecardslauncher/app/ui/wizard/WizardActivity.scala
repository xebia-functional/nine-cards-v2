package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.Account
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.GoogleApiClientActivityProvider
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserPermissions
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.google.android.gms.common.api.GoogleApiClient
import macroid.{Contexts, Ui}

import scala.util.Try

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GoogleApiClientActivityProvider
  with TypedFindView
  with WizardUiActionsImpl
  with BroadcastDispatcher { self =>

  var clientStatuses = GoogleApiClientStatuses()

  override lazy val presenter = new WizardPresenter(self)

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (WizardActionFilter(action), data) match {
    case (WizardStateActionFilter, Some(`stateSuccess`)) =>
      presenter.saveCurrentDevice(clientStatuses.apiClient)
    case (WizardStateActionFilter, Some(`stateFailure`)) =>
      presenter.goToUser()
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) =>
      presenter.goToWizard()
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    presenter.initialize()
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
  override def onRequestConnectionError(errorCode: Int): Unit = presenter.connectionError()

  override def onResolveConnectionError(): Unit = presenter.connectionError()

  override def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  override def onConnected(bundle: Bundle): Unit =
    presenter.getDevices(clientStatuses.apiClient, clientStatuses.username, clientStatuses.userPermissions)

  override def createGoogleApiClient(account: Account): GoogleApiClient = {
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

}

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None,
  userPermissions: Option[UserPermissions] = None)