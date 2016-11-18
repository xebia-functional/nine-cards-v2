package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.commons.Conversions
import cards.nine.app.di.Injector
import cards.nine.app.ui.collections.actions.apps.AppsFragment._
import cards.nine.app.ui.data.IterableData
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.commons.test.data.{ApiTestData, CardTestData, DeviceTestData}
import cards.nine.models.types.GetByName
import cards.nine.process.device.DeviceProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.recommendations.RecommendationsProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait AppsJobsSpecification
  extends TaskServiceSpecification
  with Mockito {

  trait AppsJobsScope
    extends Scope
    with ApiTestData
    with CardTestData
    with DeviceTestData
    with IterableData
    with Conversions {

    val exception = new Throwable("")

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val mockInjector: Injector = mock[Injector]

    val mockAppsUiAction = mock[AppsUiActions]

    val mockDeviceProcess = mock[DeviceProcess]

    val mockRecommendationsProcess = mock[RecommendationsProcess]

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    mockInjector.recommendationsProcess returns mockRecommendationsProcess

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val appsJobs = new AppsJobs(mockAppsUiAction)(contextWrapper) {
      override lazy val di: Injector = mockInjector
    }

  }

}

class AppsJobsSpec
  extends AppsJobsSpecification {

  "initialize" should {
    "call to initialize" in new AppsJobsScope {

      override val appsJobs = new AppsJobs(mockAppsUiAction)(contextWrapper) {
        override def loadApps() = TaskService.empty
      }

      mockAppsUiAction.initialize(cardPackageSet) returns serviceRight(Unit)

      appsJobs.initialize(cardPackageSet).mustRightUnit

      there was one(mockAppsUiAction).initialize(cardPackageSet)

    }
  }

  "destroy" should {
    "call to destroy" in new AppsJobsScope {

      mockAppsUiAction.destroy() returns serviceRight(Unit)

      appsJobs.destroy().mustRightUnit

      there was one(mockAppsUiAction).destroy()

    }
  }

  "loadApps" should {
    "return a valid response when the service returns a right response" in new AppsJobsScope {

      mockAppsUiAction.showLoading() returns serviceRight(Unit)
      mockAppsUiAction.showSelectedMessageAndFab() returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(iterableCursorApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(appsCounters)
      mockAppsUiAction.showApps(any, any) returns serviceRight(Unit)

      appsJobs.loadApps().mustRightUnit

      there was one(mockAppsUiAction).showLoading()
      there was one(mockDeviceProcess).getIterableApps(===(GetByName))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByName))(any)
      there was one(mockAppsUiAction).showApps(iterableCursorApps, appsCounters)

    }

    "return a valid response when the service returns no IterableApps " in new AppsJobsScope {

      mockAppsUiAction.showLoading() returns serviceRight(Unit)
      mockAppsUiAction.showSelectedMessageAndFab() returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(emptyIterableCursorApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(appsCounters)
      mockAppsUiAction.showApps(any, any) returns serviceRight(Unit)

      appsJobs.loadApps().mustRightUnit

      there was one(mockAppsUiAction).showLoading()
      there was one(mockDeviceProcess).getIterableApps(===(GetByName))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByName))(any)
      there was one(mockAppsUiAction).showApps(emptyIterableCursorApps, appsCounters)

    }

    "return a valid response when the service returns an empty TermCounters sequence" in new AppsJobsScope {

      mockAppsUiAction.showLoading() returns serviceRight(Unit)
      mockAppsUiAction.showSelectedMessageAndFab() returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(iterableCursorApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(Seq.empty)
      mockAppsUiAction.showApps(any, any) returns serviceRight(Unit)

      appsJobs.loadApps().mustRightUnit

      there was one(mockAppsUiAction).showLoading()
      there was one(mockDeviceProcess).getIterableApps(===(GetByName))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByName))(any)
      there was one(mockAppsUiAction).showApps(iterableCursorApps, Seq.empty)

    }

  }

  "loadSearch" should {
    "return a valid response when the service returns a right response" in new AppsJobsScope {

      mockAppsUiAction.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(seqNotCategorizedPackage)
      mockAppsUiAction.reloadSearch(any) returns serviceRight(Unit)

      appsJobs.loadSearch(keyword).mustRightUnit

      there was one(mockAppsUiAction).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(keyword), any)(any)
      there was one(mockAppsUiAction).reloadSearch(seqNotCategorizedPackage)

    }

    "return a valid response when the service returns an empty sequence" in new AppsJobsScope {

      mockAppsUiAction.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(Seq.empty)
      mockAppsUiAction.reloadSearch(any) returns serviceRight(Unit)

      appsJobs.loadSearch(keyword).mustRightUnit

      there was one(mockAppsUiAction).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(keyword), any)(any)
      there was one(mockAppsUiAction).reloadSearch(Seq.empty)

    }

  }

  "loadAppsByKeyword" should {
    "return a valid response when the service returns a right response" in new AppsJobsScope {

      mockDeviceProcess.getIterableAppsByKeyWord(any, any)(any) returns serviceRight(iterableCursorApps)
      mockAppsUiAction.showApps(any, any) returns serviceRight(Unit)

      appsJobs.loadAppsByKeyword(keyword).mustRightUnit

      there was one(mockDeviceProcess).getIterableAppsByKeyWord(===(keyword), ===(GetByName))(any)
      there was one(mockAppsUiAction).showApps(iterableCursorApps, Seq.empty)

    }

    "return a valid response when the service returns no IterableApps " in new AppsJobsScope {

      mockDeviceProcess.getIterableAppsByKeyWord(any, any)(any) returns serviceRight(iterableCursorApps)
      mockAppsUiAction.showApps(any, any) returns serviceRight(Unit)

      appsJobs.loadAppsByKeyword(keyword).mustRightUnit

      there was one(mockDeviceProcess).getIterableAppsByKeyWord(===(keyword), ===(GetByName))(any)
      there was one(mockAppsUiAction).showApps(iterableCursorApps, Seq.empty)

    }

  }

  sequential
  "getAddedAndRemovedApps" should {
    "return a valid response when no cards are removed or added" in new AppsJobsScope {

      appStatuses = appStatuses.copy(
        initialPackages = setApplicationDataPackages,
        selectedPackages = setApplicationDataPackages)

      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)

      appsJobs.getAddedAndRemovedApps.mustRight { result =>
        val (cardsToAdd, cardsToRemove) = result
        cardsToAdd shouldEqual Seq.empty
        cardsToRemove shouldEqual Seq.empty
      }

      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }

    "return a valid response when some cards are removed and no cards are added" in new AppsJobsScope {

      appStatuses = appStatuses.copy(
        initialPackages = setApplicationDataPackages,
        selectedPackages = setApplicationDataPackages.take(2))

      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)

      appsJobs.getAddedAndRemovedApps.mustRight { result =>
        val (cardsToAdd, cardsToRemove) = result
        cardsToAdd shouldEqual Seq.empty
        cardsToRemove shouldEqual Seq(toCardData(seqApplicationData(2)))
      }

      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }

    "return a valid response when no cards are removed and some cards are added" in new AppsJobsScope {

      appStatuses = appStatuses.copy(
        initialPackages = setApplicationDataPackages.take(1),
        selectedPackages = setApplicationDataPackages)

      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)

      val cardsToAddSeq = seqApplicationData.drop(1) map toCardData

      appsJobs.getAddedAndRemovedApps.mustRight { result =>
        val (cardsToAdd, cardsToRemove) = result
        cardsToAdd shouldEqual cardsToAddSeq
        cardsToRemove shouldEqual Seq.empty
      }

      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }

    "return a valid response when some cards are removed and some cards are added" in new AppsJobsScope {

      appStatuses = appStatuses.copy(
        initialPackages = setApplicationDataPackages.take(2),
        selectedPackages = setApplicationDataPackages.drop(1))

      mockDeviceProcess.getSavedApps(any)(any) returns serviceRight(seqApplicationData)

      val cardsToAddSeq = Seq(toCardData(seqApplicationData(2)))
      val cardsToRemoveSeq = Seq(toCardData(seqApplicationData(0)))

      appsJobs.getAddedAndRemovedApps.mustRight { result =>
        val (cardsToAdd, cardsToRemove) = result
        cardsToAdd shouldEqual cardsToAddSeq
        cardsToRemove shouldEqual cardsToRemoveSeq
      }

      there was one(mockDeviceProcess).getSavedApps(===(GetByName))(any)

    }
  }

  "updateSelectedApps" should {
    "call to showUpdateSelectedApps" in new AppsJobsScope {

      mockAppsUiAction.showUpdateSelectedApps(cardPackageSet) returns serviceRight(Unit)

      appsJobs.updateSelectedApps(cardPackageSet).mustRightUnit

      there was one(mockAppsUiAction).showUpdateSelectedApps(cardPackageSet)

    }
  }

  "launchGooglePlay" should {
    "call to launchGooglePlay" in new AppsJobsScope {

      mockLauncherExecutorProcess.launchGooglePlay(any)(any) returns serviceRight(Unit)

      appsJobs.launchGooglePlay(applicationPackageName).mustRightUnit

      there was one(mockLauncherExecutorProcess).launchGooglePlay(===(applicationPackageName))(any)

    }
  }

  "showErrorLoadingApps" should {
    "call to showErrorLoadingApps" in new AppsJobsScope {

      mockAppsUiAction.showErrorLoadingAppsInScreen() returns serviceRight(Unit)

      appsJobs.showErrorLoadingApps().mustRightUnit

      there was one(mockAppsUiAction).showErrorLoadingAppsInScreen()

    }
  }

  "showError" should {
    "call to showError" in new AppsJobsScope {

      mockAppsUiAction.showError() returns serviceRight(Unit)

      appsJobs.showError().mustRightUnit

      there was one(mockAppsUiAction).showError()

    }
  }

  "close" should {
    "call to close" in new AppsJobsScope {

      mockAppsUiAction.close() returns serviceRight(Unit)

      appsJobs.close().mustRightUnit

      there was one(mockAppsUiAction).close()

    }
  }

}
