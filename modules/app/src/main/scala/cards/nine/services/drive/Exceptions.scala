package cards.nine.services.drive

import cards.nine.commons.services.TaskService.NineCardException
import com.google.android.gms.common.ConnectionResult

sealed trait GoogleDriveError

case object DriveSigInRequired extends GoogleDriveError

case object DriveRateLimitExceeded extends GoogleDriveError

case object DriveResourceNotAvailable extends GoogleDriveError

case class DriveServicesException(
    message: String,
    googleDriveError: Option[GoogleDriveError] = None,
    cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}
