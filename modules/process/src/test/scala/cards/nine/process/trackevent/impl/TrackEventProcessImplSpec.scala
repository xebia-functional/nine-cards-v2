package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.services.track.{TrackServices, TrackServicesException}
import cards.nine.models.types.{AppCategory, Game}
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.process.trackevent.TrackEventException

trait TrackEventProcessSpecification
  extends Specification
  with Mockito
  with TrackEventProcessData {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackEventProcessScope
    extends Scope {

    val mockServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockServices)

  }
}

class TrackEventProcessImplSpec
  extends TrackEventProcessSpecification {

  "openAppFromAppDrawer" should {

    "track the app with the right parameters" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(openAppEntertainmentEvent)
    }

    "track the app with the right parameters when the package is a game" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromAppDrawer(gamePackageName, gameCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(openAppGameEvent)
      there was one(mockServices).trackEvent(openAppGameEvent.copy(category = AppCategory(Game)))
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(openAppEntertainmentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception in the second call" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns
        (TaskService(Task(Right((): Unit))), TaskService(Task(Left(trackServicesException))))

      val result = process.openAppFromAppDrawer(gamePackageName, gameCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(openAppGameEvent)
      there was one(mockServices).trackEvent(openAppGameEvent.copy(category = AppCategory(Game)))
    }

  }

  "openAppFromCollection" should {

    "track the app with the right parameters" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(openAppFromCollectionEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.openAppFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(openAppFromCollectionEvent)
    }

  }

  "addAppToCollection" should {

    "track the app with the right parameters" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.addAppToCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(addAppEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.addAppToCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(addAppEvent)
    }

  }

  "removeFromCollection" should {

    "track the app with the right parameters" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.removeFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(removeEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.removeFromCollection(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(removeEvent)
    }

  }

  "addWidgetToMoment" should {

    "track the app with the right parameters" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.addWidgetToMoment(momentPackageName, momentClassName, momentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockServices).trackEvent(momentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackEventProcessScope {

      mockServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.addWidgetToMoment(momentPackageName, momentClassName, momentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockServices).trackEvent(momentEvent)
    }

  }

}
