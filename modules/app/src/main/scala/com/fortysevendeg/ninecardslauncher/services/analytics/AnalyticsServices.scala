package com.fortysevendeg.ninecardslauncher.services.analytics

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

trait AnalyticsServices {

  /**
    * Track event in Google Analytics
    * @throws AnalyticsException if there was an error with the request GoogleAnalytics
    */
  def trackEvent(event: AnalyticEvent): TaskService[Unit]
}
