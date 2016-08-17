package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.{Account, AccountManager, OperationCanceledException}
import android.app.Activity
import android.content.{Context, Intent}
import android.os.{Build, Bundle}
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.google_api.{ConnectionSuspendedCause, GoogleDriveApiClientProvider, GooglePlusApiClientProvider}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevices
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.{CatchAll, _}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDeviceData, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcess, CloudStorageProcessException, ImplicitsCloudStorageProcessExceptions}
import com.fortysevendeg.ninecardslauncher.process.social.SocialProfileProcessException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import macroid.{ActivityContextWrapper, Ui}
import rapture.core.{Answer, Errata, Result}

import scala.reflect.ClassTag
import scala.util.{Failure, Try}
import scalaz.concurrent.Task
import scalaz.{-\/, \/, \/-}

class WizardPresenter(actions: WizardUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with GoogleDriveApiClientProvider
  with GooglePlusApiClientProvider
  with ImplicitsCloudStorageProcessExceptions
  with ImplicitsAuthTokenException {

  import Statuses._

  val accountType = "com.google"

  val tagDialog = "dialog-not-accepted"

  lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

  lazy val accountManager: AccountManager = AccountManager.get(contextSupport.context)

  var clientStatuses = WizardPresenterStatuses()

  def initialize(): Unit = actions.initialize(accounts).run

  def goToUser(): Unit = actions.goToUser().run

  def goToWizard(): Unit = actions.goToWizard().run

  def processFinished(): Unit = actions.showDiveIn().run

  def connectAccount(username: String, termsAccept: Boolean): Unit = {

    def getAccount(username: String): Option[Account] = accounts find (_.name == username)

    if (termsAccept) {
      getAccount(username) match {
        case Some(acc) =>
          val googleApiClient = createGoogleDriveClient(acc.name)
          clientStatuses = clientStatuses.copy(
            driveApiClient = Some(googleApiClient),
            email = Some(acc.name))
          requestAndroidMarketPermission(acc, googleApiClient)
        case _ => actions.showErrorSelectUser().run
      }
    } else {
      actions.showErrorAcceptTerms().run
    }
  }

  def generateCollections(maybeKey: Option[String]): Unit =
    contextWrapper.original.get match {
      case Some(activity) =>
        val intent = createIntent(activity, classOf[CreateCollectionService])
        intent.putExtra(CreateCollectionService.cloudIdKey, maybeKey.getOrElse(CreateCollectionService.newConfiguration))
        activity.startService(intent)
        actions.goToWizard().run
      case _ =>
    }

  def finishWizard(): Unit =
    contextWrapper.original.get match {
      case Some(activity) =>
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
      case _ =>
    }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean = {

    def tryToConnect(): Unit = clientStatuses.driveApiClient foreach (_.connect())

    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
        true
      case (`resolveGooglePlayConnection`, _) =>
        connectionError()
        true
      case (`resolveConnectedUser`, Activity.RESULT_OK) =>

        val mailTokenId = Option(Auth.GoogleSignInApi.getSignInResultFromIntent(data)) match {
          case Some(result) if result.isSuccess =>
            Option(result.getSignInAccount) flatMap (acct => Option(acct.getIdToken))
          case _ => None
        }

        clientStatuses = clientStatuses.copy(mailTokenId = mailTokenId)

        clientStatuses.plusApiClient match {
          case Some(apiClient) => apiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
          case None => onDriveConnected(javaNull)
        }

        true
      case (`resolveConnectedUser`, _) =>
        connectionError()
        true
      case _ => false
    }
  }

  def stop(): Unit = {
    List(clientStatuses.driveApiClient, clientStatuses.plusApiClient).flatten foreach {
      client => Try(client.disconnect())
    }
  }

  override def onDriveConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit = {}

  override def onDriveConnected(bundle: Bundle): Unit = {
    clientStatuses.email match {
      case Some(email) =>
        val client = createGooglePlusClient(email)
        clientStatuses = clientStatuses.copy(plusApiClient = Some(client))
        signIn(client)
      case None => actions.goToUser().run
    }
  }

  override def onDriveConnectionFailed(connectionResult: ConnectionResult): Unit =
    onConnectionFailed(connectionResult)

  override def onPlusConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit = {}

  override def onPlusConnectionFailed(connectionResult: ConnectionResult): Unit = {
    onConnectionFailed(connectionResult)
  }

  override def onPlusConnected(bundle: Bundle): Unit = {

    def error(throwable: Throwable): Unit = throwable match {
      case ex: SocialProfileProcessException if ex.recoverable => onDriveConnected(javaNull)
      case _ => actions.showErrorConnectingGoogle().run
    }

    clientStatuses.plusApiClient match {
      case Some(apiClient) =>
        val googlePlusProcess = di.createGooglePlusProcess(apiClient)
        Task.fork(googlePlusProcess.updateUserProfile().run).resolveAsync(
          onResult = (profileName) =>
            loadDevices(clientStatuses.driveApiClient, profileName, clientStatuses.email, clientStatuses.androidMarketToken),
          onException = error,
          onPreTask = () => actions.showLoading().run
        )
      case None => actions.goToUser().run
    }
  }

  protected def createIntent[T](activity: Activity, targetClass: Class[T]): Intent = new Intent(activity, targetClass)

  private[this] def onConnectionFailed(connectionResult: ConnectionResult): Unit = {

    def withActivity(f: (AppCompatActivity => Unit)) =
      contextWrapper.original.get match {
        case Some(activity: AppCompatActivity) => f(activity)
        case _ =>
      }

    if (connectionResult.hasResolution) {
      withActivity { activity =>
        Try(connectionResult.startResolutionForResult(activity, resolveGooglePlayConnection)) match {
          case Failure(e) => connectionError()
          case _ =>
        }
      }
    } else if (
      connectionResult.getErrorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
      connectionResult.getErrorCode == ConnectionResult.SERVICE_MISSING ||
      connectionResult.getErrorCode == ConnectionResult.SERVICE_DISABLED) {
      actions.goToUser().run
      withActivity { activity =>
        GoogleApiAvailability.getInstance()
          .getErrorDialog(activity, connectionResult.getErrorCode, resolveGooglePlayConnection)
          .show()
      }
    } else {
      connectionError()
    }
  }

  private[this] def requestToken(
    account: Account,
    scopes: String,
    client: GoogleApiClient): ServiceDef2[String, AuthTokenException with AuthTokenOperationCancelledException] = Service {
      Task {
        \/.fromTryCatchNonFatal {
          val result = accountManager.getAuthToken(account, scopes, javaNull, contextWrapper.getOriginal, javaNull, javaNull).getResult
          result.getString(AccountManager.KEY_AUTHTOKEN)
        } match {
          case \/-(x) => Result.answer(x)
          case -\/(e: OperationCanceledException) => Errata(Seq((
            implicitly[ClassTag[AuthTokenOperationCancelledException]],
            (e.getMessage, AuthTokenOperationCancelledExceptionImpl(e.getMessage, Some(e))))))
          case -\/(e) => Errata(Seq((
            implicitly[ClassTag[AuthTokenException]],
            (e.getMessage, AuthTokenExceptionImpl(e.getMessage, Some(e))))))
        }
      }
    }

  private[this] def connectionError(): Unit = actions.showErrorConnectingGoogle().run

  private[this] def requestAndroidMarketPermission(
    account: Account,
    client: GoogleApiClient): Unit = {

    def invalidateToken(): Unit = {
      clientStatuses.androidMarketToken foreach { token =>
        accountManager.invalidateAuthToken(accountType, token)
      }
    }

    invalidateToken()
    val scopes = resGetString(R.string.android_market_oauth_scopes)
    Task.fork(requestToken(account, scopes, client).run).resolveAsync(
      onResult = (token: String) => {
        clientStatuses = clientStatuses.copy(androidMarketToken = Some(token))
        requestGooglePermission(account, client)
      },
      onException = (ex: Throwable) => ex match {
        case ex: AuthTokenOperationCancelledException =>
          showErrorDialog(
            message = R.string.errorAndroidMarketPermissionNotAccepted,
            action = () => requestAndroidMarketPermission(account, client))
        case ex: Throwable => actions.showErrorConnectingGoogle().run
      },
      onPreTask = () => actions.showLoading().run)
  }

  private[this] def requestGooglePermission(
    account: Account,
    client: GoogleApiClient): Unit = {
    val scopes = resGetString(R.string.profile_and_drive_oauth_scopes)
    Task.fork(requestToken(account, scopes, client).run).resolveAsync(
      onResult = (_) => {
        clientStatuses.driveApiClient foreach (_.connect())
      },
      onException = (ex: Throwable) => ex match {
        case ex: AuthTokenOperationCancelledException =>
          showErrorDialog(
            message = R.string.errorGooglePermissionNotAccepted,
            action = () => requestGooglePermission(account, client))
        case ex: Throwable => actions.showErrorConnectingGoogle()
      },
      onPreTask = () => actions.showLoading().run)
  }

  private[this] def showErrorDialog(message: Int, action: () => Unit): Unit =
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val fm = activity.getSupportFragmentManager
        val ft = fm.beginTransaction()
        Option(fm.findFragmentByTag(tagDialog)) foreach ft.remove
        val dialog = new AlertDialogFragment(
          message = message,
          positiveAction = action,
          negativeAction = () => actions.goToUser().run)
        ft.add(dialog, tagDialog).addToBackStack(javaNull)
        ft.commitAllowingStateLoss()
      case _ =>
    }

  private[this] def loadDevices(
    maybeClient: Option[GoogleApiClient],
    maybeProfileName: Option[String],
    maybeEmail: Option[String],
    maybeAndroidMarketToken: Option[String]): Unit = {

    def storeOnCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageDevices: Seq[CloudStorageDeviceData]) = Service {
      val tasks = cloudStorageDevices map (d => cloudStorageProcess.createCloudStorageDevice(d).run)
      Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r }))
    }

    def fakeUserConfigException: ServiceDef2[Unit, UserConfigException] = Service(Task(Answer(())))

    def verifyAndUpdate(
      cloudStorageProcess: CloudStorageProcess,
      email: String,
      cloudStorageResources: Seq[CloudStorageDeviceSummary]) =
      if (cloudStorageResources.isEmpty) {
        for {
          userInfo <- di.userConfigProcess.getUserInfo
          cloudStorageDevices <- storeOnCloud(cloudStorageProcess, userInfo.devices map toCloudStorageDevice)
          (maybeUserDevice, devices) <- cloudStorageProcess.prepareForActualDevice(cloudStorageDevices)
        } yield {
          UserCloudDevices(
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      } else {
        for {
          (maybeUserDevice, devices) <- cloudStorageProcess.prepareForActualDevice(cloudStorageResources)
          _ <- fakeUserConfigException
        } yield {
          UserCloudDevices(
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      }

    def loadCloudDevices(
      client: GoogleApiClient,
      email: String,
      androidMarketToken: String) = {
      val cloudStorageProcess = di.createCloudStorageProcess(client)
      for {
        response <- di.userProcess.signIn(email, Build.MODEL, androidMarketToken, Seq(resGetString(R.string.android_market_oauth_scopes)))
        cloudStorageResources <- cloudStorageProcess.getCloudStorageDevices
        userCloudDevices <- verifyAndUpdate(cloudStorageProcess, email, cloudStorageResources).resolveTo(UserCloudDevices(email, None, Seq.empty))
      } yield userCloudDevices

    }

    (for {
      client <- maybeClient
      email <- maybeEmail
      userPermissions <- maybeAndroidMarketToken
    } yield {
      Task.fork(loadCloudDevices(client, email, userPermissions).run).resolveAsyncUi(
        onPreTask = () => actions.showLoading(),
        onResult = (devices: UserCloudDevices) => actions.showDevices(devices),
        onException = (ex: Throwable) => ex match {
          case ex: UserException => actions.showErrorLoginUser()
          case ex: UserConfigException => actions.showErrorLoginUser()
          case _ => actions.showErrorConnectingGoogle()
        })
    }) getOrElse actions.showErrorConnectingGoogle().run
  }

}

object Statuses {

  case class WizardPresenterStatuses(
    driveApiClient: Option[GoogleApiClient] = None,
    plusApiClient: Option[GoogleApiClient] = None,
    email: Option[String] = None,
    androidMarketToken: Option[String] = None,
    mailTokenId: Option[String] = None)

}

trait WizardUiActions {

  def initialize(accounts: Seq[Account]): Ui[Any]

  def goToUser(): Ui[Any]

  def goToWizard(): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorConnectingGoogle(): Ui[Any]

  def showErrorSelectUser(): Ui[Any]

  def showErrorAcceptTerms(): Ui[Any]

  def showErrorLoginUser(): Ui[Any]

  def showDevices(devices: UserCloudDevices): Ui[Any]

  def showDiveIn(): Ui[Any]
}