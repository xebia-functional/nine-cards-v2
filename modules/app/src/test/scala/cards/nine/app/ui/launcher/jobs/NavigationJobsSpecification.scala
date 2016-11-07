package cards.nine.app.ui.launcher.jobs

import android.graphics.Point
import android.os.Bundle
import cards.nine.app.di.Injector
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.launcher.{EditWidgetsMode, MoveTransformation, NormalMode}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.moment.MomentProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.R

trait NavigationJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait NavigationJobsScope
    extends Scope
      with LauncherTestData
      with CollectionTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    val mockMenuDrawersUiActions = mock[MenuDrawersUiActions]

    val mockWidgetUiActions = mock[WidgetUiActions]

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val bundle = mock[Bundle]

    val mockPoint = mock[Point]

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
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "openContact" should {
    "returns a valid response when " in new NavigationJobsScope {


    }
  }
  "openLastCall" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "openMomentIntent" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "openMomentIntentException" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "execute" should {
    "returns a valid response when " in new NavigationJobsScope {


    }
  }
  "launchSearch" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "launchVoiceSearch" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "launchGooglePlay" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }
  "launchGoogleWeather" should {
    "returns a valid response when  " in new NavigationJobsScope {


    }
  }

  "launchPlayStore" should {
    "returns a valid response when " in new NavigationJobsScope {


    }
  }
  "launchDial" should {
    "returns a valid response when the service returns a right response " in new NavigationJobsScope {

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
      navigationJobs.goToMenuOption(R.id.menu_collections)
      there was one(mockNavigationUiActions).goToCollectionWorkspace()
    }
    "returns a valid response when itemId is moments " in new NavigationJobsScope {

      mockNavigationUiActions.goToMomentWorkspace() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_moments)
      there was one(mockNavigationUiActions).goToMomentWorkspace()
    }
    "returns a valid response when itemId is profile " in new NavigationJobsScope {

      mockNavigationUiActions.goToProfile() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_profile)
      there was one(mockNavigationUiActions).goToProfile()
    }
    "show a message that not implemted yet when itemId is send_feeback " in new NavigationJobsScope {

      mockNavigationUiActions.showNoImplementedYetMessage() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_send_feedback)
      there was one(mockNavigationUiActions).showNoImplementedYetMessage()
    }
    "show a message that not implemted yet when itemId is help " in new NavigationJobsScope {

      mockNavigationUiActions.showNoImplementedYetMessage() returns serviceRight(Unit)
      navigationJobs.goToMenuOption(R.id.menu_help)
      there was one(mockNavigationUiActions).showNoImplementedYetMessage()
    }
  }
}