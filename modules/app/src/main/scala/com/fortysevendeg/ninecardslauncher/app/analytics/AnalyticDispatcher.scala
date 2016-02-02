package com.fortysevendeg.ninecardslauncher.app.analytics

import android.content.Context
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.analytics.{GoogleAnalytics, HitBuilders}

trait AnalyticDispatcher {

  def getApplicationContext: Context

  lazy val tracker = {
    val track = GoogleAnalytics
      .getInstance(getApplicationContext)
      .newTracker(getApplicationContext.getString(R.string.google_analytics_id))
    track.setAppName(getApplicationContext.getString(R.string.app_name))
    track.enableAutoActivityTracking(false)
    track
  }

  def !>(screen: Screen): Unit = {
    tracker.setScreenName(screen.name)
    val event = new HitBuilders.EventBuilder()
    tracker.send(event.build())
  }

  def !>>(trackEvent: TrackEvent): Unit = {
    tracker.setScreenName(trackEvent.screen.name)
    val event = new HitBuilders.EventBuilder()
    event.setCategory(trackEvent.category.name)
    event.setAction(trackEvent.action.name)
    trackEvent.label foreach (l => event.setLabel(l.name))
    trackEvent.value foreach (v => event.setValue(v.value))
    tracker.send(event.build())
  }

}

case class TrackEvent(
   screen: Screen,
   category: Category,
   action: Action,
   label: Option[Label] = None,
   value: Option[Value] = None)