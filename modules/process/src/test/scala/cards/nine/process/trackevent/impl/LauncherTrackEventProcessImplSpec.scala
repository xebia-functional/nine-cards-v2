package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.LauncherTrackEventTestData
import cards.nine.models.types.{AppCategory, Game}
import cards.nine.process.trackevent.TrackEventException
import monix.eval.Task
import org.specs2.mutable.Specification

trait LauncherTrackEventProcessSpecification
  extends Specification
  with LauncherTrackEventTestData
  with TrackServicesScope {

  trait LauncherTrackEventProcessScope
    extends TrackServicesScope {

  }

}

class LauncherTrackEventProcessImplSpec extends LauncherTrackEventProcessSpecification {

  "openAppFromAppDrawer" should {

    "track the app with the right parameters" in new LauncherTrackEventProcessScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(openAppEntertainmentEvent)
    }

    "track the app with the right parameters when the package is a game" in new LauncherTrackEventProcessScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.openAppFromAppDrawer(gamePackageName, gameCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(openAppGameEvent)
      there was one(mockTrackServices).trackEvent(openAppGameEvent.copy(category = AppCategory(Game)))
    }

    "return a Left[TrackEventException] when the service return an exception" in new LauncherTrackEventProcessScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.openAppFromAppDrawer(entertainmentPackageName, entertainmentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(openAppEntertainmentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception in the second call" in new LauncherTrackEventProcessScope {

      mockTrackServices.trackEvent(any) returns
        (TaskService(Task(Right((): Unit))), TaskService(Task(Left(trackServicesException))))

      val result = process.openAppFromAppDrawer(gamePackageName, gameCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(openAppGameEvent)
      there was one(mockTrackServices).trackEvent(openAppGameEvent.copy(category = AppCategory(Game)))
    }

  }

}
