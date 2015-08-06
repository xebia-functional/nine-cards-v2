package com.fortysevendeg.ninecardslauncher.process.device

import scalaz.Scalaz._

case class AppCategorizationException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

case class CreateBitmapException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsDeviceException {
  implicit def appCategorizationException = (t: Throwable) => AppCategorizationException(t.getMessage, t.some)

  implicit def createBitmapException = (t: Throwable) => CreateBitmapException(t.getMessage, t.some)

}