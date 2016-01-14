package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.services.drive._

import scalaz.Scalaz._

sealed trait CloudStorageError

case object SigInRequired extends CloudStorageError

case object RateLimitExceeded extends CloudStorageError

case object ResourceNotAvailable extends CloudStorageError

case class CloudStorageProcessException(
  message: String,
  cause: Option[Throwable] = None,
  driveError: Option[CloudStorageError] = None) extends RuntimeException(message) {

  cause map initCause

}

trait ImplicitsCloudStorageProcessExceptions {

  implicit def cloudStorageExceptionConverter = (t: Throwable) => t match {
    case e: DriveServicesException =>
      CloudStorageProcessException(
        message = e.message,
        cause = e.some,
        driveError = e.googleDriveError flatMap driveErrorToCloudStorageError)
    case e: CloudStorageProcessException => e
    case _ => CloudStorageProcessException(t.getMessage, t.some)
  }

  private[this] def driveErrorToCloudStorageError(driveError: GoogleDriveError): Option[CloudStorageError] =
    driveError match {
      case DriveSigInRequired => SigInRequired.some
      case DriveRateLimitExceeded => RateLimitExceeded.some
      case DriveResourceNotAvailable => ResourceNotAvailable.some
      case _ => None
    }
}