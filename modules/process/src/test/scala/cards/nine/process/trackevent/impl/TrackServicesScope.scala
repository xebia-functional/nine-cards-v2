package cards.nine.process.trackevent.impl

import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait TrackServicesScope
  extends Scope
  with Mockito{

    val trackServicesException = TrackServicesException("Irrelevant message")

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

}
