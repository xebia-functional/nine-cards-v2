package com.fortysevendeg.ninecardslauncher.services.widgets

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class WidgetServicesException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsWidgetsExceptions {
  implicit def widgetServicesException = (t: Throwable) => WidgetServicesException(t.getMessage, Option(t))
}