package cards.nine.app.ui.wizard.jobs

import android.content.res.Resources
import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.{JobException, RequestCodes}
import cards.nine.app.ui.wizard.WizardMarketTokenRequestCancelledException
import cards.nine.app.ui.wizard.jobs.uiactions.{VisibilityUiActions, WizardUiActions}
import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CloudStorageTestData
import cards.nine.commons.test.data.WizardJobsValues._
import cards.nine.models.types.{CallPhone, FineLocation, PermissionResult}
import cards.nine.process.accounts.{UserAccountsProcess, UserAccountsProcessPermissionException}
import cards.nine.process.cloud.CloudStorageProcess
import cards.nine.process.social.SocialProfileProcess
import cards.nine.process.trackevent.TrackEventProcess
import cards.nine.process.user.UserProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait WizardJobsSpecification
  extends TaskServiceSpecification
  with Mockito {

  trait WizardJobsScope
    extends Scope
    with CloudStorageTestData {

    val exception = new Throwable("")

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    contextSupport.getResources returns mockResources

    val mockResources = mock[Resources]

    val mockActivityContextSupport = mock[ActivityContextSupport]

    val userAccountProcessException = UserAccountsProcessPermissionException("")

    val wizardMarketTokenRequestCancelledException = WizardMarketTokenRequestCancelledException("")

    val mockInjector: Injector = mock[Injector]

    val mockWizardUiAction = mock[WizardUiActions]

    val mockVisibilityUiActions = mock[VisibilityUiActions]

    val mockUserAccountsProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountsProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val mockApiClient = mock[GoogleApiClient]

    val mockSocialProfileProcess = mock[SocialProfileProcess]

    mockInjector.socialProfileProcess returns mockSocialProfileProcess

    val mockUserProcess = mock[UserProcess]

    mockInjector.userProcess returns mockUserProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val wizardJobs = new WizardJobs(mockWizardUiAction, mockVisibilityUiActions)(contextWrapper) {
      override lazy val di: Injector = mockInjector

      override def getString(res: Int): String = ""
    }
  }

}

