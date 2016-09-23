package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException
import com.fortysevendeg.ninecardslauncher.process.commons.ConnectionSuspendedCause
import com.google.android.gms.common.ConnectionResult

sealed trait CloudStorageError

case object SigInRequired extends CloudStorageError

case object RateLimitExceeded extends CloudStorageError

case object ResourceNotAvailable extends CloudStorageError

case class CloudStorageProcessException(message: String, cause: Option[Throwable] = None, driveError: Option[CloudStorageError] = None)
  extends RuntimeException(message)
  with NineCardException {

  cause foreach initCause

}

case class CloudStorageConnectionSuspendedServicesException(message: String, googleCauseCode: ConnectionSuspendedCause, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

case class CloudStorageConnectionFailedServicesException(message: String, connectionResult: Option[ConnectionResult], cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}