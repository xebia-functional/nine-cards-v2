package cards.nine.process.widget

import cards.nine.commons.services.TaskService.NineCardException


case class AppWidgetException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsWidgetException {
  implicit def widgetException = (t: Throwable) => AppWidgetException(t.getMessage, Option(t))
}