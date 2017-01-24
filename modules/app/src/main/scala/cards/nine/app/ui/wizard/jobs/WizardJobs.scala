/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.wizard.jobs

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.preferences.commons.{GoogleDriveEmptyDeviceWizard, V1EmptyDeviceWizard}
import cards.nine.app.ui.wizard.jobs.uiactions.{VisibilityUiActions, WizardUiActions}
import cards.nine.app.ui.wizard.models.{
  GoogleDriveDeviceType,
  NoFoundDeviceType,
  UserCloudDevices,
  V1DeviceType
}
import cards.nine.app.ui.wizard.{
  WizardGoogleTokenRequestCancelledException,
  WizardMarketTokenRequestCancelledException
}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.FineLocation
import cards.nine.models.{
  CloudStorageDeviceData,
  CloudStorageDeviceSummary,
  PackagesByCategory,
  UserV1Device
}
import cards.nine.process.accounts.UserAccountsProcessOperationCancelledException
import cards.nine.process.cloud.Conversions
import cards.nine.process.userv1.UserV1ConfigurationException
import cats.data.EitherT
import cats.implicits._
import macroid.extras.DeviceVersion.Marshmallow
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.{AccountPicker, ConnectionResult, GoogleApiAvailability}
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task

import scala.util.{Failure, Success, Try}

/**
 * This class manages all jobs over the Wizard. This is the ideal flow:
 * - Activity calls to 'Job.initialize'
 * + Job calls to 'UiAction.initialize'
 * - UiAction calls to 'Job.connectAccount'
 * + Job starts a new activity for getting the account
 * - Activity calls to 'Job.activityResult'
 * + Job calls to 'Job.requestAndroidMarketPermission' that fetches the Android Market token
 * + Job calls to 'Job.requestGooglePermission' that fetches the Google Profile token
 * + Job calls to 'Job.tryToConnectDriveApiClient' that connects the Drive client
 * - GoogleDriveApiClientProvider calls 'Job.onDriveConnected'
 * + Job execute 'Job.googleSignIn'
 * + Job starts a new activity for Google Profile sign in
 * - Activity calls to 'Job.activityResult'
 * + Job fetches the tokenId and calls to 'Job.tryToConnectGoogleApiClient'
 * - GooglePlusApiClientProvider calls to 'Job.onPlusConnected'
 * + Job update the user profile information and calls to 'Job.loadDevices'
 * + Job calls to 'UiAction.showDevices' with the loaded devices
 * - UiAction calls to 'Job.deviceSelected'
 * + Job asks for Location permissions and calls to 'Job.generateCollections'
 * + Job starts the service
 * - Activity calls to 'Job.serviceFinished'
 * + Job calls to 'UiAction.showDiveIn'
 * - UiAction calls to 'Job.finishWizard'
 * + Job set the result RESULT_OK and finish the activity
 */
