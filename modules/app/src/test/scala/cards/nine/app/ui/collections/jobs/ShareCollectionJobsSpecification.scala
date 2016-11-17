package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.collections.jobs.uiactions.SharedCollectionUiActions
import cards.nine.app.ui.commons.UiException
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.data.CollectionsData
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.models.types.PhoneCardType
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.{ContextWrapper, ActivityContextWrapper}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ShareCollectionJobsSpecification
  extends TaskServiceSpecification
    with Mockito {


  trait ShareCollectionScope
    extends Scope
      with CollectionsData
      with CollectionTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockSharedCollectionUiActions = mock[SharedCollectionUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val shareCollectionJobs = new SharedCollectionJobs(mockSharedCollectionUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector
    }
  }

}


class ShareCollectionJobsSpec
  extends ShareCollectionJobsSpecification {

  "reloadSharedCollectionId" should {
    "return a valid response when the service returns a right response" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockSharedCollectionUiActions.reloadSharedCollectionId(any) returns serviceRight(Unit)

      shareCollectionJobs.reloadSharedCollectionId().mustRightUnit

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockSharedCollectionUiActions).reloadSharedCollectionId(collection.sharedCollectionId)
    }

    "return an CollectionException if the service throws an exception" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceLeft(CollectionException(""))

      shareCollectionJobs.reloadSharedCollectionId().mustLeft[CollectionException]

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was no(mockSharedCollectionUiActions).reloadSharedCollectionId(any)
    }

    "return an UiException if the service throws an exception" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceLeft(UiException(""))

      shareCollectionJobs.reloadSharedCollectionId().mustLeft[UiException]

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was no(mockCollectionProcess).getCollectionById(any)
      there was no(mockSharedCollectionUiActions).reloadSharedCollectionId(any)
    }
  }

  "showPublishCollectionWizard" should {
    "shows a message of publish collection when the service returns a right response and  cardType is equal AppCardType" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockSharedCollectionUiActions.showPublishCollectionWizardDialog(any) returns serviceRight(Unit)

      shareCollectionJobs.showPublishCollectionWizard().mustRightUnit

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockSharedCollectionUiActions).showPublishCollectionWizardDialog(collection)
    }

    "shows a message of error publish collection when the service returns a right response and  cardType isn't equal AppCardType" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection.copy(cards = collection.cards map (_.copy(cardType = PhoneCardType)))))
      mockSharedCollectionUiActions.showMessagePublishContactsCollectionError returns serviceRight(Unit)

      shareCollectionJobs.showPublishCollectionWizard().mustRightUnit

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockSharedCollectionUiActions).showMessagePublishContactsCollectionError
    }

    "return an UiException if the service throws an exception" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      shareCollectionJobs.showPublishCollectionWizard().mustLeft[UiException]
      there was one(mockSharedCollectionUiActions).getCurrentCollection
    }
  }

  "shareCollection" should {
    "return a valid response when the service returns a right response" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockTrackEventProcess.shareCollectionByMenu(any) returns serviceRight(Unit)
      mockLauncherExecutorProcess.launchShare(any)(any) returns serviceRight(Unit)

      shareCollectionJobs.shareCollection().mustRightUnit

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
    }.pendingUntilFixed

    "shows a message of error that can't publish collection." in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection.copy(sharedCollectionId = None)))
      mockSharedCollectionUiActions.showMessageNotPublishedCollectionError returns serviceRight(Unit)

      shareCollectionJobs.shareCollection().mustRightUnit

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
      there was one(mockSharedCollectionUiActions).showMessageNotPublishedCollectionError
    }

    "return an CollectionException if the service throws an exception" in new ShareCollectionScope {

      mockSharedCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockCollectionProcess.getCollectionById(any) returns serviceLeft(CollectionException(""))

      shareCollectionJobs.shareCollection().mustLeft[CollectionException]

      there was one(mockSharedCollectionUiActions).getCurrentCollection
      there was one(mockCollectionProcess).getCollectionById(collection.id)
    }

    "return an UiException if the service throws an exception" in new ShareCollectionScope {
      mockSharedCollectionUiActions.getCurrentCollection returns serviceLeft(UiException(""))
      shareCollectionJobs.shareCollection().mustLeft[UiException]
      there was one(mockSharedCollectionUiActions).getCurrentCollection
    }
  }

}