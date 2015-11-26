package com.fortysevendeg.ninecardslauncher.process.device

import scalaz.Scalaz._

case class ResetException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class AppException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class CreateBitmapException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class ShortcutException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class ContactException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class WidgetException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class CallException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsDeviceException {
  implicit def resetException = (t: Throwable) => ResetException(t.getMessage, t.some)

  implicit def appException = (t: Throwable) => AppException(t.getMessage, t.some)

  implicit def createBitmapException = (t: Throwable) => CreateBitmapException(t.getMessage, t.some)

  implicit def shortcutException = (t: Throwable) => ShortcutException(t.getMessage, t.some)

  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, t.some)

  implicit def widgetException = (t: Throwable) => WidgetException(t.getMessage, t.some)

  implicit def callException = (t: Throwable) => CallException(t.getMessage, t.some)
}