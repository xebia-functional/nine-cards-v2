package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserPermissions
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.google.android.gms.common.api.GoogleApiClient
import macroid.FullDsl._
import macroid.{Contexts, Ui}

import scala.util.Try
import scalaz.concurrent.Task

case class GoogleApiClientStatuses(
  apiClient: Option[GoogleApiClient] = None,
  username: Option[String] = None,
  userPermissions: Option[UserPermissions] = None)

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GoogleApiClientProvider
  with WizardListeners
  with WizardTasks
  with WizardPersistence
  with TypedFindView
  with WizardComposer
  with BroadcastDispatcher { self =>

  implicit lazy val di = new Injector

  private[this] val accountType = "com.google"

  lazy val accountManager: AccountManager = AccountManager.get(this)

  lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

  var clientStatuses = GoogleApiClientStatuses()

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageCommand(action: String, data: Option[String]): Unit = (WizardActionFilter(action), data) match {
    case (WizardStateActionFilter, Some(`stateSuccess`)) => runUi(finishProcess)
    case (WizardStateActionFilter, Some(`stateFaliure`)) => runUi(showUser)
    case (WizardAnswerActionFilter, Some(`stateCreatingCollections`)) => runUi(showWizard)
    case _ =>
  }

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    runUi(showUser ~ initUi(accounts))
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

  def requestToken(username: String): Unit =
    getAccountAndAndroidId(username) match {
      case Some((account, _)) =>
        val client = createGoogleDriveClient(username)
        clientStatuses = clientStatuses.copy(
          apiClient = Some(client),
          username = Some(username))
        invalidateToken()
        Task.fork(requestUserPermissions(accountManager, account, client).run).resolveAsyncUi(
          userPermissions => {
            clientStatuses = clientStatuses.copy(userPermissions = Some(userPermissions))
            client.connect()
            showLoading
          },
          onRequestTokensException)
      case _ => runUi(backToUser(R.string.errorLoginUser))
    }

  def finishUi: Ui[_] = Ui {
    setResult(Activity.RESULT_OK)
    finish()
  }

  def launchService(maybeKey: Option[String]): Unit = {
    val intent = new Intent(this, classOf[CreateCollectionService])
    maybeKey map (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    startService(intent)
  }

  // TODO - Implement error code
  override def onRequestConnectionError(errorCode: Int): Unit =
    runUi(backToUser(R.string.errorConnectingGoogle))

  override def onResolveConnectionError(): Unit = runUi(backToUser(R.string.errorConnectingGoogle))

  override def tryToConnect(): Unit = clientStatuses.apiClient foreach (_.connect())

  override def onConnected(bundle: Bundle): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(client), Some(username), Some(userPermissions)) =>
        loadCloudDevices(client, username, userPermissions)
      case _ =>
        runUi(backToUser(R.string.errorConnectingGoogle))
    }

  private[this] def loadCloudDevices(client: GoogleApiClient, username: String, userPermissions: UserPermissions) =
    getAccountAndAndroidId(username) match {
      case Some((account, id)) =>
        invalidateToken()
        Task.fork(loadUserDevices(client, id, username, userPermissions).run).resolveAsyncUi(
          userCloudDevices => showLoading ~ searchDevices(userCloudDevices),
          onLoadDevicesException)
      case _ => runUi(backToUser(R.string.errorConnectingGoogle))
    }

  private[this] def getAccountAndAndroidId(username: String): Option[(Account, String)] =
    for {
      account <- accounts find (_.name == username)
      androidId <- getAndroidId
    } yield (account, androidId)

  private[this] def invalidateToken() = {
    getToken foreach (accountManager.invalidateAuthToken(accountType, _))
    setToken(javaNull)
  }

  private[this] def onRequestTokensException[E >: Exception](exception: E): Ui[_] = exception match {
    case ex: AuthTokenOperationCancelledException => backToUser(R.string.canceledGooglePermission)
    case _ => backToUser(R.string.errorConnectingGoogle)
  }

  private[this] def onLoadDevicesException[E >: Exception](exception: E): Ui[_] = exception match {
    case ex: UserException => backToUser(R.string.errorLoginUser)
    case ex: UserConfigException => backToUser(R.string.errorLoginUser)
    case _ => backToUser(R.string.errorConnectingGoogle)
  }

}

