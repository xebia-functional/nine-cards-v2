package cards.nine.app.ui.launcher.jobs

import android.graphics.Point
import android.os.Bundle
import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.launcher.{EditWidgetsMode, MoveTransformation, NormalMode}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{ApplicationTestData, CardTestData, CollectionTestData, DeviceTestData}
import cards.nine.models.NineCardsIntent
import cards.nine.models.types._
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.moment.MomentProcess
import cards.nine.process.trackevent.TrackEventProcess
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait NavigationJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait NavigationJobsScope
    extends Scope
    with LauncherTestData
    with CollectionTestData
    with ApplicationTestData
    with DeviceTestData
    with CardTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockNavigationUiActions = mock[NavigationUiActions]

    mockNavigationUiActions.dom returns mockLauncherDOM

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    val mockMenuDrawersUiActions = mock[MenuDrawersUiActions]

    val mockWidgetUiActions = mock[WidgetUiActions]

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockUserAccountsProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountsProcess

    val bundle = mock[Bundle]

    val mockPoint = mock[Point]

    val mockNineCardsIntent = mock[NineCardsIntent]

    val navigationJobs = new NavigationJobs(mockNavigationUiActions, mockAppDrawerUiActions, mockMenuDrawersUiActions, mockWidgetUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }
  }

}


