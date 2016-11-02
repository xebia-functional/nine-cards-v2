package cards.nine.process.trackevent.impl

import cards.nine.process.trackevent._
import cards.nine.services.track.TrackServices

class TrackEventProcessImpl(
  val trackServices: TrackServices)
  extends TrackEventProcess
  with TrackEventDependencies
  with CollectionDetailTrackEventProcessImpl
  with LauncherTrackEventProcessImpl
  with ProfileTrackEventProcessImpl
  with WidgetTrackEventProcessImpl
  with ImplicitsTrackEventException


