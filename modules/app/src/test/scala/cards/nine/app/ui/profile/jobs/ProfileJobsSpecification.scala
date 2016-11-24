package cards.nine.app.ui.profile.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.JobException
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.UserTestData
import cards.nine.process.cloud.CloudStorageProcess
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.user.UserProcess
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.app.ui.profile.ProfileActivity._


trait ProfileJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait ProfileJobsScope
    extends Scope
    with LauncherTestData
    with UserTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockProfileUiActions = mock[ProfileUiActions]

    val mockApiClient = mock[GoogleApiClient]

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockUserProcess = mock[UserProcess]

    mockInjector.userProcess returns mockUserProcess

    val mockCloudStorageProcess = mock[CloudStorageProcess]

    mockInjector.cloudStorageProcess returns mockCloudStorageProcess

    val profileJobs = new ProfileJobs(mockProfileUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def getThemeTask = TaskService.right(theme)

      override def themeFile = ""
    }

  }

}

class ProfileJobsSpec
  extends ProfileJobsSpecification {

  sequential
  "driveConnected" should {

    "returns a valid response when apiclient is connected" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns true

      profileJobs.driveConnected().mustRightUnit

    }.pendingUntilFixed

    "returns a valid response when apiclient isn't connected" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns false
      mockProfileUiActions.showLoading() returns serviceRight(Unit)

      profileJobs.driveConnected().mustRightUnit

      there was one(mockProfileUiActions).showLoading()
    }

    "returns a JobException when the connection throws a exception" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = Option(mockApiClient))
      mockApiClient.isConnected returns false
      mockProfileUiActions.showLoading() returns serviceRight(Unit)
      mockApiClient.connect() throws new RuntimeException("")

      profileJobs.driveConnected().mustLeft[JobException]

      there was one(mockProfileUiActions).showLoading()
    }

    "Do nothing when ApiClient is None" in new ProfileJobsScope {

      statuses = statuses.copy(apiClient = None)
      mockProfileUiActions.showLoading() returns serviceRight(Unit)

      profileJobs.driveConnected().mustRightUnit

      there was one(mockProfileUiActions).showLoading()
    }
  }

  "initialize" should {
    "initialize profile jobs when the service returns a right response" in new ProfileJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockProfileUiActions.initialize(any) returns serviceRight(Unit)
      mockUserProcess.getUser(any) returns serviceRight(user)
      mockCloudStorageProcess.createCloudStorageClient(any)(any) returns serviceRight(mockApiClient)
      mockProfileUiActions.userProfile(any,any,any) returns serviceRight(Unit)

      profileJobs.initialize().mustRightUnit

    }

  }

}