package com.fortysevendeg.ninecardslauncher.services.drive

import scalaz.Scalaz._

case class DriveServiceException(
  message: String,
  statusCode: Option[Int] = None,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause map initCause

}

trait ImplicitsDriveServiceExceptions {
  implicit def googleDriveExceptionConverter = (t: Throwable) => DriveServiceException(t.getMessage, cause = t.some)
}