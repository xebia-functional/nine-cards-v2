package com.fortysevendeg.ninecardslauncher.services.drive

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException
import com.google.android.gms.common.ConnectionResult

sealed trait GoogleDriveError

case object DriveSigInRequired extends GoogleDriveError

case object DriveRateLimitExceeded extends GoogleDriveError

case object DriveResourceNotAvailable extends GoogleDriveError

case class DriveServicesException(message: String, googleDriveError: Option[GoogleDriveError] = None, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

case class DriveConnectionSuspendedServicesException(message: String, googleCauseCode: Int, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

case class DriveConnectionFailedServicesException(message: String, connectionResult: Option[ConnectionResult], cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsDriveServicesExceptions {
  implicit def driveServicesExceptionConverter = (t: Throwable) => DriveServicesException(t.getMessage, cause = Option(t))
}