package com.fortysevendeg.ninecardslauncher.services.widgets

import cards.nine.commons.services.TaskService.NineCardException

case class WidgetServicesException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsWidgetsExceptions {
  implicit def widgetServicesException = (t: Throwable) => WidgetServicesException(t.getMessage, Option(t))
}