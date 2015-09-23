package com.fortysevendeg.ninecardslauncher.process.device

import scalaz.Scalaz._

case class AppException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class AppCategorizationException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
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

trait ImplicitsDeviceException {
  implicit def appException = (t: Throwable) => AppException(t.getMessage, t.some)

  implicit def appCategorizationException = (t: Throwable) => AppCategorizationException(t.getMessage, t.some)

  implicit def createBitmapException = (t: Throwable) => CreateBitmapException(t.getMessage, t.some)

  implicit def shortcutException = (t: Throwable) => ShortcutException(t.getMessage, t.some)

  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, t.some)

}