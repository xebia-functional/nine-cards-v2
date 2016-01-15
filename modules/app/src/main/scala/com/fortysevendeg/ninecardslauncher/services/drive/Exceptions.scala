package com.fortysevendeg.ninecardslauncher.services.drive

import scalaz.Scalaz._

sealed trait GoogleDriveError

case object DriveSigInRequired extends GoogleDriveError

case object DriveRateLimitExceeded extends GoogleDriveError

case object DriveResourceNotAvailable extends GoogleDriveError

case class DriveServicesException(
  message: String,
  googleDriveError: Option[GoogleDriveError] = None,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause map initCause

}

trait ImplicitsDriveServicesExceptions {
  implicit def driveServicesExceptionConverter = (t: Throwable) => DriveServicesException(t.getMessage, cause = t.some)
}