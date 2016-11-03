package cards.nine.process.trackevent.impl

import cards.nine.services.track.TrackServices

trait TrackEventDependencies {
  val trackServices: TrackServices
}
