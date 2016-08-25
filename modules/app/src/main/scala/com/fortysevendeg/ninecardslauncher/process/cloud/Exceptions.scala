package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException
import com.fortysevendeg.ninecardslauncher.services.drive._

sealed trait CloudStorageError

case object SigInRequired extends CloudStorageError

case object RateLimitExceeded extends CloudStorageError

case object ResourceNotAvailable extends CloudStorageError

case class CloudStorageProcessException(  message: String,  cause: Option[Throwable] = None,  driveError: Option[CloudStorageError] = None)
  extends RuntimeException(message)
  with NineCardException {

  cause foreach initCause

}

trait ImplicitsCloudStorageProcessExceptions {

  implicit def cloudStorageExceptionConverter = (t: Throwable) => t match {
    case e: DriveServicesException =>
      CloudStorageProcessException(
        message = e.message,
        cause = Option(e),
        driveError = e.googleDriveError flatMap driveErrorToCloudStorageError)
    case e: CloudStorageProcessException => e
    case _ => CloudStorageProcessException(t.getMessage, Option(t))
  }

  private[this] def driveErrorToCloudStorageError(driveError: GoogleDriveError): Option[CloudStorageError] =
    driveError match {
      case DriveSigInRequired => Option(SigInRequired)
      case DriveRateLimitExceeded => Option(RateLimitExceeded)
      case DriveResourceNotAvailable => Option(ResourceNotAvailable)
      case _ => None
    }
}