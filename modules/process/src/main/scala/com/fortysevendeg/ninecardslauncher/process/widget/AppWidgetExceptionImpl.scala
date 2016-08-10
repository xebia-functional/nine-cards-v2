package com.fortysevendeg.ninecardslauncher.process.widget

import scalaz.Scalaz._

trait AppWidgetException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class AppWidgetExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with AppWidgetException {
  cause map initCause
}

trait ImplicitsWidgetException {
  implicit def widgetException = (t: Throwable) => AppWidgetExceptionImpl(t.getMessage, t.some)
}