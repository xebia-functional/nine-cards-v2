package com.fortysevendeg.ninecardslauncher.process.widget

import scalaz.Scalaz._

trait WidgetException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class WidgetExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with WidgetException {
  cause map initCause
}

trait ImplicitsWidgetException {
  implicit def widgetException = (t: Throwable) => WidgetExceptionImpl(t.getMessage, t.some)
}