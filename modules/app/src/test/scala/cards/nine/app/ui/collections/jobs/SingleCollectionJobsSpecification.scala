package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.collections.jobs.uiactions.{ScrollUp, SingleCollectionUiActions}
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait SingleCollectionJobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait SingleCollectionJobsScope
    extends Scope
      with CollectionTestData
      with LauncherTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockSingleCollectionUiActions = mock[SingleCollectionUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockAnimateCards = true

    val mockCollection = collection

    val singleCollectionJobs = new SingleCollectionJobs(mockAnimateCards, Option(mockCollection), mockSingleCollectionUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def themeFile = ""

    }

  }

}

class SingleCollectionJobsSpec
  extends SingleCollectionJobsSpecification {

  "initialize" should {
    "Shows empty collection if it doesn't have a collection" in new SingleCollectionJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockSingleCollectionUiActions.updateStatus(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.initialize(any, any) returns serviceRight(Unit)

      singleCollectionJobs.initialize(ScrollUp).mustRightUnit

      there was one(mockSingleCollectionUiActions).updateStatus(false, ScrollUp)
      there was one(mockSingleCollectionUiActions).initialize(true, mockCollection)

    }

    "Initializes all actions and services" in new SingleCollectionJobsScope {

      override val singleCollectionJobs = new SingleCollectionJobs(mockAnimateCards, None, mockSingleCollectionUiActions)(contextWrapper) {

        override lazy val di: Injector = mockInjector

        override def themeFile = ""

      }

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockSingleCollectionUiActions.updateStatus(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.showEmptyCollection() returns serviceRight(Unit)

      singleCollectionJobs.initialize(ScrollUp).mustRightUnit

    }
  }

  "reorderCard" should {
    "return a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockTrackEventProcess.reorderApplication(any) returns serviceRight(Unit)
      mockCollectionProcess.reorderCard(any, any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.reloadCards() returns serviceRight(Unit)

      singleCollectionJobs.reorderCard(collection.id, card.id, position).mustRightUnit

      there was one(mockTrackEventProcess).reorderApplication(position)
      there was one(mockCollectionProcess).reorderCard(collection.id, card.id, position)
      there was one(mockSingleCollectionUiActions).reloadCards()
    }
  }

  "moveToCollection" should {
    "return a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockSingleCollectionUiActions.moveToCollection(any) returns serviceRight(Unit)

      singleCollectionJobs.moveToCollection().mustRightUnit

      there was one(mockCollectionProcess).getCollections
      there was one(mockSingleCollectionUiActions).moveToCollection(seqCollection)
    }

    "return a CollectionException if the service throws an exception " in new SingleCollectionJobsScope {

      mockCollectionProcess.getCollections returns serviceLeft(CollectionException(""))

      singleCollectionJobs.moveToCollection().mustLeft[CollectionException]

      there was one(mockCollectionProcess).getCollections
      there was no(mockSingleCollectionUiActions).moveToCollection(any)
    }
  }

  "addCards" should {
    "return a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.addAppToCollection(any,any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was exactly(seqCard.size)(mockTrackEventProcess).addAppToCollection(any,any)
      there was one(mockSingleCollectionUiActions).addCards(seqCard)
    }

  }

}