class NavigationJobsSpec
  extends NavigationJobsSpecification {

  "goToWizard" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.goToWizard() returns serviceRight(Unit)
      navigationJobs.goToWizard().mustRightUnit
      there was one(mockNavigationUiActions).goToWizard()
    }
  }

  "openMenu" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockMenuDrawersUiActions.openMenu() returns serviceRight(Unit)
      navigationJobs.openMenu().mustRightUnit
      there was one(mockMenuDrawersUiActions).openMenu()
    }
  }

  "launchCreateOrCollection" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchCreateOrCollection(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchCreateOrCollection(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchCreateOrCollection(bundle)
    }
  }

  "launchPrivateCollection" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchPrivateCollection(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchPrivateCollection(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchPrivateCollection(bundle)
    }
  }

  "launchPublicCollection" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchPublicCollection(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchPublicCollection(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchPublicCollection(bundle)
    }
  }

  "launchAddMoment" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchAddMoment(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchAddMoment(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchAddMoment(bundle)
    }
  }

  "launchEditMoment" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchEditMoment(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchEditMoment(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchEditMoment(bundle)
    }
  }

  "launchWidgets" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchWidgets(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchWidgets(bundle).mustRightUnit
      there was one(mockNavigationUiActions).launchWidgets(bundle)
    }
  }

  sequential
  "clickWorkspaceBackground" should {
    "returns a valid response when statuses mode is NormalMode" in new NavigationJobsScope {

      statuses = statuses.copy(mode = NormalMode)
      mockMenuDrawersUiActions.openAppsMoment() returns serviceRight(Unit)

      navigationJobs.clickWorkspaceBackground().mustRightUnit

      there was one(mockMenuDrawersUiActions).openAppsMoment()
    }

    "returns a valid response when statuses mode is EditWidgetMode and transformation isn't None" in new NavigationJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode, transformation = Option(MoveTransformation))
      mockWidgetUiActions.reloadViewEditWidgets() returns serviceRight(Unit)
      navigationJobs.clickWorkspaceBackground().mustRightUnit

      there was one(mockWidgetUiActions).reloadViewEditWidgets()
      statuses.transformation shouldEqual None
    }

    "returns a valid response when statuses mode is EditWidgetMode and transformation is None" in new NavigationJobsScope {

      statuses = statuses.copy(mode = EditWidgetsMode)
      mockWidgetUiActions.closeModeEditWidgets() returns serviceRight(Unit)
      navigationJobs.clickWorkspaceBackground().mustRightUnit

      there was one(mockWidgetUiActions).closeModeEditWidgets()
      statuses.idWidget shouldEqual None
      statuses.mode shouldEqual NormalMode
    }
  }

  "goToCollection" should {
    "returns a valid response when has a collection " in new NavigationJobsScope {

      mockNavigationUiActions.goToCollection(any, any) returns serviceRight(Unit)

      navigationJobs.goToCollection(Option(collection), mockPoint)

      there was one(mockNavigationUiActions).goToCollection(collection, mockPoint)
      there was no(mockNavigationUiActions).showContactUsError()

    }

    "show a error message of contact when hasn't a collection " in new NavigationJobsScope {

      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)
      navigationJobs.goToCollection(None, mockPoint)
      there was one(mockNavigationUiActions).showContactUsError()
    }
  }

  "openApp" should {
    "returns a valid response when drawer is open" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns true
      mockAppDrawerUiActions.closeTabs() returns serviceRight(Unit)

      navigationJobs.openApp(applicationData).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was one(mockAppDrawerUiActions).closeTabs()
    }

    "returns a valid response when drawer is close" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns false
      mockTrackEventProcess.openAppFromAppDrawer(any, any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openApp(applicationData).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was no(mockAppDrawerUiActions).closeTabs()
      there was one(mockTrackEventProcess).openAppFromAppDrawer(applicationData.packageName, AppCategory(applicationData.category))
      there was one(mockLauncherExecutorProcess).execute(===(toNineCardIntent(applicationData)))(any)
    }
  }
  "openContact" should {
    "returns a valid response when drawer is open and must be closed" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns true
      mockAppDrawerUiActions.closeTabs() returns serviceRight(Unit)

      navigationJobs.openContact(contact).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was one(mockAppDrawerUiActions).closeTabs()
    }

    "returns a valid response when drawer is close" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns false
      mockLauncherExecutorProcess.executeContact(any)(any) returns serviceRight(Unit)

      navigationJobs.openContact(contact).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was no(mockAppDrawerUiActions).closeTabs()
      there was one(mockLauncherExecutorProcess).executeContact(===(contact.lookupKey))(any)
    }
  }
  "openLastCall" should {
    "returns a valid response when drawer is open and must be closed" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns true
      mockAppDrawerUiActions.closeTabs() returns serviceRight(Unit)

      navigationJobs.openLastCall(numberPhone).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was one(mockAppDrawerUiActions).closeTabs()
    }

    "returns a valid response when drawer is close" in new NavigationJobsScope {

      mockLauncherDOM.isDrawerTabsOpened returns false
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openLastCall(numberPhone).mustRightUnit

      there was one(mockLauncherDOM).isDrawerTabsOpened
      there was no(mockAppDrawerUiActions).closeTabs()
    }
  }
  "openMomentIntent" should {
    "returns a valid response when card has a packageName and moment" in new NavigationJobsScope {

      mockTrackEventProcess.openAppFromAppDrawer(any, any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.closeAppsMoment() returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntent(card, Option(NineCardsMoment.defaultMoment)).mustRightUnit

      there was one(mockTrackEventProcess).openAppFromAppDrawer(card.packageName.getOrElse(""), MomentCategory(NineCardsMoment.defaultMoment))
      there was one(mockMenuDrawersUiActions).closeAppsMoment()
    }

    "returns a valid response when card has a packageName and hasn't moment" in new NavigationJobsScope {

      mockTrackEventProcess.openAppFromAppDrawer(any, any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.closeAppsMoment() returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntent(card, None).mustRightUnit

      there was one(mockTrackEventProcess).openAppFromAppDrawer(card.packageName.getOrElse(""), FreeCategory)
      there was one(mockMenuDrawersUiActions).closeAppsMoment()
    }

    "returns a valid response when card hasn't a packageName" in new NavigationJobsScope {

      mockMenuDrawersUiActions.closeAppsMoment() returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntent(card.copy(packageName = None), Option(NineCardsMoment.defaultMoment)).mustRightUnit

      there was no(mockTrackEventProcess).openAppFromAppDrawer(any, any)
      there was one(mockMenuDrawersUiActions).closeAppsMoment()
    }
  }

  "openMomentIntentException" should {
    "returns a valid response when has a number phone" in new NavigationJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntentException(Option(numberPhone)).mustRightUnit

      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.phoneCallPermission), ===(CallPhone))(any)
      statuses.lastPhone shouldEqual Option(numberPhone)
    }

    "returns a valid response when hasn't a number phone" in new NavigationJobsScope {

      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntentException(None).mustRightUnit

      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.phoneCallPermission), ===(CallPhone))(any)
      statuses.lastPhone shouldEqual None
    }

  }

  "execute" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)
      navigationJobs.execute(mockNineCardsIntent).mustRightUnit
      there was one(mockLauncherExecutorProcess).execute(===(mockNineCardsIntent))(any)
    }
  }

  "launchSearch" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.launchSearch(any) returns serviceRight(Unit)
      navigationJobs.launchSearch.mustRightUnit
      there was one(mockLauncherExecutorProcess).launchSearch(any)
    }
  }

  "launchVoiceSearch" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.launchVoiceSearch(any) returns serviceRight(Unit)
      navigationJobs.launchVoiceSearch.mustRightUnit
      there was one(mockLauncherExecutorProcess).launchVoiceSearch(any)
    }
  }

  "launchGooglePlay" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.launchGooglePlay(any)(any) returns serviceRight(Unit)
      navigationJobs.launchGooglePlay(packageName).mustRightUnit
      there was one(mockLauncherExecutorProcess).launchGooglePlay(===(packageName))(any)
    }
  }

  "launchGoogleWeather" should {
    "return false if the service return PermissionDenied for the specified permission" in new NavigationJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = false))
      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)

      navigationJobs.launchGoogleWeather().mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.locationPermission), ===(FineLocation))(any)
      there was no(mockLauncherExecutorProcess).launchGoogleWeather(any)
    }

    "return true if the service return true for the specified permission" in new NavigationJobsScope {

      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = true))
      mockLauncherExecutorProcess.launchGoogleWeather(any) returns serviceRight(Unit)

      navigationJobs.launchGoogleWeather().mustRightUnit

      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was no(mockUserAccountsProcess).requestPermission(===(RequestCodes.locationPermission), ===(FineLocation))(any)
      there was one(mockLauncherExecutorProcess).launchGoogleWeather(any)
    }
  }

  "launchPlayStore" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.launchPlayStore(any) returns serviceRight(Unit)
      navigationJobs.launchPlayStore().mustRightUnit
      there was one(mockLauncherExecutorProcess).launchPlayStore(any)
    }
  }

  "launchDial" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockLauncherExecutorProcess.launchDial(any)(any) returns serviceRight(Unit)
      navigationJobs.launchDial().mustRightUnit
      there was one(mockLauncherExecutorProcess).launchDial(===(None))(any)
    }
  }

  "goToChangeMoment" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockNavigationUiActions.showSelectMomentDialog(any) returns serviceRight(Unit)

      navigationJobs.goToChangeMoment().mustRightUnit

      there was one(mockMomentProcess).getMoments
      there was one(mockNavigationUiActions).showSelectMomentDialog(seqMoment)
    }
  }

  "goToMenuOption" should {
    "returns a valid response when itemId is collections " in new NavigationJobsScope {

      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_collections).mustRightUnit
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
    "returns a valid response when itemId is moments " in new NavigationJobsScope {

      mockNavigationUiActions.goToMomentWorkspace() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_moments).mustRightUnit
      there was one(mockNavigationUiActions).goToMomentWorkspace()
    }
    "returns a valid response when itemId is profile " in new NavigationJobsScope {

      mockNavigationUiActions.goToProfile() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_profile).mustRightUnit
      there was one(mockNavigationUiActions).goToProfile()
    }
    "show a message that not implemted yet when itemId is send_feeback " in new NavigationJobsScope {

      mockNavigationUiActions.showNoImplementedYetMessage() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_send_feedback).mustRightUnit
      there was one(mockNavigationUiActions).showNoImplementedYetMessage()
    }
    "show a message that not implemted yet when itemId is help " in new NavigationJobsScope {

      mockNavigationUiActions.showNoImplementedYetMessage() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_help).mustRightUnit
      there was one(mockNavigationUiActions).showNoImplementedYetMessage()
    }
    "return a Unit when itemId is other " in new NavigationJobsScope {
      navigationJobs.goToMenuOption(errorMenu).mustRightUnit
    }
  }
}