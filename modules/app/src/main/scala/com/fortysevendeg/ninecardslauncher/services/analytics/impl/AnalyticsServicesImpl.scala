package com.fortysevendeg.ninecardslauncher.services.analytics.impl

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.analytics.{AnalyticEvent, AnalyticsException, AnalyticsServices, ImplicitsAnalyticsException}
import com.google.android.gms.analytics.{HitBuilders, Tracker}


class AnalyticsServicesImpl(tracker: Tracker)
  extends AnalyticsServices
  with ImplicitsAnalyticsException {

  override def trackEvent(event: AnalyticEvent) = TaskService {
    CatchAll[AnalyticsException] {
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
