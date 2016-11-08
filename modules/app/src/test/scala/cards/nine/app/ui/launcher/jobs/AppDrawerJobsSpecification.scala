package cards.nine.app.ui.launcher.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.launcher.jobs.uiactions.AppDrawerUiActions
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data._
import cards.nine.models.types.{GetByName, ReadContacts, ReadCallLog}
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.recommendations.{RecommendedAppsConfigurationException, RecommendedAppsException, RecommendationsProcess}
import cards.nine.services.persistence.PersistenceServiceException
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait AppDrawerJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait AppDrawerJobsScope
    extends Scope
      with LauncherTestData
      with CollectionTestData
      with ApplicationTestData
      with DeviceTestData
      with ApiTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val exception = new PersistenceServiceException("")

    val mockInjector = mock[Injector]

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    val mockUserAccountsProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountsProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockRecommendationsProcess = mock[RecommendationsProcess]

    mockInjector.recommendationsProcess returns mockRecommendationsProcess

    val appDrawerJobs = new AppDrawerJobs(mockAppDrawerUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector
    }

  }

}

class AppDrawerJobsSpec
  extends AppDrawerJobsSpecification {

  "loadSearch" should {
    "return a valid response when the service returns a Seq.empty" in new AppDrawerJobsScope {

      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(Seq.empty)
      mockAppDrawerUiActions.reloadSearchInDrawer(any) returns serviceRight(Unit)

      appDrawerJobs.loadSearch(querry).mustRightUnit

      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was one(mockAppDrawerUiActions).reloadSearchInDrawer(Seq.empty)
    }

    "return a valid response when the service returns a Sequence not categorized" in new AppDrawerJobsScope {

      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(seqNotCategorizedPackage)
      mockAppDrawerUiActions.reloadSearchInDrawer(any) returns serviceRight(Unit)

      appDrawerJobs.loadSearch(querry).mustRightUnit

      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was one(mockAppDrawerUiActions).reloadSearchInDrawer(seqNotCategorizedPackage)
    }

    "returns a RecommendedAppsException when the service returns a fail response" in new AppDrawerJobsScope {

      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceLeft(RecommendedAppsException(""))

      appDrawerJobs.loadSearch(querry).mustLeft[RecommendedAppsException]

      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was no(mockAppDrawerUiActions).reloadSearchInDrawer(any)
    }

    "returns a RecommendedAppsConfigurationException when the service returns a fail response" in new AppDrawerJobsScope {

      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceLeft(RecommendedAppsConfigurationException(""))

      appDrawerJobs.loadSearch(querry).mustLeft[RecommendedAppsConfigurationException]

      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was no(mockAppDrawerUiActions).reloadSearchInDrawer(any)
    }
  }

  "loadApps" should {
    "" in new AppDrawerJobsScope {


    }
  }

  "loadContacts" should {
    "" in new AppDrawerJobsScope {

    }
  }

  "loadAppsByKeyword" should {
    "return a valid response when the service returns a right response" in new AppDrawerJobsScope {

      mockDeviceProcess.getIterableAppsByKeyWord(any, any)(any) returns serviceRight(iterableApps)
      mockAppDrawerUiActions.reloadAppsInDrawer(any, any, any) returns serviceRight(Unit)

      appDrawerJobs.loadAppsByKeyword(keyword).mustRightUnit

      there was one(mockDeviceProcess).getIterableAppsByKeyWord(===(keyword), ===(GetByName))(any)
      there was one(mockAppDrawerUiActions).reloadAppsInDrawer(iterableApps, null, null)
    }

    "returns a PersistenceServiceException when the service returns a fail response" in new AppDrawerJobsScope {

      mockDeviceProcess.getIterableAppsByKeyWord(any, any)(any) returns serviceLeft(exception)

      appDrawerJobs.loadAppsByKeyword(keyword).mustLeft[PersistenceServiceException]

      there was one(mockDeviceProcess).getIterableAppsByKeyWord(===(keyword), ===(GetByName))(any)
      there was no(mockAppDrawerUiActions).reloadAppsInDrawer(any, any, any)
    }
  }

  "loadContactsByKeyword" should {
    "return a valid response when the service returns a right response" in new AppDrawerJobsScope {

      mockDeviceProcess.getIterableContactsByKeyWord(any)(any) returns serviceRight(iterableContact)
      mockAppDrawerUiActions.reloadContactsInDrawer(any, any) returns serviceRight(Unit)

      appDrawerJobs.loadContactsByKeyword(keyword).mustRightUnit

      there was one(mockDeviceProcess).getIterableContactsByKeyWord(===(keyword))(any)
      there was one(mockAppDrawerUiActions).reloadContactsInDrawer(iterableContact, null)
    }

    "returns a PersistenceServiceException when the service returns a fail response" in new AppDrawerJobsScope {

      mockDeviceProcess.getIterableContactsByKeyWord(any)(any) returns serviceLeft(exception)

      appDrawerJobs.loadContactsByKeyword(keyword).mustLeft[PersistenceServiceException]

      there was one(mockDeviceProcess).getIterableContactsByKeyWord(===(keyword))(any)
      there was no(mockAppDrawerUiActions).reloadContactsInDrawer(any, any)
    }
  }

  "requestReadContacts" should {
    "return a valid response when the service returns a right response" in new AppDrawerJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)
      appDrawerJobs.requestReadContacts().mustRightUnit
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.contactsPermission), ===(ReadContacts))(any)
    }
  }

  "requestReadCallLog" should {
    "return a valid response when the service returns a right response" in new AppDrawerJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)
      appDrawerJobs.requestReadCallLog().mustRightUnit
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.callLogPermission), ===(ReadCallLog))(any)
    }
  }

}
