package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.trackevent.ProfileTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import monix.eval.Task
import org.specs2.mutable.Specification

trait ProfileTrackEventProcessSpecification
  extends Specification
  with ProfileTrackEventTestData
  with TrackServicesScope {

}

class ProfileTrackEventProcessImplSpec extends ProfileTrackEventProcessSpecification {

  "logout" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.logout().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(logoutEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.logout().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(logoutEvent)
    }

  }

  "showAccountsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.showAccountsContent().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(showAccountsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.showAccountsContent().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(showAccountsContentEvent)
    }

  }

  "copyConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.copyConfiguration().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(copyConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.copyConfiguration().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(copyConfigurationEvent)
    }

  }

  "synchronizeConfiguration" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.synchronizeConfiguration().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(synchronizeConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.synchronizeConfiguration().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(synchronizeConfigurationEvent)
    }

  }

  "changeConfigurationName" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.changeConfigurationName().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(deleteConfigurationEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.changeConfigurationName().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(deleteConfigurationEvent)
    }

  }

  "showPublicationsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.showPublicationsContent().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(showPublicationsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.showPublicationsContent().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(showPublicationsContentEvent)
    }

  }

  "addToMyCollectionsFromProfile" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.addToMyCollectionsFromProfile(publication).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(addToMyCollectionsFromProfileEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.addToMyCollectionsFromProfile(publication).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(addToMyCollectionsFromProfileEvent)
    }

  }

  "shareCollectionFromProfile" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.shareCollectionFromProfile(publication).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(shareCollectionFromProfileEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.shareCollectionFromProfile(publication).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(shareCollectionFromProfileEvent)
    }

  }

  "showSubscriptionsContent" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.showSubscriptionsContent().value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(showSubscriptionsContentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.showSubscriptionsContent().value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(showSubscriptionsContentEvent)
    }

  }

  "subscribeToCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.subscribeToCollection(sharedCollectionId).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(subscribeToCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.subscribeToCollection(sharedCollectionId).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(subscribeToCollectionEvent)
    }

  }

  "unsubscribeFromCollection" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.unsubscribeFromCollection(sharedCollectionId).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(unsubscribeFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.unsubscribeFromCollection(sharedCollectionId).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(unsubscribeFromCollectionEvent)
    }

  }

}
