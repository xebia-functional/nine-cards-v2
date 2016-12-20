package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.TrackEventValues._
import cards.nine.commons.test.data.trackevent.WidgetTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait WidgetTrackEventProcessSpecification
    extends TaskServiceSpecification
    with WidgetTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class WidgetTrackEventProcessImplSpec extends WidgetTrackEventProcessSpecification {

  "addWidgetToMoment" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.addWidgetToMoment(momentPackageName, momentClassName, momentCategory).mustRightUnit

      there was one(mockTrackServices).trackEvent(momentEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process
        .addWidgetToMoment(momentPackageName, momentClassName, momentCategory)
        .mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(momentEvent)
    }

  }

}
