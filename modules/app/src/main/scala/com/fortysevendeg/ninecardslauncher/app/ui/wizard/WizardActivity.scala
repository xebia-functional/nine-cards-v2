package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.app.Activity
import android.content.{Context, Intent}
import android.net.Uri
import android.os.{Build, Bundle}
import android.support.v4.app.FragmentActivity
import android.support.v7.app.ActionBarActivity
import android.widget.RadioButton
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity._
import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserInfo
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{Contexts, Transformer, Ui}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._

import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class WizardActivity
  extends ActionBarActivity
  with Contexts[FragmentActivity]
  with ContextSupportProvider
  with WizardTasks
  with Layout {

  implicit lazy val di = new Injector

  lazy val preferences = getSharedPreferences(googleKeyPreferences, Context.MODE_PRIVATE)

  lazy val accountManager = AccountManager.get(this)

  private var finished = false

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(layout)
    runUi(loadUsers)
  }

  override def onBackPressed(): Unit = {
    if (finished) {
      setResult(Activity.RESULT_OK)
      finish()
      super.onBackPressed()
    }
  }

  def loadUsers: Ui[_] = {
    val accountManager: AccountManager = AccountManager.get(this)
    val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq
    addUsersToRadioGroup(accounts) ~
      (userAction <~ On.click {
        usersGroup <~ selectUser
      }) ~
      (deviceAction <~ On.click {
        devicesGroup <~ selectDevice
      }) ~
      (finishAction <~ On.click {
        Ui {
          setResult(Activity.RESULT_OK)
          finish()
        }
      })
  }

  private def selectUser = Transformer {
    case i: RadioButton if i.isChecked =>
      requestToken(i.getTag.toString)
      showLoading
  }

  private def selectDevice = Transformer {
    case i: RadioButton if i.isChecked =>
      val tag = i.getTag.toString
      (if (tag == NewConfigurationKey) {
        launchService()
      } else {
        launchService(Option(tag))
      }) ~ showWizard
  }

  // R.string.deviceNotFoundMessage

  private def searchDevices(userInfo: UserInfo) = addDevicesToRadioGroup(userInfo.devices) ~
    showDevices ~
    (titleDevice <~ tvText(resGetString(R.string.addDeviceTitle, userInfo.name)))

  private def showLoading: Ui[_] =
    (loadingRootLayout <~ vVisible) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  private def showUser: Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vVisible) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  private def showWizard: Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vVisible) ~
      (deviceRootLayout <~ vGone) ~
      Ui(finished = true)

  private def showDevices: Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vVisible)

  private[this] def requestToken(username: String) = (for {
    account <- getAccount(username)
    androidId <- getAndroidId
  } yield {
    setUser(username)
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
            Task.fork(signInUser(username, device)).resolveAsyncUi(
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

  private[this] def getAccount(name: String): Option[Account] = {
    val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq
    accounts find (_.name == name)
  }

  private[this] def getUser: Option[String] = Option(preferences.getString(googleKeyUser, null))

  private[this] def getToken: Option[String] = Option(preferences.getString(googleKeyToken, null))

  private[this] def setUser(user: String) = preferences.edit.putString(googleKeyUser, user).apply()

  private[this] def setToken(token: String) = preferences.edit.putString(googleKeyToken, token).apply()

  private[this] def invalidateToken() {
    getToken foreach (accountManager.invalidateAuthToken(accountType, _))
    setToken(null)
  }

  private[this] def getAndroidId: Option[String] = {
    val cursor = Option(getContentResolver.query(Uri.parse(contentGServices), null, null, Array(androidId), null))
    cursor filter (c => c.moveToFirst && c.getColumnCount >= 2) map (_.getLong(1).toHexString.toUpperCase)
  }

  private def launchService(maybeKey: Option[String] = None) = Ui {
    val intent = new Intent(this, classOf[CreateCollectionService])
    maybeKey map (key => intent.putExtra(CreateCollectionService.keyDevice, key))
    startService(intent)
  }

}

object WizardActivity {
  val googleKeyPreferences = "__google_auth__"
  val googleKeyUser = "__google_user__"
  val googleKeyToken = "__google_token__"
  val accountType = "com.google"
  val androidId = "android_id"
  val contentGServices = "content://com.google.android.gsf.gservices"
}