class WizardJobsSpec
  extends WizardJobsSpecification {

  "initialize" should {
    "call to initialize" in new WizardJobsScope {

      mockWizardUiAction.initialize() returns serviceRight(Unit)
      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.initialize().mustRightUnit

      there was one(mockWizardUiAction).initialize()
      there was one(mockVisibilityUiActions).goToUser()

    }
  }

  "stop" should {
    "call to stop" in new WizardJobsScope {

      mockApiClient.disconnect()
      wizardJobs.stop().mustRightUnit

      there was one(mockApiClient).disconnect()

    }
  }

  "deviceSelected" should {
    "return a valid response when the service returns a right response" in new WizardJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = true))
      mockVisibilityUiActions.goToWizard(any) returns serviceRight(Unit)
      mockTrackEventProcess.chooseExistingDevice() returns serviceRight(Unit)

      wizardJobs.deviceSelected(Option(keyDevice)).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToWizard(keyDevice)
      there was one(mockTrackEventProcess).chooseExistingDevice()
    }

    "return a valid response when the service returns that haven't permissions" in new WizardJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = false))
      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)
      mockTrackEventProcess.chooseExistingDevice() returns serviceRight(Unit)

      wizardJobs.deviceSelected(Option(keyDevice)).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was no(mockVisibilityUiActions).goToWizard(keyDevice)
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.wizardPermissions), ===(FineLocation))(any)
      there was one(mockTrackEventProcess).chooseExistingDevice()
    }

    "return a valid response if deviceKey is None" in new WizardJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = true))
      mockVisibilityUiActions.goToNewConfiguration() returns serviceRight(Unit)
      mockTrackEventProcess.chooseNewConfiguration() returns serviceRight(Unit)

      wizardJobs.deviceSelected(None).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToNewConfiguration()
      there was no(mockVisibilityUiActions).goToWizard(any)
      there was one(mockTrackEventProcess).chooseNewConfiguration()
    }

  }

  "requestPermissions" should {

    "call with wizardPermissions" in new WizardJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)
      wizardJobs.requestPermissions().mustRightUnit

      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.wizardPermissions), ===(FineLocation))(any)
    }

    "return a UserAccountsProcessPermissionException when the service returns an exception" in new WizardJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceLeft(userAccountProcessException)
      wizardJobs.requestPermissions().mustLeft[UserAccountsProcessPermissionException]

      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.wizardPermissions), ===(FineLocation))(any)
    }
  }

  "permissionDialogCancelled" should {

    "return a valid response if deviceKey is right" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(deviceKey = Option(keyDevice))
      mockVisibilityUiActions.goToWizard(any) returns serviceRight(Unit)

      wizardJobs.permissionDialogCancelled().mustRightUnit
      there was one(mockVisibilityUiActions).goToWizard(keyDevice)
    }

    "return a valid response if deviceKey is None" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(deviceKey = None)
      mockVisibilityUiActions.goToNewConfiguration() returns serviceRight(Unit)

      wizardJobs.permissionDialogCancelled().mustRightUnit
      there was one(mockVisibilityUiActions).goToNewConfiguration()
      there was no(mockVisibilityUiActions).goToWizard(any)
    }
  }

  "requestAndroidMarketPermission" should {

    "return an Answer and call goToUser if the client hasn't email" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = None)
      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.requestAndroidMarketPermission().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()

    }

    "return an Answer if the client hasn't androidMarketToken" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(mockApiClient)

      mockVisibilityUiActions.showLoadingConnectingWithGoogle() returns serviceRight(Unit)
      mockUserAccountsProcess.getAuthToken(any, any)(any) returns serviceRight(token)
      mockVisibilityUiActions.showLoadingRequestGooglePermission() returns serviceRight(Unit)

      wizardJobs.requestAndroidMarketPermission().mustRightUnit

      there was no(mockVisibilityUiActions).goToUser()
      there was one(mockCloudStorageProcess).createCloudStorageClient(===(email))(any)
      there was one(mockVisibilityUiActions).showLoadingConnectingWithGoogle()
      there was two(mockUserAccountsProcess).getAuthToken(===(email), any)(any)

    }

    "return a WizardMarketTokenRequestCancelledException if the client hasn't androidMarketToken and the service returns an exception" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(mockApiClient)
      mockVisibilityUiActions.showLoadingConnectingWithGoogle() returns serviceRight(Unit)
      mockUserAccountsProcess.getAuthToken(anyString, anyString)(any) returns serviceLeft(WizardMarketTokenRequestCancelledException(""))
      mockVisibilityUiActions.showLoadingRequestGooglePermission() returns serviceRight(Unit)

      wizardJobs.requestAndroidMarketPermission().mustLeft[WizardMarketTokenRequestCancelledException]

      there was no(mockVisibilityUiActions).goToUser()
      there was one(mockCloudStorageProcess).createCloudStorageClient(===(email))(any)
      there was one(mockVisibilityUiActions).showLoadingConnectingWithGoogle()
      there was one(mockUserAccountsProcess).getAuthToken(===(email), any)(any)

    }
  }

  "requestGooglePermission" should {

    "return an Answer and call goToUser if the client hasn't email" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = None)
      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.requestGooglePermission().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()
    }

    "return an Answer when the client has email" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = Option(mockApiClient))
      mockVisibilityUiActions.showLoadingRequestGooglePermission() returns serviceRight(Unit)
      mockUserAccountsProcess.getAuthToken(any, any)(any) returns serviceRight(token)

      wizardJobs.requestGooglePermission().mustRightUnit

      there was no(mockVisibilityUiActions).goToUser()
    }

    "return an Answer when the client has email and hasn't driveApiClient" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
      mockVisibilityUiActions.showLoadingRequestGooglePermission() returns serviceRight(Unit)
      mockUserAccountsProcess.getAuthToken(any, any)(any) returns serviceRight(token)
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = None)

      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(mockApiClient)
      mockVisibilityUiActions.showLoadingConnectingWithGoogle() returns serviceRight(Unit)

      wizardJobs.requestGooglePermission().mustRightUnit

      there was no(mockVisibilityUiActions).goToUser()
      there was one(mockCloudStorageProcess).createCloudStorageClient(===(email))(any)
      there was one(mockVisibilityUiActions).showLoadingConnectingWithGoogle()
      there was two(mockVisibilityUiActions).showLoadingRequestGooglePermission()
      there was three(mockUserAccountsProcess).getAuthToken(===(email), any)(any)
    }
  }

  "requestPermissionsResult" should {

    "return a Unit when hasn't wizard permissions" in new WizardJobsScope {

      wizardJobs.requestPermissionsResult(requestCodeError, permissions, granResults).mustRightUnit

    }

    "return a Unit when resquestCode is WizardPermmisons and has FineLocation but result is false and hasn't deviceKey" in new WizardJobsScope {

      val permissionResultFalse = PermissionResult(FineLocation, result = false)
      mockUserAccountsProcess.parsePermissionsRequestResult(any, any) returns serviceRight(Seq(permissionResultFalse))
      mockUserAccountsProcess.shouldRequestPermission(any)(any) returns serviceRight(permissionResultFalse)
      mockVisibilityUiActions.goToNewConfiguration() returns serviceRight(Unit)

      wizardJobs.requestPermissionsResult(requestCode, permissions, granResults).mustRightUnit

      there was one(mockUserAccountsProcess).parsePermissionsRequestResult(permissions, granResults)
      there was one(mockUserAccountsProcess).shouldRequestPermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToNewConfiguration()
      there was no(mockVisibilityUiActions).goToWizard(any)

    }

    "return a Unit when resquestCode is WizardPermmisons and has FineLocation but result is false and has deviceKey" in new WizardJobsScope {

      val permissionResultFalse = PermissionResult(FineLocation, result = false)
      mockUserAccountsProcess.parsePermissionsRequestResult(any, any) returns serviceRight(Seq(permissionResultFalse))
      mockUserAccountsProcess.shouldRequestPermission(any)(any) returns serviceRight(permissionResultFalse)
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(deviceKey = Option(keyDevice))
      mockVisibilityUiActions.goToWizard(any) returns serviceRight(Unit)

      wizardJobs.requestPermissionsResult(requestCode, permissions, granResults).mustRightUnit

      there was one(mockUserAccountsProcess).parsePermissionsRequestResult(permissions, granResults)
      there was one(mockUserAccountsProcess).shouldRequestPermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToWizard(keyDevice)

    }

    "return a Unit when resquestCode is WizardPermmisons and has FineLocation but result is true and hasn't deviceKey" in new WizardJobsScope {

      val permissionResult = PermissionResult(FineLocation, result = true)
      mockUserAccountsProcess.parsePermissionsRequestResult(any, any) returns serviceRight(Seq(permissionResult))
      mockUserAccountsProcess.shouldRequestPermission(any)(any) returns serviceRight(permissionResult)
      mockVisibilityUiActions.goToNewConfiguration() returns serviceRight(Unit)

      wizardJobs.requestPermissionsResult(requestCode, permissions, granResults).mustRightUnit

      there was one(mockUserAccountsProcess).parsePermissionsRequestResult(permissions, granResults)
      there was one(mockUserAccountsProcess).shouldRequestPermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToNewConfiguration()
      there was no(mockVisibilityUiActions).goToWizard(any)

    }

    "return a Unit when resquestCode is WizardPermmisons and has FineLocation but result is true and has deviceKey" in new WizardJobsScope {

      val permissionResult = PermissionResult(FineLocation, result = true)
      mockUserAccountsProcess.parsePermissionsRequestResult(any, any) returns serviceRight(Seq(permissionResult))
      mockUserAccountsProcess.shouldRequestPermission(any)(any) returns serviceRight(permissionResult)
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(deviceKey = Option(keyDevice))
      mockVisibilityUiActions.goToWizard(any) returns serviceRight(Unit)

      wizardJobs.requestPermissionsResult(requestCode, permissions, granResults).mustRightUnit

      there was one(mockUserAccountsProcess).parsePermissionsRequestResult(permissions, granResults)
      there was one(mockUserAccountsProcess).shouldRequestPermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToWizard(keyDevice)

    }

    "return a Unit when resquestCode is WizardPermmisons and has CallPhone but result is true" in new WizardJobsScope {

      val permissionResult = PermissionResult(CallPhone, result = true)
      mockUserAccountsProcess.parsePermissionsRequestResult(any, any) returns serviceRight(Seq(permissionResult))
      mockUserAccountsProcess.shouldRequestPermission(any)(any) returns serviceRight(permissionResult)
      mockWizardUiAction.showRequestPermissionsDialog() returns serviceRight(Unit)

      wizardJobs.requestPermissionsResult(requestCode, permissions, granResults).mustRightUnit

      there was one(mockUserAccountsProcess).parsePermissionsRequestResult(permissions, granResults)
      there was one(mockUserAccountsProcess).shouldRequestPermission(===(FineLocation))(any)
      there was one(mockWizardUiAction).showRequestPermissionsDialog()

    }
  }

  "errorOperationMarketTokenCancelled" should {
    "return a valid response when the service returns a right response" in new WizardJobsScope {

      mockWizardUiAction.showMarketPermissionDialog() returns serviceRight(Unit)

      wizardJobs.errorOperationMarketTokenCancelled().mustRightUnit

      there was one(mockWizardUiAction).showMarketPermissionDialog()
    }
  }

  "errorOperationGoogleTokenCancelled" should {
    "return a valid response when the service returns a right response" in new WizardJobsScope {

      mockWizardUiAction.showGooglePermissionDialog() returns serviceRight(Unit)

      wizardJobs.errorOperationGoogleTokenCancelled().mustRightUnit

      there was one(mockWizardUiAction).showGooglePermissionDialog()
    }
  }


  "plusConnected" should {
    "return an Answer when the client hasn't plusApiClient" in new WizardJobsScope {

      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.plusConnected().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()

    }

    "return an Answer when the client has plusApiClient" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(plusApiClient = Option(mockApiClient))
      mockVisibilityUiActions.showLoadingConnectingWithGooglePlus() returns serviceRight(Unit)
      mockSocialProfileProcess.updateUserProfile(any)(any) returns serviceRight(Option(profileName))
      mockWizardUiAction.showErrorConnectingGoogle() returns serviceRight(Unit)
      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.plusConnected().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()
      there was one(mockVisibilityUiActions).showLoadingConnectingWithGooglePlus()
      there was one(mockSocialProfileProcess).updateUserProfile(===(mockApiClient))(any)

    }

    "return an Answer when the WizardStatus has all" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(plusApiClient = Option(mockApiClient), driveApiClient = Option(mockApiClient), email = Option(email), androidMarketToken = Option(token), mailTokenId = Option(emailTokenId))
      mockVisibilityUiActions.showLoadingConnectingWithGooglePlus() returns serviceRight(Unit)
      mockSocialProfileProcess.updateUserProfile(any)(any) returns serviceRight(Option(profileName))
      mockVisibilityUiActions.showLoadingDevices() returns serviceRight(Unit)

      mockUserProcess.signIn(any, any, any)(any) returns serviceRight(Unit)
      mockCloudStorageProcess.getCloudStorageDevices(any)(any) returns serviceRight(Seq(generateCloudStorageDeviceSummary()))

      mockWizardUiAction.showDevices(any) returns serviceRight(Unit)

      wizardJobs.plusConnected().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()
      there was one(mockVisibilityUiActions).showLoadingConnectingWithGooglePlus()
      there was one(mockSocialProfileProcess).updateUserProfile(===(mockApiClient))(any)
      there was one(mockVisibilityUiActions).showLoadingDevices()
      there was one(mockUserProcess).signIn(===(email), ===(token), ===(emailTokenId))(any)
      there was one(mockCloudStorageProcess).getCloudStorageDevices(===(mockApiClient))(any)

    }.pendingUntilFixed
  }

  "googleSignIn" should {

    "return an Answer when hasn't email" in new WizardJobsScope {

      mockVisibilityUiActions.goToUser() returns serviceRight(Unit)

      wizardJobs.googleSignIn().mustRightUnit

      there was one(mockVisibilityUiActions).goToUser()

    }
  }

  "googleDriveClient" should {
    "return a JobException when hasn't driveApiClient" in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = None)
      wizardJobs.googleDriveClient.mustLeft[JobException]
    }

    "return a JobException when has driveApiClient and the client hasn't connected" in new WizardJobsScope {

      mockApiClient.isConnected returns false
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = Option(mockApiClient))
      wizardJobs.googleDriveClient.mustLeft[JobException]

    }

    "return an Answer when has driveApiClient and the client has connected" in new WizardJobsScope {

      mockApiClient.isConnected returns true
      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = Option(mockApiClient))
      wizardJobs.googleDriveClient.run must beLike {
        case Right(result) => result shouldEqual mockApiClient
      }
    }
  }

}
