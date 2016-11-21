package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.observers.ObserverRegister
import cards.nine.app.ui.collections.jobs.uiactions.{NavigationUiActions, ToolbarUiActions, GroupCollectionsUiActions, SingleCollectionUiActions}
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait GroupCollectionsJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait GroupCollectionsJobsScope
    extends Scope
      with CollectionTestData
      with LauncherTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockGroupCollectionsUiActions = mock[GroupCollectionsUiActions]

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockToolbarUiActions = mock[ToolbarUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockObserverRegister = mock[ObserverRegister]

    mockInjector.observerRegister returns mockObserverRegister

    val groupCollectionsJobs = new GroupCollectionsJobs(mockGroupCollectionsUiActions,mockToolbarUiActions,mockNavigationUiActions)(contextWrapper)

  }

}


class GroupCollectionsJobsSpec
  extends GroupCollectionsJobsSpecification {

  "resume" should {
    "calls to register Observer" in new GroupCollectionsJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      groupCollectionsJobs.resume().mustRightUnit
      there was one(mockObserverRegister).registerObserverTask()
    }
  }

  "pause" should {
    "calls to unregister Observer" in new GroupCollectionsJobsScope {

      mockObserverRegister.unregisterObserverTask() returns serviceRight(Unit)
      groupCollectionsJobs.pause().mustRightUnit
      there was one(mockObserverRegister).unregisterObserverTask()
    }
  }

  "back" should {
    "calls to back" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.back() returns serviceRight(Unit)
      groupCollectionsJobs.back().mustRightUnit
      there was one(mockGroupCollectionsUiActions).back()
    }
  }

  "destroy" should {
    "calls to destroy" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.destroy() returns serviceRight(Unit)
      groupCollectionsJobs.destroy().mustRightUnit
      there was one(mockGroupCollectionsUiActions).destroy()
    }
  }
}