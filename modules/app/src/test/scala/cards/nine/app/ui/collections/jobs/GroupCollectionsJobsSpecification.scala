package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.observers.ObserverRegister
import cards.nine.app.ui.collections.jobs.uiactions.{GroupCollectionsUiActions, NavigationUiActions, ToolbarUiActions}
import cards.nine.app.ui.commons.{JobException, BroadAction, UiException}
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.moment.MomentProcess
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

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockObserverRegister = mock[ObserverRegister]

    mockInjector.observerRegister returns mockObserverRegister

    val groupCollectionsJobs = new GroupCollectionsJobs(mockGroupCollectionsUiActions, mockToolbarUiActions, mockNavigationUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def themeFile = ""

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty
    }

  }

}


class GroupCollectionsJobsSpec
  extends GroupCollectionsJobsSpecification {

  "initialize" should {
    "shows the collections when the service returns a right response" in new GroupCollectionsJobsScope {

      val backgroundColor = 1
      val initialToolbarColor = 1
      val icon = "icon"
      //      val position = 1
      val stateChanged = true

      mockToolbarUiActions.initialize(any, any, any, any) returns serviceRight(Unit)
      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockGroupCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockGroupCollectionsUiActions.showCollections(any, any) returns serviceRight(Unit)

      groupCollectionsJobs.initialize(backgroundColor, initialToolbarColor, icon, position, stateChanged).mustRightUnit

      there was one(mockToolbarUiActions).initialize(backgroundColor, initialToolbarColor, icon, stateChanged)
      there was one(mockGroupCollectionsUiActions).showCollections(seqCollection, position)

    }

    "return a CollectionException if the service throws an exception" in new GroupCollectionsJobsScope {

      val backgroundColor = 1
      val initialToolbarColor = 1
      val icon = "icon"
      //      val position = 1
      val stateChanged = true

      mockToolbarUiActions.initialize(any, any, any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.initialize() returns serviceRight(Unit)
      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockCollectionProcess.getCollections returns serviceLeft(CollectionException(""))

      groupCollectionsJobs.initialize(backgroundColor, initialToolbarColor, icon, position, stateChanged).mustLeft[CollectionException]

      there was one(mockToolbarUiActions).initialize(backgroundColor, initialToolbarColor, icon, stateChanged)
      there was no(mockGroupCollectionsUiActions).showCollections(seqCollection, position)

    }
  }

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

  "reloadCards" should {
    "reloads cards when the service returns a right response " in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.reloadCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.reloadCards() mustRight { r => r shouldEqual seqCard }

      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockGroupCollectionsUiActions).reloadCards(seqCard)
    }

    "reloads card the database when current the collection cards are different" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection.copy(cards = Seq(card(3), card(3), card(5)))))
      mockGroupCollectionsUiActions.reloadCards(any) returns serviceRight(Unit)
      mockMomentProcess.getMoments returns serviceRight(seqMoment)

      groupCollectionsJobs.reloadCards() mustRight { r => r shouldEqual Seq(card(3), card(3), card(5)) }

      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockGroupCollectionsUiActions).reloadCards(Seq(card(3), card(3), card(5)))
    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      groupCollectionsJobs.reloadCards().mustLeft[UiException]
      there was no(mockCollectionProcess).getCollectionById(any)
    }

    "return a CollectionException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceLeft(CollectionException(""))

      groupCollectionsJobs.reloadCards().mustLeft[CollectionException]

      there was one(mockGroupCollectionsUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
    }
  }

  "editCard" should {
    "" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.editCard(any, any, any) returns serviceRight(Unit)
      mockGroupCollectionsUiActions.closeEditingModeUi() returns serviceRight(Unit)

      groupCollectionsJobs.editCard().mustLeft[JobException]

    }
  }

  "firstItemInCollection" should {
    "call to hide menu button" in new GroupCollectionsJobsScope {

      mockGroupCollectionsUiActions.hideMenuButton() returns serviceRight(Unit)
      groupCollectionsJobs.firstItemInCollection().mustRightUnit
      there was one(mockGroupCollectionsUiActions).hideMenuButton()
    }
  }

  "close" should {
    "call action close" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.closeCollectionByGesture() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.close() returns serviceRight(Unit)
      groupCollectionsJobs.close().mustRightUnit

    }
  }

  "showMenu" should {
    "shows menu when the service returns a right response" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addCardByMenu() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockGroupCollectionsUiActions.showMenu(any, any, any) returns serviceRight(Unit)

      groupCollectionsJobs.showMenu(true).mustRightUnit

      there was one(mockGroupCollectionsUiActions).showMenu(true, true, collection.themedColorIndex)

    }

    "return a UiException when the service throws an exception" in new GroupCollectionsJobsScope {

      mockTrackEventProcess.addCardByMenu() returns serviceRight(Unit)
      mockGroupCollectionsUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      groupCollectionsJobs.showMenu(true).mustLeft[UiException]

      there was no(mockGroupCollectionsUiActions).showMenu(any, any, any)

    }
  }
}