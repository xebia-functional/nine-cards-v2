package com.fortysevendeg.ninecardslauncher.services.drive

import scalaz.Scalaz._

case class GoogleDriveException(
  message: String,
  statusCode: Option[Int] = None,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause map initCause

}

trait ImplicitsGoogleDriveExceptions {
  implicit def googleDriveExceptionConverter = (t: Throwable) => GoogleDriveException(t.getMessage, cause = t.some)
}