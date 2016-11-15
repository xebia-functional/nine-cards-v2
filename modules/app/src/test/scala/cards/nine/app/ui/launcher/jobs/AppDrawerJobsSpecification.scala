package cards.nine.app.ui.launcher.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.launcher.jobs.uiactions.AppDrawerUiActions
import cards.nine.app.ui.launcher.types._
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data._
import cards.nine.models.types._
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.recommendations.{RecommendationsProcess, RecommendedAppsConfigurationException, RecommendedAppsException}
import cards.nine.process.trackevent.TrackEventProcess
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

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val appDrawerJobs = new AppDrawerJobs(mockAppDrawerUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector
    }

  }

}

class AppDrawerJobsSpec
  extends AppDrawerJobsSpecification {

  "loadSearch" should {
    "return a valid response when the service returns a Seq.empty" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToGooglePlayButton() returns serviceRight(Unit)
      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(Seq.empty)
      mockAppDrawerUiActions.reloadSearchInDrawer(any) returns serviceRight(Unit)

      appDrawerJobs.loadSearch(querry).mustRightUnit

      there was one(mockTrackEventProcess).goToGooglePlayButton()
      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was one(mockAppDrawerUiActions).reloadSearchInDrawer(Seq.empty)
    }

    "return a valid response when the service returns a Sequence not categorized" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToGooglePlayButton() returns serviceRight(Unit)
      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceRight(seqNotCategorizedPackage)
      mockAppDrawerUiActions.reloadSearchInDrawer(any) returns serviceRight(Unit)

      appDrawerJobs.loadSearch(querry).mustRightUnit

      there was one(mockTrackEventProcess).goToGooglePlayButton()
      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was one(mockAppDrawerUiActions).reloadSearchInDrawer(seqNotCategorizedPackage)
    }

    "returns a RecommendedAppsException when the service returns a fail response" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToGooglePlayButton() returns serviceRight(Unit)
      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceLeft(RecommendedAppsException(""))

      appDrawerJobs.loadSearch(querry).mustLeft[RecommendedAppsException]

      there was one(mockTrackEventProcess).goToGooglePlayButton()
      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was no(mockAppDrawerUiActions).reloadSearchInDrawer(any)
    }

    "returns a RecommendedAppsConfigurationException when the service returns a fail response" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToGooglePlayButton() returns serviceRight(Unit)
      mockAppDrawerUiActions.showLoadingInGooglePlay() returns serviceRight(Unit)
      mockRecommendationsProcess.searchApps(any, any)(any) returns serviceLeft(RecommendedAppsConfigurationException(""))

      appDrawerJobs.loadSearch(querry).mustLeft[RecommendedAppsConfigurationException]

      there was one(mockTrackEventProcess).goToGooglePlayButton()
      there was one(mockAppDrawerUiActions).showLoadingInGooglePlay()
      there was one(mockRecommendationsProcess).searchApps(===(querry), ===(null))(any)
      there was no(mockAppDrawerUiActions).reloadSearchInDrawer(any)
    }
  }

  "loadApps" should {
    "return a valid response when loading the apps with AppsAlphabetical" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToAppDrawer() returns serviceRight(Unit)
      mockTrackEventProcess.goToApps() returns serviceRight(Unit)
      mockTrackEventProcess.goToFiltersByButton(any) returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(iterableApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(appsCounters)
      mockAppDrawerUiActions.reloadAppsInDrawer(any, any, any) returns serviceRight(Unit)

      appDrawerJobs.loadApps(AppsAlphabetical).mustRightUnit

      there was one(mockTrackEventProcess).goToAppDrawer()
      there was one(mockTrackEventProcess).goToApps()
      there was one(mockTrackEventProcess).goToFiltersByButton(GetByName.name)
      there was one(mockDeviceProcess).getIterableApps(===(GetByName))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByName))(any)
      there was one(mockAppDrawerUiActions).reloadAppsInDrawer(iterableApps, GetByName, appsCounters)
    }

    "return a valid response when loading the apps with AppsByCategories" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToAppDrawer() returns serviceRight(Unit)
      mockTrackEventProcess.goToApps() returns serviceRight(Unit)
      mockTrackEventProcess.goToFiltersByButton(any) returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(iterableApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(appsCounters)
      mockAppDrawerUiActions.reloadAppsInDrawer(any, any, any) returns serviceRight(Unit)

      appDrawerJobs.loadApps(AppsByCategories).mustRightUnit

      there was one(mockTrackEventProcess).goToAppDrawer()
      there was one(mockTrackEventProcess).goToApps()
      there was one(mockTrackEventProcess).goToFiltersByButton(GetByCategory.name)
      there was one(mockDeviceProcess).getIterableApps(===(GetByCategory))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByCategory))(any)
      there was one(mockAppDrawerUiActions).reloadAppsInDrawer(iterableApps, GetByCategory, appsCounters)
    }

    "return a valid response when loading the apps with AppsByLastInstall" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToAppDrawer() returns serviceRight(Unit)
      mockTrackEventProcess.goToApps() returns serviceRight(Unit)
      mockTrackEventProcess.goToFiltersByButton(any) returns serviceRight(Unit)
      mockDeviceProcess.getIterableApps(any)(any) returns serviceRight(iterableApps)
      mockDeviceProcess.getTermCountersForApps(any)(any) returns serviceRight(appsCounters)
      mockAppDrawerUiActions.reloadAppsInDrawer(any, any, any) returns serviceRight(Unit)

      appDrawerJobs.loadApps(AppsByLastInstall).mustRightUnit

      there was one(mockTrackEventProcess).goToAppDrawer()
      there was one(mockTrackEventProcess).goToApps()
      there was one(mockTrackEventProcess).goToFiltersByButton(GetByInstallDate.name)
      there was one(mockDeviceProcess).getIterableApps(===(GetByInstallDate))(any)
      there was one(mockDeviceProcess).getTermCountersForApps(===(GetByInstallDate))(any)
      there was one(mockAppDrawerUiActions).reloadAppsInDrawer(iterableApps, GetByInstallDate, appsCounters)
    }
  }

  "loadContacts" should {
    "return a valid response when loading the contacts with ContactsByLastCall" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToContacts() returns serviceRight(Unit)
      mockDeviceProcess.getLastCalls(any) returns serviceRight(seqLastCallsContact)
      mockAppDrawerUiActions.reloadLastCallContactsInDrawer(any) returns serviceRight(Unit)

      appDrawerJobs.loadContacts(ContactsByLastCall).mustRightUnit

      there was one(mockTrackEventProcess).goToContacts()
      there was one(mockDeviceProcess).getLastCalls(any)
      there was one(mockAppDrawerUiActions).reloadLastCallContactsInDrawer(seqLastCallsContact)
    }

    "return a valid response when loading the contacts with ContactsFavorites" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToContacts() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContacts(any)(any) returns serviceRight(iterableContact)
      mockDeviceProcess.getTermCountersForContacts(any)(any) returns serviceRight(contactsCounters)
      mockAppDrawerUiActions.reloadContactsInDrawer(any, any) returns serviceRight(Unit)

      appDrawerJobs.loadContacts(ContactsFavorites).mustRightUnit

      there was one(mockTrackEventProcess).goToContacts()
      there was one(mockDeviceProcess).getIterableContacts(===(FavoriteContacts))(any)
      there was one(mockDeviceProcess).getTermCountersForContacts(===(FavoriteContacts))(any)
      there was one(mockAppDrawerUiActions).reloadContactsInDrawer(===(iterableContact),===(contactsCounters))
    }

    "return a valid response when loading the contacts with ContactsAlphabetical" in new AppDrawerJobsScope {

      mockTrackEventProcess.goToContacts() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContacts(any)(any) returns serviceRight(iterableContact)
      mockDeviceProcess.getTermCountersForContacts(any)(any) returns serviceRight(contactsCounters)
      mockAppDrawerUiActions.reloadContactsInDrawer(any, any) returns serviceRight(Unit)

      appDrawerJobs.loadContacts(ContactsAlphabetical).mustRightUnit

      there was one(mockTrackEventProcess).goToContacts()
      there was one(mockDeviceProcess).getIterableContacts(===(AllContacts))(any)
      there was one(mockDeviceProcess).getTermCountersForContacts(===(AllContacts))(any)
      there was one(mockAppDrawerUiActions).reloadContactsInDrawer(===(iterableContact),===(contactsCounters))
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
