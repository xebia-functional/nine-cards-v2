package cards.nine.app.ui.wizard.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.wizard.WizardMarketTokenRequestCancelledException
import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.models.types.{FineLocation, PermissionResult}
import cards.nine.process.accounts.{UserAccountsProcessOperationCancelledException, UserAccountsProcess, UserAccountsProcessPermissionException}
import cards.nine.process.cloud.CloudStorageProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.commons.test.data.WizardJobsValues._


trait WizardJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait WizardJobsScope
    extends Scope {

    val exception = new Throwable("")

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val mockActivityContextSupport = mock[ActivityContextSupport]

    val userAccountProcessException = UserAccountsProcessPermissionException("")

    val mockInjector: Injector = mock[Injector]

    val mockWizardUiAction = mock[WizardUiActions]

    val mockVisibilityUiActions = mock[VisibilityUiActions]

    val mockUserAccountsProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountsProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val mockApiClient = mock[GoogleApiClient]

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

      wizardJobs.deviceSelected(Option(keyDevice)).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToWizard(keyDevice)
    }

    "return a valid response when the service returns that haven't permissions" in new WizardJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = false))
      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)

      wizardJobs.deviceSelected(Option(keyDevice)).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was no(mockVisibilityUiActions).goToWizard(keyDevice)
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.wizardPermissions), ===(FineLocation))(any)
    }

    "return a valid response if deviceKey is None" in new WizardJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = true))
      mockVisibilityUiActions.goToNewConfiguration() returns serviceRight(Unit)

      wizardJobs.deviceSelected(None).mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockVisibilityUiActions).goToNewConfiguration()
      there was no(mockVisibilityUiActions).goToWizard(any)
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

    "return a WizardMarketTokenRequestCancelledException if the client hasn't androidMarketToken and " in new WizardJobsScope {

      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(mockApiClient)
      mockVisibilityUiActions.showLoadingConnectingWithGoogle() returns serviceRight(Unit)
      mockUserAccountsProcess.getAuthToken(anyString, anyString)(any) returns serviceLeft(UserAccountsProcessOperationCancelledException(""))
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

//    "return an Answer when the client has email and hasn't driveApiClient" in new WizardJobsScope {
//
//      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(email = Option(email))
//      mockVisibilityUiActions.showLoadingRequestGooglePermission() returns serviceRight(Unit)
//      mockUserAccountsProcess.getAuthToken(any, any)(any) returns serviceRight(token)
//      wizardJobs.clientStatuses = wizardJobs.clientStatuses.copy(driveApiClient = None)
//
//      wizardJobs.requestGooglePermission().mustRightUnit
//
//      there was no(mockVisibilityUiActions).goToUser()
//    }
  }

}
