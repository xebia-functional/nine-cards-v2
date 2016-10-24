package cards.nine.app.ui.wizard.jobs

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Build
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.wizard.models.UserCloudDevices
import cards.nine.app.ui.wizard.{WizardGoogleTokenRequestCancelledException, WizardMarketTokenRequestCancelledException}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{CloudStorageDeviceData, CloudStorageDeviceSummary, UserV1Device}
import cards.nine.process.accounts.{FineLocation, UserAccountsProcessOperationCancelledException}
import cards.nine.process.cloud.Conversions
import cards.nine.process.userv1.UserV1ConfigurationException
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.{AccountPicker, ConnectionResult, GoogleApiAvailability}
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task
import cats.implicits._
import com.fortysevendeg.macroid.extras.DeviceVersion.Marshmallow

import scala.util.{Failure, Success, Try}

/**
  * This class manages all jobs over the Wizard. This is the ideal flow:
  *  - Activity calls to 'Job.initialize'
  *   + Job calls to 'UiAction.initialize'
  *  - UiAction calls to 'Job.connectAccount'
  *   + Job starts a new activity for getting the account
  *  - Activity calls to 'Job.activityResult'
  *   + Job calls to 'Job.requestAndroidMarketPermission' that fetches the Android Market token
  *   + Job calls to 'Job.requestGooglePermission' that fetches the Google Profile token
  *   + Job calls to 'Job.tryToConnectDriveApiClient' that connects the Drive client
  *  - GoogleDriveApiClientProvider calls 'Job.onDriveConnected'
  *   + Job execute 'Job.googleSignIn'
  *   + Job starts a new activity for Google Profile sign in
  *  - Activity calls to 'Job.activityResult'
  *   + Job fetches the tokenId and calls to 'Job.tryToConnectGoogleApiClient'
  *  - GooglePlusApiClientProvider calls to 'Job.onPlusConnected'
  *   + Job update the user profile information and calls to 'Job.loadDevices'
  *   + Job calls to 'UiAction.showDevices' with the loaded devices
  *  - UiAction calls to 'Job.deviceSelected'
  *   + Job asks for Location permissions and calls to 'Job.generateCollections'
  *   + Job starts the service
  *  - Activity calls to 'Job.serviceFinished'
  *   + Job calls to 'UiAction.showDiveIn'
  *  - UiAction calls to 'Job.finishWizard'
  *   + Job set the result RESULT_OK and finish the activity
  */
