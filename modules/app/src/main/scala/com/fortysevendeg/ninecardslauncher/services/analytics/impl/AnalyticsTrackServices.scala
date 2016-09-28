package com.fortysevendeg.ninecardslauncher.services.analytics.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.track.{TrackEvent, TrackServicesException, ImplicitsTrackServicesException, TrackServices}
import com.google.android.gms.analytics.{HitBuilders, Tracker}

class AnalyticsTrackServices(tracker: Tracker)
  extends TrackServices
  with ImplicitsTrackServicesException {

  override def trackEvent(event: TrackEvent) = TaskService {
    CatchAll[TrackServicesException] {
      tracker.setScreenName(event.screen)
      val eventBuilder = new HitBuilders.EventBuilder()
      eventBuilder.setCategory(event.category)
      eventBuilder.setAction(event.action)
      event.label foreach eventBuilder.setLabel
      event.value foreach eventBuilder.setValue
      tracker.send(eventBuilder.build())
    }
  }

}
