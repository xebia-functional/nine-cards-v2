package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.CollectionDetailTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait CollectionDetailTrackEventProcessSpecification
  extends TaskServiceSpecification
  with CollectionDetailTrackEventTestData
  with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope
    extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class CollectionDetailTrackEventProcessImplSpec extends CollectionDetailTrackEventProcessSpecification {

  "useNavigationBar" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.useNavigationBar().mustRightUnit

      there was one(mockTrackServices).trackEvent(useNavigationBarEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.useNavigationBar().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(useNavigationBarEvent)
    }

  }

  "reorderApplication" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.reorderApplication(newPosition).mustRightUnit

      there was one(mockTrackServices).trackEvent(reorderApplicationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.reorderApplication(newPosition).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(reorderApplicationEvent)
    }

  }

  "moveApplications" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.moveApplications(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(moveApplicationsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.moveApplications(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(moveApplicationsEvent)
    }

  }

  "removeApplications" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.removeApplications(packageNameSeq).mustRightUnit

      there was one(mockTrackServices).trackEvent(removeApplicationsEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.removeApplications(packageNameSeq).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(removeApplicationsEvent)
    }

  }

  "closeCollectionByGesture" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.closeCollectionByGesture().mustRightUnit

      there was one(mockTrackServices).trackEvent(closeCollectionByGestureEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.closeCollectionByGesture().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(closeCollectionByGestureEvent)
    }

  }

  "addShortcutByFab" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addShortcutByFab(shortcutName).mustRightUnit

      there was one(mockTrackServices).trackEvent(addShortcutByFabEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addShortcutByFab(shortcutName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addShortcutByFabEvent)
    }

  }

  "addRecommendationByFab" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addRecommendationByFab(recommendationName).mustRightUnit

      there was one(mockTrackServices).trackEvent(addRecommendationByFabEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addRecommendationByFab(recommendationName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addRecommendationByFabEvent)
    }

  }

  "addContactByFab" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addContactByFab().mustRightUnit

      there was one(mockTrackServices).trackEvent(addContactByFabEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addContactByFab().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addContactByFabEvent)
    }

  }

  "addAppsByFab" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addAppsByFab(packageNameSeq).mustRightUnit

      there was one(mockTrackServices).trackEvent(addAppsByFabEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addAppsByFab(packageNameSeq).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addAppsByFabEvent)
    }

  }

  "removeAppsByFab" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.removeAppsByFab(packageNameSeq).mustRightUnit

      there was one(mockTrackServices).trackEvent(removeAppsByFabEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.removeAppsByFab(packageNameSeq).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(removeAppsByFabEvent)
    }

  }

  "addCardByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addCardByMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(addCardByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addCardByMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addCardByMenuEvent)
    }

  }

  "publishCollectionByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.publishCollectionByMenu(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(publishCollectionByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.publishCollectionByMenu(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(publishCollectionByMenuEvent)
    }

  }

  "shareCollectionByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.shareCollectionByMenu(collectionName).mustRightUnit

      there was one(mockTrackServices).trackEvent(shareCollectionByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.shareCollectionByMenu(collectionName).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(shareCollectionByMenuEvent)
    }

  }

  "openAppFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(openAppFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(openAppFromCollectionEvent)
    }

  }

  "addAppToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addAppToCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(addAppEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.addAppToCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(addAppEvent)
    }

  }

  "removeFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.removeFromCollection(entertainmentPackageName, entertainmentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(removeEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.removeFromCollection(entertainmentPackageName, entertainmentCategory).mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(removeEvent)
    }

  }

}
