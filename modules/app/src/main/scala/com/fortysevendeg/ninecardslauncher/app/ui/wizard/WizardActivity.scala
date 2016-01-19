package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{GoogleApiClientStatuses, GoogleApiClientProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.google.android.gms.common.api.GoogleApiClient
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

  def requestToken(username: String): Unit = connectGoogleDrive(username)

  def finishUi: Ui[_] = Ui {
    setResult(Activity.RESULT_OK)
    finish()
  }

  def launchService(maybeKey: Option[String]): Unit = {
    val intent = new Intent(this, classOf[CreateCollectionService])
    maybeKey map (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    startService(intent)
  }

  override def onRequestConnectionError(): Unit = runUi(backToUser(R.string.errorConnectingGoogle))

  override def onResolveConnectionError(): Unit = runUi(backToUser(R.string.errorConnectingGoogle))

  override def onConnected(bundle: Bundle): Unit =
    clientStatuses match {
      case GoogleApiClientStatuses(Some(username), Some(client)) =>
        loadCloudDevices(username, client)
      case _ =>
        runUi(backToUser(R.string.errorConnectingGoogle))
    }

  private[this] def loadCloudDevices(username: String, client: GoogleApiClient) =
    getAccountAndAndroidId(username) match {
      case Some((account, id)) =>
        invalidateToken()
        Task.fork(loadDevices(accountManager, account, client).run).resolveAsyncUi(
          userCloudDevices => showLoading ~ searchDevices(userCloudDevices),
          onException)
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

  private[this] def onException[E >: Exception](exception: E): Ui[_] = exception match {
    case ex: AuthTokenOperationCancelledException => backToUser(R.string.canceledGooglePermission)
    case _ => backToUser(R.string.errorConnectingGoogle)
  }

}

