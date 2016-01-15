package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.app.Activity
import android.content.Intent
import android.os.{Build, Bundle}
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.GoogleApiClientProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import macroid.FullDsl._
import macroid.{Contexts, Ui}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._

import scalaz.concurrent.Task

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with GoogleApiClientProvider
  with WizarListeners
  with WizardTasks
  with WizardPersistence
  with TypedFindView
  with WizardComposer
  with BroadcastDispatcher { self =>

  implicit lazy val di = new Injector

  lazy val accountManager: AccountManager = AccountManager.get(this)

  lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

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

  override def onBackPressed(): Unit = {}

  def requestToken(username: String): Unit =
    getAccountAndAndroidId(username) match {
      case Some((account, id)) =>
        invalidateToken()
        Task.fork(loadDevices(account, username, id).run).resolveAsyncUi(
          userCloudDevices => showLoading ~ searchDevices(userCloudDevices),
          onException)
      case _ => runUi(uiShortToast(R.string.errorConnectingGoogle) ~ showUser)
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

  private[this] def onException[E >: Exception](e: E): Ui[_] = e match {
    case ex: AuthTokenOperationCancelledException => uiShortToast(R.string.canceledGooglePermission) ~ showUser
    case _ => uiShortToast(R.string.errorConnectingGoogle) ~ showUser
  }

  private[this] def getAccountAndAndroidId(username: String): Option[(Account, String)] = {
    for {
      account <- accounts find (_.name == username)
      androidId <- getAndroidId
    } yield (account, androidId)
  }

  private[this] def loadDevices(account: Account, username: String, androidId: String) = {
    val oauthScopes = resGetString(R.string.oauth_scopes)
    val driveScope = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/drive.appdata"
    for {
      token <- getAuthToken(accountManager, account, oauthScopes)
      _ = setToken(token)
      driveToken <- getAuthToken(accountManager, account, driveScope)
      device = Device(
        name = Build.MODEL,
        deviceId = androidId,
        secretToken = token,
        permissions = Seq(oauthScopes))
      userCloudDevices <- signInUser(username, device)
    } yield userCloudDevices
  }

  private[this] def invalidateToken() = {
    getToken foreach (accountManager.invalidateAuthToken(accountType, _))
    setToken(javaNull)
  }

  override def onRequestConnectionError(): Unit = {}

  override def onResolveConnectionError(): Unit = {}
}

