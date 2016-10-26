package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.CollectionDetailTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import monix.eval.Task
import org.specs2.mutable.Specification

trait CollectionDetailTrackEventProcessSpecification
  extends Specification
  with CollectionDetailTrackEventTestData
  with TrackServicesScope {

  trait CollectionDetailTrackEventProcessScope
    extends TrackServicesScope
    with CollectionDetailTrackEventTestData {

  }

}

class CollectionDetailTrackEventProcessImplSpec extends CollectionDetailTrackEventProcessSpecification {

  "openAppFromCollection" should {

    "track the app with the right parameters" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(openAppFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(openAppFromCollectionEvent)
    }

  }

  "addAppToCollection" should {

    "track the app with the right parameters" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.addAppToCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(addAppEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.addAppToCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(addAppEvent)
    }

  }

  "removeFromCollection" should {

    "track the app with the right parameters" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.removeFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(removeEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new CollectionDetailTrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.removeFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(removeEvent)
    }

  }

}
