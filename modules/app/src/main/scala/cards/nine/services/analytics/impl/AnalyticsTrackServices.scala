package cards.nine.services.analytics.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.models.TrackEvent
import cards.nine.services.track.{ImplicitsTrackServicesException, TrackServices, TrackServicesException}
import com.google.android.gms.analytics.{HitBuilders, Tracker}

class AnalyticsTrackServices(tracker: Tracker)
  extends TrackServices
  with ImplicitsTrackServicesException {

  override def trackEvent(event: TrackEvent) = TaskService {
    CatchAll[TrackServicesException] {
      tracker.setScreenName(event.screen.name)
      val eventBuilder = new HitBuilders.EventBuilder()
      eventBuilder.setCategory(event.category.name)
      eventBuilder.setAction(event.action.name)
      event.label foreach eventBuilder.setLabel
      event.value.map(_.value) foreach eventBuilder.setValue
      tracker.send(eventBuilder.build())
    }
  }

}
