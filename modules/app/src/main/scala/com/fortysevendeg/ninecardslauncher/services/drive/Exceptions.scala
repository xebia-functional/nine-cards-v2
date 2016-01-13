package com.fortysevendeg.ninecardslauncher.services.drive

import scalaz.Scalaz._

sealed trait GoogleDriveError

case object DriveSigInRequired extends GoogleDriveError

case object DriveRateLimitExceeded extends GoogleDriveError

case object DriveResourceNotAvailable extends GoogleDriveError

case class DriveServiceException(
  message: String,
  googleDriveError: Option[GoogleDriveError] = None,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause map initCause

}

trait ImplicitsDriveServiceExceptions {
  implicit def googleDriveExceptionConverter = (t: Throwable) => DriveServiceException(t.getMessage, cause = t.some)
}