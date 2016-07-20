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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{AppLog, Presenter}
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevices, UserPermissions}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDevice, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.cloud.{CloudStorageProcess, CloudStorageProcessException, ImplicitsCloudStorageProcessExceptions}
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.userconfig.UserConfigException
import NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserInfo
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import com.google.android.gms.common.api.GoogleApiClient
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

  val googleKeyPreferences = "__google_auth__"

  val googleKeyToken = "__google_token__"

  val tagDialog = "dialog-not-accepted"

  lazy val accounts: Seq[Account] = accountManager.getAccountsByType(accountType).toSeq

  lazy val accountManager: AccountManager = AccountManager.get(contextSupport.context)

  lazy val preferences = contextWrapper.bestAvailable.getSharedPreferences(googleKeyPreferences, Context.MODE_PRIVATE)

  private[this] def getToken: Option[String] = Option(preferences.getString(googleKeyToken, javaNull))

  private[this] def setToken(token: String) = preferences.edit.putString(googleKeyToken, token).apply()

  var clientStatuses = WizardPresenterStatuses()

  def initialize(): Unit = actions.initialize(accounts).run

  def goToUser(): Unit = actions.goToUser().run

  def goToWizard(): Unit = actions.goToWizard().run

  def connectAccount(username: String, termsAccept: Boolean): Unit = if (termsAccept) {
    getAccount(username) match {
      case Some(acc) =>
        val googleApiClient = createGoogleDriveClient(acc.name)
        clientStatuses = clientStatuses.copy(
          driveApiClient = Some(googleApiClient),
          username = Some(acc.name))
        requestAndroidMarketPermission(acc, googleApiClient)
      case _ => actions.showErrorSelectUser().run
    }
  } else {
    actions.showErrorAcceptTerms().run
  }

  def saveCurrentDevice(): Unit =
    (clientStatuses.driveApiClient, clientStatuses.username) match {
      case (Some(client), _) =>
        Task.fork(storeDevice(client).run).resolveAsyncUi(
          onResult = (_) => actions.showDiveIn(),
          onException = (_) => actions.showDiveIn())
      case (_, Some(account)) =>
        connectAccount(account, termsAccept = true)
      case _ =>
        actions.goToUser().run
    }

  def generateCollections(maybeKey: Option[String]): Unit =
    contextWrapper.original.get match {
      case Some(activity) =>
        val intent = createIntent(activity, classOf[CreateCollectionService])
        intent.putExtra(CreateCollectionService.keyDevice, maybeKey.getOrElse(CreateCollectionService.newConfiguration))
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

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean =
    (requestCode, resultCode) match {
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnect()
        true
      case (`resolveGooglePlayConnection`, _) =>
        connectionError()
        true
      case (`resolveConnectedUser`, Activity.RESULT_OK) =>
        clientStatuses.plusApiClient match {
          case Some(apiClient) => apiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)
          case None => onDriveConnected(javaNull)
        }
        true
      case (`resolveConnectedUser`, _) =>
        // TODO - Failed to SignIn
        loadDevices(clientStatuses.driveApiClient, clientStatuses.username, clientStatuses.userPermissions)
        true
      case _ => false
    }

  def stop(): Unit = {
    List(clientStatuses.driveApiClient, clientStatuses.plusApiClient).flatten foreach {
      client => Try(client.disconnect())
    }
  }

  override def onDriveConnectionSuspended(connectionSuspendedCause: ConnectionSuspendedCause): Unit = {}

  override def onDriveConnected(bundle: Bundle): Unit = {
    clientStatuses.username match {
      case Some(username) =>
        val client = createGooglePlusClient(username)
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

  override def onPlusConnected(bundle: Bundle): Unit =
    clientStatuses.plusApiClient match {
      case Some(apiClient) =>
        val googlePlusProcess = di.createGooglePlusProcess(apiClient)
        Task.fork(googlePlusProcess.updateUserProfile().run).resolveAsync(
          onResult = (_) => loadDevices(clientStatuses.driveApiClient, clientStatuses.username, clientStatuses.userPermissions),
          onException = (_) => onDriveConnected(javaNull),
          onPreTask = () => actions.showLoading().run
        )
      case None => actions.goToUser().run
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
    } else if (connectionResult.getErrorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
      withActivity { activity =>
        GoogleApiAvailability.getInstance()
          .getErrorDialog(activity, connectionResult.getErrorCode, resolveGooglePlayConnection)
          .show()
      }
    } else {
      connectionError()
    }
  }

  private[this] def getAccount(username: String): Option[Account] = accounts find (_.name == username)

  private[this] def requestUserPermissions(
    account: Account,
    scopes: String,
    client: GoogleApiClient): ServiceDef2[UserPermissions, AuthTokenException with AuthTokenOperationCancelledException] = {
    for {
      token <- getAuthToken(accountManager, account, scopes)
    } yield UserPermissions(token, Seq(scopes))
  }

  private[this] def invalidateToken(): Unit = {
    getToken foreach { token =>
      accountManager.invalidateAuthToken(accountType, token)
      setToken(javaNull)
    }
  }

  private[this] def loadCloudDevices(
    client: GoogleApiClient,
    username: String,
    userPermissions: UserPermissions
  ): ServiceDef2[UserCloudDevices, UserException with UserConfigException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    for {
      response <- di.userProcess.signIn(username, Build.MODEL, userPermissions.token, userPermissions.oauthScopes)
      cloudStorageResources <- cloudStorageProcess.getCloudStorageDevices
      userCloudDevices <- verifyAndUpdate(cloudStorageProcess, username, cloudStorageResources).resolveTo(UserCloudDevices(username, Seq.empty))
    } yield userCloudDevices

  }

  private[this] def connectionError(): Unit = actions.showErrorConnectingGoogle().run

  private[this] def tryToConnect(): Unit = clientStatuses.driveApiClient foreach (_.connect())

  private[this] def requestAndroidMarketPermission(
    account: Account,
    client: GoogleApiClient): Unit = {
    invalidateToken()
    val scopes = "androidmarket"
    Task.fork(requestUserPermissions(account, scopes, client).run).resolveAsync(
      onResult = (permissions: UserPermissions) => {
        setToken(permissions.token)
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
    val scopes = resGetString(R.string.oauth_scopes)
    Task.fork(requestUserPermissions(account, scopes, client).run).resolveAsync(
      onResult = (permissions: UserPermissions) => {
        clientStatuses = clientStatuses.copy(userPermissions = Some(permissions))
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
    maybeUsername: Option[String],
    maybeUserPermissions: Option[UserPermissions]
  ): Unit = {
    (for {
      client <- maybeClient
      username <- maybeUsername
      userPermissions <- maybeUserPermissions
    } yield {
      Task.fork(loadCloudDevices(client, username, userPermissions).run).resolveAsyncUi(
        onPreTask = () => actions.showLoading(),
        onResult = (devices: UserCloudDevices) => actions.showDevices(devices),
        onException = (ex: Throwable) => ex match {
          case ex: UserException => actions.showErrorLoginUser()
          case ex: UserConfigException => actions.showErrorLoginUser()
          case _ => actions.showErrorConnectingGoogle()
        })
    }) getOrElse actions.showErrorConnectingGoogle().run
  }

  private[this] def storeDevice(client: GoogleApiClient): ServiceDef2[Unit, CollectionException with MomentException with CloudStorageProcessException] = {
    val cloudStorageProcess = di.createCloudStorageProcess(client)
    for {
      collections <- di.collectionProcess.getCollections
      moments <- di.momentProcess.getMoments
      _ <- cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
        collections = addMomentsToCollections(collections, moments),
        moments = moments.filter(_.collectionId.isEmpty) map toCloudStorageMoment)
    } yield ()
  }

  private[this] def addMomentsToCollections(collections: Seq[Collection], moments: Seq[Moment]) =
    collections map (collection => toCloudStorageCollection(collection, moments.find(_.collectionId == Option(collection.id))))

  private[this] def verifyAndUpdate(
    cloudStorageProcess: CloudStorageProcess,
    name: String,
    cloudStorageResources: Seq[CloudStorageDeviceSummary]
  ): ServiceDef2[UserCloudDevices, UserConfigException with CloudStorageProcessException] = {
    if (cloudStorageResources.isEmpty) {
      for {
        userInfo <- di.userConfigProcess.getUserInfo
        cloudStorageDevices = userInfo.devices map toCloudStorageDevice
        _ <- storeOnCloud(cloudStorageProcess, cloudStorageDevices)
      } yield UserCloudDevices(userInfo.name, cloudStorageDevices)
    } else {
      for {
        devices <- loadFromCloud(cloudStorageProcess, cloudStorageResources)
        _ <- fakeUserConfigException
      } yield UserCloudDevices(name, devices.flatten)
    }
  }

  private[this] def storeOnCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageDevices: Seq[CloudStorageDevice]) = Service {
    val tasks = cloudStorageDevices map (d => cloudStorageProcess.createOrUpdateCloudStorageDevice(d).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect { case Answer(r) => r }))
  }

  private[this] def loadFromCloud(cloudStorageProcess: CloudStorageProcess, cloudStorageResources: Seq[CloudStorageDeviceSummary]) = Service {
    val tasks = cloudStorageResources map (r => cloudStorageProcess.getCloudStorageDevice(r.resourceId).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CloudStorageProcessException](c.collect {
      case Answer(r) => Some(r)
      case e@Errata(_) =>
        AppLog.printErrorTaskMessage(s"Error parsing cloud device", e.exceptions)
        None
    }))
  }

  private[this] def fakeUserConfigException: ServiceDef2[Unit, UserConfigException] = Service(Task(Answer()))

  private[this] def getAuthToken(
    accountManager: AccountManager,
    account: Account,
    scopes: String
  ): ServiceDef2[String, AuthTokenException with AuthTokenOperationCancelledException] = Service {
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
}

object Statuses {

  case class WizardPresenterStatuses(
    driveApiClient: Option[GoogleApiClient] = None,
    plusApiClient: Option[GoogleApiClient] = None,
    username: Option[String] = None,
    userPermissions: Option[UserPermissions] = None)

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