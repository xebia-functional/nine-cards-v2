package cards.nine.process.trackevent.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.WidgetTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import monix.eval.Task
import org.specs2.mutable.Specification

trait WidgetTrackEventProcessSpecification
  extends Specification
  with WidgetTrackEventTestData
  with TrackServicesScope {

}

class WidgetTrackEventProcessImplSpec extends WidgetTrackEventProcessSpecification {

  "addWidgetToMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Right((): Unit)))

      val result = process.addWidgetToMoment(momentPackageName, momentClassName, momentCategory).value.run
      result shouldEqual Right((): Unit)

      there was one(mockTrackServices).trackEvent(momentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns TaskService(Task(Left(trackServicesException)))

      val result = process.addWidgetToMoment(momentPackageName, momentClassName, momentCategory).value.run
      result must beLike {
        case Left(e) => e must beAnInstanceOf[TrackEventException]
      }

      there was one(mockTrackServices).trackEvent(momentEvent)
    }

  }
 

}