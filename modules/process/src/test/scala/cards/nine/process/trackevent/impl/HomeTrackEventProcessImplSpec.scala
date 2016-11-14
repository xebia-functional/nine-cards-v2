package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.HomeTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait HomeTrackEventProcessSpecification
  extends TaskServiceSpecification
  with HomeTrackEventTestData
  with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope
    extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class HomeTrackEventProcessImplSpec extends HomeTrackEventProcessSpecification {

  "openCollectionTitle" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openCollectionTitle(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(openCollectionTitleEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openCollectionTitle(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openCollectionTitleEvent)
    }

  }

  "openCollectionOrder" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openCollectionOrder(position).mustRightUnit

      there was one(mockTrackServices).trackEvent(openCollectionOrderEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openCollectionOrder(position).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openCollectionOrderEvent)
    }

  }

  "deleteCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.deleteCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(deleteCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.deleteCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(deleteCollectionEvent)
    }

  }

  "reorderCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.reorderCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(reorderCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.reorderCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(reorderCollectionEvent)
    }

  }

  "usingSearchByKeyboard" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.usingSearchByKeyboard().mustRightUnit

      there was one(mockTrackServices).trackEvent(usingSearchByKeyboardEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.usingSearchByKeyboard().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(usingSearchByKeyboardEvent)
    }

  }

  "usingSearchByVoice" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.usingSearchByVoice().mustRightUnit

      there was one(mockTrackServices).trackEvent(usingSearchByVoiceEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.usingSearchByVoice().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(usingSearchByVoiceEvent)
    }

  }

  "createNewCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollection().mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollection().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionEvent)
    }

  }

  "createNewCollectionFromMyCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollectionFromMyCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionFromMyCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollectionFromMyCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionFromMyCollectionEvent)
    }

  }

  "createNewCollectionFromPublicCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.createNewCollectionFromPublicCollection(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(createNewCollectionFromPublicCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.createNewCollectionFromPublicCollection(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(createNewCollectionFromPublicCollectionEvent)
    }

  }

  "goToSliderMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToSliderMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToSliderMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToSliderMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToSliderMenuEvent)
    }

  }

  "goToWorkspaceActions" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToWorkspaceActions().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToWorkspaceActionsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToWorkspaceActions().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToWorkspaceActionsEvent)
    }

  }

  "goToSliderMenuByGestures" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToSliderMenuByGestures().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToSliderMenuByGesturesEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToSliderMenuByGestures().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToSliderMenuByGesturesEvent)
    }

  }

  "goToMoments" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToMoments().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToMomentsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToMoments().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToMomentsEvent)
    }

  }

  "openDockAppTitle" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openDockAppTitle(packageName).mustRightUnit

      there was one(mockTrackServices).trackEvent(openDockAppTitleEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openDockAppTitle(packageName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openDockAppTitleEvent)
    }

  }

  "openDockAppOrder" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openDockAppOrder(position).mustRightUnit

      there was one(mockTrackServices).trackEvent(openDockAppOrderEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openDockAppOrder(position).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openDockAppOrderEvent)
    }

  }

  "goToAppDrawer" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToAppDrawer().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToAppDrawerEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToAppDrawer().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToAppDrawerEvent)
    }

  }
}