class WizardJobs(wizardUiActions: WizardUiActions, visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions {

  val accountType = "com.google"

  val tagDialog = "wizard-dialog"

  var clientStatuses = WizardJobsStatuses()

  def initialize(): TaskService[Unit] =
    for {
      _ <- wizardUiActions.initialize()
      _ <- visibilityUiActions.goToUser()
    } yield()

  def stop(): TaskService[Unit] = {

    def tryToClose(maybeClient: Option[GoogleApiClient]): Try[Unit] =
      maybeClient map (c => Try(c.disconnect())) getOrElse Success((): Unit)

    TaskService {
      Task {
        List(clientStatuses.driveApiClient, clientStatuses.plusApiClient) map tryToClose collect {
          case Failure(e) => e
        } match {
          case Nil => Right((): Unit)
          case list =>
            val message = s"Error disconnecting clients:\n ${list.map(_.getMessage).mkString(" \n")}"
            Left(UiException(message, cause = None))
        }
      }
    }
  }

  def connectAccount(termsAccepted: Boolean): TaskService[Unit] =
    if (termsAccepted) {
      val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(contextSupport.context)
      if (resultCode == ConnectionResult.SUCCESS) {
        val intent = Marshmallow ifSupportedThen {
          AccountManager
            .newChooseAccountIntent(javaNull, javaNull, Array(accountType), javaNull, javaNull, javaNull, javaNull)
        } getOrElse {
          AccountPicker
            .newChooseAccountIntent(javaNull, javaNull, Array(accountType), false, javaNull, javaNull, javaNull, javaNull)
        }
        uiStartIntentForResult(intent, RequestCodes.selectAccount).toService
      } else {
        onConnectionFailed(None, resultCode)
      }
    } else {
      wizardUiActions.showErrorAcceptTerms()
    }

  def deviceSelected(maybeKey: Option[String]): TaskService[Unit] =
    for {
      _ <- TaskService(Task(Right(clientStatuses = clientStatuses.copy(deviceKey = maybeKey))))
      havePermission <- di.userAccountsProcess.havePermission(FineLocation)
      _ <- if (havePermission.result) generateCollections(maybeKey) else requestPermissions()
    } yield ()

  def requestPermissions(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.wizardPermissions, FineLocation)

  def permissionDialogCancelled(): TaskService[Unit] =
    generateCollections(clientStatuses.deviceKey)

  def finishWizard(): TaskService[Unit] = TaskService {
    CatchAll[UiException] {
      activityContextSupport.getActivity match {
        case Some(activity) =>
          activity.setResult(Activity.RESULT_OK)
          activity.finish()
        case None => throw new NullPointerException("Activity instance not found")
      }
    }
  }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): TaskService[Unit] =
    (requestCode, resultCode) match {
      case (RequestCodes.selectAccount, Activity.RESULT_OK) =>
        clientStatuses = clientStatuses.copy(email = readStringValue(data, AccountManager.KEY_ACCOUNT_NAME))
        requestAndroidMarketPermission()
      case (RequestCodes.selectAccount, _) =>
        wizardUiActions.showSelectAccountDialog()
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnectDriveApiClient()
      case (`resolveGooglePlayConnection`, _) =>
        wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
      case (`resolveConnectedUser`, Activity.RESULT_OK) =>
        val mailTokenId = Option(Auth.GoogleSignInApi.getSignInResultFromIntent(data)) match {
          case Some(result) if result.isSuccess =>
            Option(result.getSignInAccount) flatMap (acct => Option(acct.getIdToken))
          case _ => None
        }
        clientStatuses = clientStatuses.copy(mailTokenId = mailTokenId)
        tryToConnectGoogleApiClient()
      case (`resolveConnectedUser`, _) =>
        wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
      case _ => TaskService(Task(Right((): Unit)))
    }

  def requestAndroidMarketPermission(): TaskService[Unit] = {

    def invalidateToken(): TaskService[Unit] =
      clientStatuses.androidMarketToken match {
        case Some(token) => di.userAccountsProcess.invalidateToken(token)
        case None => TaskService(Task(Right((): Unit)))
      }

    def storeDriveApiClient(driveApiClient: GoogleApiClient): Unit =
      clientStatuses = clientStatuses.copy(driveApiClient = Option(driveApiClient))

    def storeAndroidMarketToken(token: String): Unit =
      clientStatuses = clientStatuses.copy(androidMarketToken = Option(token))

    clientStatuses.email match {
      case Some(account) =>
        for {
          googleApiClient <- di.cloudStorageProcess.createCloudStorageClient(account)
          _ = storeDriveApiClient(googleApiClient)
          _ <- visibilityUiActions.showLoadingConnectingWithGoogle()
          _ <- invalidateToken()
          token <- di.userAccountsProcess
            .getAuthToken(account, resGetString(R.string.android_market_oauth_scopes))
            .resolveLeft {
              case ex: UserAccountsProcessOperationCancelledException =>
                Left(WizardMarketTokenRequestCancelledException(ex.getMessage, Some(ex)))
              case ex: Throwable => Left(ex)
            }
          _ = storeAndroidMarketToken(token)
          _ <- requestGooglePermission()
        } yield ()
      case None => visibilityUiActions.goToUser()
    }

  }

  def requestGooglePermission(): TaskService[Unit] =
    clientStatuses.email match {
      case Some(account) =>
        for {
          _ <- visibilityUiActions.showLoadingRequestGooglePermission()
          _ <- di.userAccountsProcess
            .getAuthToken(account, resGetString(R.string.profile_and_drive_oauth_scopes))
            .resolveLeft {
              case ex: UserAccountsProcessOperationCancelledException =>
                Left(WizardGoogleTokenRequestCancelledException(ex.getMessage, Some(ex)))
              case ex: Throwable => Left(ex)
            }
          _ <- tryToConnectDriveApiClient()
        } yield ()
      case None => visibilityUiActions.goToUser()
    }

  def requestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): TaskService[Unit] = {

    def generateOrRequest(hasPermission: Boolean, shouldRequest: Boolean): TaskService[Unit] =
      if (hasPermission || !shouldRequest) {
        generateCollections(clientStatuses.deviceKey)
      } else {
        wizardUiActions.showRequestPermissionsDialog()
      }

    if (requestCode == RequestCodes.wizardPermissions) {
      for {
        result <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
        shouldRequest <- di.userAccountsProcess.shouldRequestPermission(FineLocation)
        _ <- generateOrRequest(result.exists(_.hasPermission(FineLocation)), shouldRequest.result)
      } yield ()
    } else {
      TaskService.empty
    }
  }

  def errorOperationMarketTokenCancelled(): TaskService[Unit] = wizardUiActions.showMarketPermissionDialog()

  def errorOperationGoogleTokenCancelled(): TaskService[Unit] = wizardUiActions.showGooglePermissionDialog()

  def driveConnected(): TaskService[Unit] = googleSignIn()

  def driveConnectionFailed(connectionResult: ConnectionResult): TaskService[Unit] =
    onConnectionFailed(connectionResult)

  def plusConnected(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(apiClient) =>
        for {
          _ <- visibilityUiActions.showLoadingConnectingWithGooglePlus()
          maybeProfileName <- di.socialProfileProcess.updateUserProfile(apiClient)
          _ <- loadDevices(maybeProfileName)
        } yield ()
      case None => visibilityUiActions.goToUser()
    }

  def plusConnectionFailed(connectionResult: ConnectionResult): TaskService[Unit] =
    onConnectionFailed(connectionResult)

  def googleSignIn(): TaskService[Unit] = {

    def storePlusApiClient(plusApiClient: GoogleApiClient): Unit =
      clientStatuses = clientStatuses.copy(plusApiClient = Option(plusApiClient))

    def signInIntentService(plusApiClient: GoogleApiClient): TaskService[Unit] = {
      val signInIntent = Auth.GoogleSignInApi.getSignInIntent(plusApiClient)
      uiStartIntentForResult(signInIntent, resolveConnectedUser).toService
    }

    clientStatuses.email match {
      case Some(email) =>
        for {
          plusApiClient <- di.socialProfileProcess.createSocialProfileClient(
            clientId = contextSupport.getResources.getString(R.string.api_v2_client_id),
            account = email)
          _ = storePlusApiClient(plusApiClient)
          _ <- signInIntentService(plusApiClient)
        } yield ()
      case None => visibilityUiActions.goToUser()
    }
  }

  def googleDriveClient: TaskService[GoogleApiClient] = clientStatuses.driveApiClient match {
    case Some(client) if client.isConnected => TaskService.right(client)
    case Some(_) => TaskService.left(JobException("Client not connected"))
    case _ => TaskService.left(JobException("Client not available"))
  }

  private[this] def onConnectionFailed(result: ConnectionResult): TaskService[Unit] = {
    val maybeResult = Option(result)
    val errorCode = maybeResult.map(_.getErrorCode) getOrElse ConnectionResult.CANCELED
    onConnectionFailed(maybeResult, errorCode)
  }

  private[this] def onConnectionFailed(maybeResult: Option[ConnectionResult], errorCode: Int): TaskService[Unit] = {

    def showGoogleApiErrorDialog: TaskService[Unit] = withActivity { activity =>
      Ui(GoogleApiAvailability.getInstance()
        .getErrorDialog(activity, errorCode, resolveGooglePlayConnection)
        .show()).toService
    }

    def shouldShowDialog: Boolean = errorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
      errorCode == ConnectionResult.SERVICE_MISSING ||
      errorCode == ConnectionResult.SERVICE_DISABLED

    maybeResult match {
      case Some(result) if result.hasResolution =>
        withActivityTask { activity =>
          result.startResolutionForResult(activity, resolveGooglePlayConnection)
        }
      case _ if shouldShowDialog =>
        for {
          _ <- visibilityUiActions.goToUser()
          _ <- showGoogleApiErrorDialog
        } yield ()
      case _ =>
        wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
    }
  }

  private[this] def generateCollections(maybeKey: Option[String]): TaskService[Unit] = {
    maybeKey match {
      case Some(key) => visibilityUiActions.goToWizard(key)
      case _ => visibilityUiActions.goToNewConfiguration()
    }
  }

  private[this] def tryToConnectDriveApiClient(): TaskService[Unit] =
    clientStatuses.driveApiClient match {
      case Some(client) => TaskService(CatchAll[UiException](client.connect()))
      case None => requestAndroidMarketPermission()
    }

  private[this] def tryToConnectGoogleApiClient(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(client) => TaskService(CatchAll[UiException](client.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)))
      case None => googleSignIn()
    }

  private[this] def loadDevices(maybeProfileName: Option[String]): TaskService[Unit] = {

    def storeOnCloud(client: GoogleApiClient, cloudStorageDevices: Seq[CloudStorageDeviceData]) =
      TaskService {
        val tasks = cloudStorageDevices map { deviceData =>
          di.cloudStorageProcess.createOrUpdateCloudStorageDevice(
            client = client,
            maybeCloudId = None,
            cloudStorageDevice = deviceData).value
        }
        Task.gatherUnordered(tasks) map { list =>
          Right(list.collect {
            case Right(r) => r
          })
        }
      }

    // If we found some error when connecting to Backend V1 we just return an empty collection of devices
    def loadDevicesFromV1(): TaskService[Seq[UserV1Device]] =
      di.userV1Process.getUserInfo(Build.MODEL, Seq(resGetString(R.string.android_market_oauth_scopes)))
        .map(_.devices)
        .resolveRight {
          case e: UserV1ConfigurationException =>
            AppLog.info("Invalid configuration for backend V1")
            Right(Seq.empty)
          case e => Right(Seq.empty)
        }

    def verifyAndUpdate(
      client: GoogleApiClient,
      email: String,
      cloudStorageResources: Seq[CloudStorageDeviceSummary]) = {

      import Conversions._

      if (cloudStorageResources.isEmpty) {
        for {
          userInfoDevices <- loadDevicesFromV1()
          cloudStorageDevices <- storeOnCloud(client, userInfoDevices map toCloudStorageDevice)
          actualDevice <- di.cloudStorageProcess.prepareForActualDevice(client, cloudStorageDevices)
          (maybeUserDevice, devices) = actualDevice
        } yield {
          UserCloudDevices(
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      } else {
        for {
          actualDevice <- di.cloudStorageProcess.prepareForActualDevice(client, cloudStorageResources)
          (maybeUserDevice, devices) = actualDevice
        } yield {
          UserCloudDevices(
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      }
    }

    def loadCloudDevices(
      client: GoogleApiClient,
      email: String,
      androidMarketToken: String,
      emailTokenId: String) = {
      for {
        _ <- di.userProcess.signIn(email, androidMarketToken, emailTokenId)
        cloudStorageResources <- di.cloudStorageProcess.getCloudStorageDevices(client)
        userCloudDevices <- verifyAndUpdate(client, email, cloudStorageResources).resolveLeftTo(UserCloudDevices(email, None, Seq.empty))
      } yield userCloudDevices

    }

    clientStatuses match {
      case WizardJobsStatuses(_, Some(client), _, Some(email), Some(androidMarketToken), Some(emailTokenId)) =>
        for {
          _ <- visibilityUiActions.showLoadingDevices()
          devices <- loadCloudDevices(client, email, androidMarketToken, emailTokenId)
          _ <- wizardUiActions.showDevices(devices)
        } yield ()
      case _ => wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
    }

  }

}

case class WizardJobsStatuses(
  deviceKey: Option[String] = None,
  driveApiClient: Option[GoogleApiClient] = None,
  plusApiClient: Option[GoogleApiClient] = None,
  email: Option[String] = None,
  androidMarketToken: Option[String] = None,
  mailTokenId: Option[String] = None)