package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.app.Activity
import android.content.Intent
import android.os.{Build, Bundle}
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid.{Contexts, Ui}

import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class WizardActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with WizardTasks
  with WizardPersistence
  with TypedFindView
  with WizardComposer {

  implicit lazy val di = new Injector

  lazy val accountManager: AccountManager = AccountManager.get(this)

  lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.wizard_activity)
    runUi(showUser ~ loadUsers(accounts, requestToken, launchService, close()))
  }

  override def onBackPressed(): Unit = {
    if (finished) {
      setResult(Activity.RESULT_OK)
      finish()
      super.onBackPressed()
    }
  }

  private[this] def requestToken(username: String): Unit = (for {
    account <- accounts find (_.name == username)
    androidId <- getAndroidId
  } yield {
      invalidateToken()
      val oauthScopes = resGetString(R.string.oauth_scopes)
      accountManager.getAuthToken(account, oauthScopes, null, this, new AccountManagerCallback[Bundle] {
        override def run(future: AccountManagerFuture[Bundle]): Unit = {
          Try {
            val authTokenBundle: Bundle = future.getResult
            val token: String = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN)
            setToken(token)
            Device(
              name = Build.MODEL,
              deviceId = androidId,
              secretToken = token,
              permissions = Seq(oauthScopes)
            )
          } match {
            case Success(device) =>
              Task.fork(signInUser(username, device).run).resolveAsyncUi(
                userInfo => showLoading ~ searchDevices(userInfo),
                ex => uiShortToast(R.string.errorConnectingGoogle) ~ showUser)
            case Failure(ex) => ex match {
              case ex: OperationCanceledException => runUi(uiShortToast(R.string.canceledGooglePermission) ~ showUser)
              case _ => runUi(uiShortToast(R.string.errorConnectingGoogle) ~ showUser)
            }
          }
        }
      }, null)
    }) getOrElse runUi(uiShortToast(R.string.errorConnectingGoogle) ~ showUser)

  private[this] def close(): Ui[_] = Ui {
    setResult(Activity.RESULT_OK)
    finish()
  }

  private[this] def invalidateToken() = {
    getToken foreach (accountManager.invalidateAuthToken(accountType, _))
    setToken(null)
  }

  private def launchService(maybeKey: Option[String]) = {
    val intent = new Intent(this, classOf[CreateCollectionService])
    maybeKey map (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    startService(intent)
  }

}