@SuppressLint(Array("NewApi"))
class WizardJobs(
    val wizardUiActions: WizardUiActions,
    val visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with ImplicitsUiExceptions {

  val accountType = "com.google"

  val tagDialog = "wizard-dialog"

  var clientStatuses = WizardJobsStatuses()

  def initialize(): TaskService[Unit] =
    for {
      _ <- wizardUiActions.initialize()
      _ <- visibilityUiActions.goToUser()
    } yield ()

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
            val message =
              s"Error disconnecting clients:\n ${list.map(_.getMessage).mkString(" \n")}"
            Left(UiException(message, cause = None))
        }
      }
    }
  }

  def connectAccount(): TaskService[Unit] = {
    val resultCode =
      GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(contextSupport.context)
    if (resultCode == ConnectionResult.SUCCESS) {
      val intent = Marshmallow ifSupportedThen {
        AccountManager.newChooseAccountIntent(
          javaNull,
          javaNull,
          Array(accountType),
          javaNull,
          javaNull,
          javaNull,
          javaNull)
      } getOrElse {
        AccountPicker.newChooseAccountIntent(
          javaNull,
          javaNull,
          Array(accountType),
          false,
          javaNull,
          javaNull,
          javaNull,
          javaNull)
      }
      for {
        _ <- di.trackEventProcess.chooseAccount()
        _ <- uiStartIntentForResult(intent, RequestCodes.selectAccount).toService()
      } yield ()
    } else {
      onConnectionFailed(None, resultCode)
    }
  }

  def deviceSelected(packages: Seq[PackagesByCategory]): TaskService[Unit] =
    for {
      _              <- di.trackEventProcess.chooseNewConfiguration()
      _              <- TaskService.right(clientStatuses = clientStatuses.copy(packages = packages))
      havePermission <- di.userAccountsProcess.havePermission(FineLocation)
      _ <- if (havePermission.result) generateCollections(None, packages)
      else requestPermissions()
    } yield ()

  def deviceSelected(maybeKey: Option[String]): TaskService[Unit] =
    for {
      _ <- if (maybeKey.isEmpty) di.trackEventProcess.chooseNewConfiguration()
      else di.trackEventProcess.chooseExistingDevice()
      _              <- TaskService.right(clientStatuses = clientStatuses.copy(deviceKey = maybeKey))
      havePermission <- di.userAccountsProcess.havePermission(FineLocation)
      _ <- if (havePermission.result) generateCollections(maybeKey, Seq.empty)
      else requestPermissions()
    } yield ()

  def requestPermissions(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.wizardPermissions, FineLocation)

  def permissionDialogCancelled(): TaskService[Unit] =
    generateCollections(clientStatuses.deviceKey, clientStatuses.packages)

  def finishWizard(): TaskService[Unit] = TaskService {
    CatchAll[UiException] {
      activityContextSupport.getActivity match {
        case Some(activity) =>
          activity.setResult(Activity.RESULT_OK)
          activity.finish()
        case None =>
          throw new NullPointerException("Activity instance not found")
      }
    }
  }

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): TaskService[Unit] =
    (requestCode, resultCode) match {
      case (RequestCodes.selectAccount, Activity.RESULT_OK) =>
        clientStatuses =
          clientStatuses.copy(email = readStringValue(data, AccountManager.KEY_ACCOUNT_NAME))
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
        case None        => TaskService(Task(Right((): Unit)))
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
            .getAuthToken(account, getString(R.string.android_market_oauth_scopes))
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
            .getAuthToken(account, getString(R.string.profile_and_drive_oauth_scopes))
            .resolveLeft {
              case ex: UserAccountsProcessOperationCancelledException =>
                Left(WizardGoogleTokenRequestCancelledException(ex.getMessage, Some(ex)))
              case ex: Throwable => Left(ex)
            }
          _ <- tryToConnectDriveApiClient()
        } yield ()
      case None => visibilityUiActions.goToUser()
    }

  def requestPermissionsResult(
      requestCode: Int,
      permissions: Array[String],
      grantResults: Array[Int]): TaskService[Unit] = {

    def generateOrRequest(hasPermission: Boolean, shouldRequest: Boolean): TaskService[Unit] =
      if (hasPermission || !shouldRequest) {
        generateCollections(clientStatuses.deviceKey, clientStatuses.packages)
      } else {
        wizardUiActions.showRequestPermissionsDialog()
      }

    if (requestCode == RequestCodes.wizardPermissions) {
      for {
        result        <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
        shouldRequest <- di.userAccountsProcess.shouldRequestPermission(FineLocation)
        _             <- generateOrRequest(result.exists(_.hasPermission(FineLocation)), shouldRequest.result)
      } yield ()
    } else {
      TaskService.empty
    }
  }

  def errorOperationMarketTokenCancelled(): TaskService[Unit] =
    wizardUiActions.showMarketPermissionDialog()

  def errorOperationGoogleTokenCancelled(): TaskService[Unit] =
    wizardUiActions.showGooglePermissionDialog()

  def driveConnected(): TaskService[Unit] = googleSignIn()

  def driveConnectionFailed(connectionResult: ConnectionResult): TaskService[Unit] =
    onConnectionFailed(connectionResult)

  def plusConnected(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(apiClient) =>
        for {
          _                <- visibilityUiActions.showLoadingConnectingWithGooglePlus()
          maybeProfileName <- di.socialProfileProcess.updateUserProfile(apiClient)
          _                <- loadDevices(maybeProfileName)
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
      uiStartIntentForResult(signInIntent, resolveConnectedUser).toService()
    }

    clientStatuses.email match {
      case Some(email) =>
        for {
          plusApiClient <- di.socialProfileProcess.createSocialProfileClient(
            clientId = getString(R.string.api_v2_client_id),
            account = email)
          _ = storePlusApiClient(plusApiClient)
          _ <- signInIntentService(plusApiClient)
        } yield ()
      case None => visibilityUiActions.goToUser()
    }
  }

  def googleDriveClient: TaskService[GoogleApiClient] =
    clientStatuses.driveApiClient match {
      case Some(client) if client.isConnected => TaskService.right(client)
      case Some(_)                            => TaskService.left(JobException("Client not connected"))
      case _                                  => TaskService.left(JobException("Client not available"))
    }

  private[this] def onConnectionFailed(result: ConnectionResult): TaskService[Unit] = {
    val maybeResult = Option(result)
    val errorCode   = maybeResult.map(_.getErrorCode) getOrElse ConnectionResult.CANCELED
    onConnectionFailed(maybeResult, errorCode)
  }

  private[this] def onConnectionFailed(
      maybeResult: Option[ConnectionResult],
      errorCode: Int): TaskService[Unit] = {

    def showGoogleApiErrorDialog: TaskService[Unit] = withActivity { activity =>
      Ui(
        GoogleApiAvailability
          .getInstance()
          .getErrorDialog(activity, errorCode, resolveGooglePlayConnection)
          .show()).toService()
    }

    def shouldShowDialog: Boolean =
      errorCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
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

  private[this] def generateCollections(
      maybeKey: Option[String],
      packages: Seq[PackagesByCategory]): TaskService[Unit] = {
    (maybeKey, packages) match {
      case (Some(key), _)       => visibilityUiActions.goToWizard(key)
      case (_, p) if p.nonEmpty => visibilityUiActions.goToNewConfiguration(p)
      case _                    => visibilityUiActions.goToNewConfiguration(Seq.empty)
    }
  }

  private[this] def tryToConnectDriveApiClient(): TaskService[Unit] =
    clientStatuses.driveApiClient match {
      case Some(client) => TaskService(CatchAll[UiException](client.connect()))
      case None         => requestAndroidMarketPermission()
    }

  private[this] def tryToConnectGoogleApiClient(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(client) =>
        TaskService(CatchAll[UiException](client.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL)))
      case None => googleSignIn()
    }

  private[this] def loadDevices(maybeProfileName: Option[String]): TaskService[Unit] = {

    // If we found some error when connecting to Backend V1 we just return an empty collection of devices
    def loadDevicesFromV1(): TaskService[Seq[UserV1Device]] =
      di.userV1Process
        .getUserInfo(Build.MODEL, Seq(getString(R.string.android_market_oauth_scopes)))
        .map(_.devices)
        .resolveLeft {
          case e: UserV1ConfigurationException =>
            AppLog.info("Invalid configuration for backend V1")
            Right(Seq.empty)
          case e => Right(Seq.empty)
        }

    def verifyAndUpdate(
        client: GoogleApiClient,
        email: String,
        cloudStorageResources: Seq[CloudStorageDeviceSummary]): TaskService[UserCloudDevices] = {

      import Conversions._

      if (cloudStorageResources.isEmpty) {
        for {
          userInfoDevices <- loadDevicesFromV1()
            .resolveIf(!V1EmptyDeviceWizard.readValue, Seq.empty)
        } yield {
          UserCloudDevices(
            deviceType = V1DeviceType,
            name = maybeProfileName getOrElse email,
            userDevice = None,
            devices = Seq.empty,
            dataV1 = userInfoDevices)
        }
      } else {
        for {
          actualDevice <- di.cloudStorageProcess
            .prepareForActualDevice(client, cloudStorageResources)
          (maybeUserDevice, devices) = actualDevice
        } yield {
          UserCloudDevices(
            deviceType = GoogleDriveDeviceType,
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice,
            dataV1 = Seq.empty)
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
        cloudStorageResources <- di.cloudStorageProcess
          .getCloudStorageDevices(client)
          .resolveIf(!GoogleDriveEmptyDeviceWizard.readValue, Seq.empty)
        userCloudDevices <- verifyAndUpdate(client, email, cloudStorageResources).resolveLeftTo(
          UserCloudDevices(NoFoundDeviceType, email, None, Seq.empty, Seq.empty))
      } yield userCloudDevices

    }

    clientStatuses match {
      case WizardJobsStatuses(
          _,
          _,
          Some(client),
          _,
          Some(email),
          Some(androidMarketToken),
          Some(emailTokenId)) =>
        for {
          _       <- visibilityUiActions.showLoadingDevices()
          devices <- loadCloudDevices(client, email, androidMarketToken, emailTokenId)
          _ <- (devices.deviceType, devices.userDevice, devices.dataV1) match {
            case (GoogleDriveDeviceType, Some(_), _) =>
              wizardUiActions.showDevices(devices)
            case (V1DeviceType, _, data) if data.nonEmpty =>
              wizardUiActions.showDevices(devices)
            case _ => deviceSelected(None)
          }
        } yield ()
      case _ =>
        wizardUiActions.showErrorConnectingGoogle() *> visibilityUiActions.goToUser()
    }

  }

  protected def getString(res: Int): String = resGetString(res)

}

case class WizardJobsStatuses(
    deviceKey: Option[String] = None,
    packages: Seq[PackagesByCategory] = Seq.empty,
    driveApiClient: Option[GoogleApiClient] = None,
    plusApiClient: Option[GoogleApiClient] = None,
    email: Option[String] = None,
    androidMarketToken: Option[String] = None,
    mailTokenId: Option[String] = None)
