package com.fortysevendeg.ninecardslauncher.services.widgets

import scalaz.Scalaz._

case class WidgetServicesException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsWidgetsExceptions {
  implicit def widgetServicesException = (t: Throwable) => WidgetServicesException(t.getMessage, t.some)
}