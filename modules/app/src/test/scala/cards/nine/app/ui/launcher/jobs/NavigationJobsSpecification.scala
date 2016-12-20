package cards.nine.app.ui.launcher.jobs

import android.graphics.Point
import android.os.Bundle
import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.components.layouts.LauncherWorkSpaces
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.launcher.{EditWidgetsMode, MoveTransformation, NormalMode}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{ApplicationTestData, CardTestData, CollectionTestData, DeviceTestData, DockAppTestData}
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
    with DockAppTestData
    with CardTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockLauncherWorkspaces = mock[LauncherWorkSpaces]

    mockLauncherDOM.workspaces returns mockLauncherWorkspaces

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

    val mockPoint = mock[Point]

    val navigationJobs = new NavigationJobs(mockNavigationUiActions, mockAppDrawerUiActions, mockMenuDrawersUiActions, mockWidgetUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def getColor(res: Int): Int = 0

    }
  }

}

class NavigationJobsSpec
  extends NavigationJobsSpecification {
  sequential
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
      navigationJobs.launchCreateOrCollection().mustRightUnit
      there was one(mockNavigationUiActions).launchCreateOrCollection(any)
    }
  }

  "launchPrivateCollection" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchPrivateCollection(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchPrivateCollection().mustRightUnit
      there was one(mockNavigationUiActions).launchPrivateCollection(any)
    }
  }

  "launchPublicCollection" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchPublicCollection(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchPublicCollection().mustRightUnit
      there was one(mockNavigationUiActions).launchPublicCollection(any)
    }
  }

  "launchAddMoment" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchAddMoment(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchAddMoment().mustRightUnit
      there was one(mockNavigationUiActions).launchAddMoment(any)
    }
  }

  "launchEditMoment" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchEditMoment(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchEditMoment(moment.momentType.name).mustRightUnit
      there was one(mockNavigationUiActions).launchEditMoment(any)
    }
  }

  "launchWidgets" should {
    "return a valid response when the service returns a right response" in new NavigationJobsScope {

      mockNavigationUiActions.launchWidgets(any[Bundle]) returns serviceRight(Unit)
      navigationJobs.launchWidgets().mustRightUnit
      there was one(mockNavigationUiActions).launchWidgets(any)
    }
  }

  "goToCollection" should {
    "returns a valid response when has a collection " in new NavigationJobsScope {

      mockTrackEventProcess.useNavigationBar() returns serviceRight(Unit)
      mockNavigationUiActions.goToCollection(any, any) returns serviceRight(Unit)

      navigationJobs.goToCollection(Option(collection), mockPoint)

      there was one(mockTrackEventProcess).useNavigationBar()
      there was one(mockNavigationUiActions).goToCollection(collection, mockPoint)
      there was no(mockNavigationUiActions).showContactUsError()
    }.pendingUntilFixed

    "show a error message of contact when hasn't a collection " in new NavigationJobsScope {

      mockTrackEventProcess.useNavigationBar() returns serviceRight(Unit)
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      navigationJobs.goToCollection(None, mockPoint)

      there was one(mockTrackEventProcess).useNavigationBar()
      there was one(mockNavigationUiActions).showContactUsError()
    }.pendingUntilFixed
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

      mockTrackEventProcess.openAppFromCollection(any, any) returns serviceRight(Unit)
      mockTrackEventProcess.openApplicationByMoment(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.closeAppsMoment() returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntent(card, Option(NineCardsMoment.defaultMoment)).mustRightUnit

      there was one(mockTrackEventProcess).openAppFromCollection(card.packageName.getOrElse(""), MomentCategory(NineCardsMoment.defaultMoment))
      there was one(mockTrackEventProcess).openApplicationByMoment(NineCardsMoment.defaultMoment.name)
      there was one(mockMenuDrawersUiActions).closeAppsMoment()
    }

    "returns a valid response when card has a packageName and hasn't moment" in new NavigationJobsScope {

      mockTrackEventProcess.openAppFromCollection(any, any) returns serviceRight(Unit)
      mockTrackEventProcess.openApplicationByMoment(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.closeAppsMoment() returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openMomentIntent(card, None).mustRightUnit

      there was no(mockTrackEventProcess).openAppFromCollection(card.packageName.getOrElse(""), MomentCategory(moment.momentType))
      there was no(mockTrackEventProcess).openApplicationByMoment(moment.momentType.name)
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

  sequential
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

  "openDockApp" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockTrackEventProcess.openDockAppTitle(any) returns serviceRight(Unit)
      mockTrackEventProcess.openDockAppOrder(any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      navigationJobs.openDockApp(dockAppData).mustRightUnit

      there was one(mockTrackEventProcess).openDockAppTitle(dockAppData.name)
      there was one(mockTrackEventProcess).openDockAppOrder(dockAppData.position)
      there was one(mockLauncherExecutorProcess).execute(===(dockAppData.intent))(any)
    }
  }

  "launchSearch" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockTrackEventProcess.usingSearchByKeyboard() returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchSearch(any) returns serviceRight(Unit)

      navigationJobs.launchSearch.mustRightUnit

      there was one(mockTrackEventProcess).usingSearchByKeyboard()
      there was one(mockLauncherExecutorProcess).launchSearch(any)
    }
  }

  "launchVoiceSearch" should {
    "returns a valid response when the service returns a right response" in new NavigationJobsScope {

      mockTrackEventProcess.usingSearchByVoice() returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchVoiceSearch(any) returns serviceRight(Unit)

      navigationJobs.launchVoiceSearch.mustRightUnit

      there was one(mockTrackEventProcess).usingSearchByVoice()
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

      mockTrackEventProcess.goToWeather() returns serviceRight(Unit)
      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = false))
      mockUserAccountsProcess.requestPermission(any, any)(any) returns serviceRight(Unit)

      navigationJobs.launchGoogleWeather().mustRightUnit

      there was one(mockTrackEventProcess).goToWeather()
      there was one(mockUserAccountsProcess).havePermission(===(FineLocation))(any)
      there was one(mockUserAccountsProcess).requestPermission(===(RequestCodes.locationPermission), ===(FineLocation))(any)
      there was no(mockLauncherExecutorProcess).launchGoogleWeather(any)
    }

    "return true if the service return true for the specified permission" in new NavigationJobsScope {

      mockTrackEventProcess.goToWeather() returns serviceRight(Unit)
      mockUserAccountsProcess.havePermission(any)(any) returns serviceRight(PermissionResult(FineLocation, result = true))
      mockLauncherExecutorProcess.launchGoogleWeather(any) returns serviceRight(Unit)

      navigationJobs.launchGoogleWeather().mustRightUnit

      there was one(mockTrackEventProcess).goToWeather()
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

      mockTrackEventProcess.goToCollectionsByMenu() returns serviceRight(Unit)
      mockNavigationUiActions.goToCollectionWorkspace() returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_collections).mustRightUnit

      there was one(mockTrackEventProcess).goToCollectionsByMenu()
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
    "returns a valid response when itemId is moments " in new NavigationJobsScope {

      mockTrackEventProcess.goToMomentsByMenu() returns serviceRight(Unit)
      mockNavigationUiActions.goToMomentWorkspace() returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_moments).mustRightUnit

      there was one(mockTrackEventProcess).goToMomentsByMenu()
      there was one(mockNavigationUiActions).goToMomentWorkspace()
    }
    "returns a valid response when itemId is profile " in new NavigationJobsScope {

      mockTrackEventProcess.goToProfileByMenu() returns serviceRight(Unit)
      mockNavigationUiActions.goToProfile() returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_profile).mustRightUnit

      there was one(mockTrackEventProcess).goToProfileByMenu()
      there was one(mockNavigationUiActions).goToProfile()
    }
    "shows a wallpaper when itemId is wallpaper " in new NavigationJobsScope {

      mockNavigationUiActions.launchWallpaper() returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_wallpaper).mustRightUnit

      there was one(mockNavigationUiActions).launchWallpaper()
    }
    "shows settings when itemId is setting " in new NavigationJobsScope {

      mockNavigationUiActions.launchSettings() returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_settings).mustRightUnit

      there was one(mockNavigationUiActions).launchSettings()
    }
    "shows settings when itemId is widgets " in new NavigationJobsScope {

      mockNavigationUiActions.launchWidgets(any[Bundle]) returns serviceRight(Unit)

      navigationJobs.goToMenuOption(R.id.menu_widget).mustRightUnit

      there was one(mockNavigationUiActions).launchWidgets(any)
    }
    "returns a Unit when itemId is other " in new NavigationJobsScope {
      navigationJobs.goToMenuOption(errorMenu).mustRightUnit
    }
  }
}