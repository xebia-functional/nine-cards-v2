package com.fortysevendeg.ninecardslauncher.services.analytics

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class AnalyticsException(message: String,cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsAnalyticsException {
  implicit def analyticsExceptionConverter = (t: Throwable) => AnalyticsException(t.getMessage, Option(t))
}