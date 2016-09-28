package cards.nine.process.social

import cards.nine.commons.services.TaskService.NineCardException
import cards.nine.process.commons.ConnectionSuspendedCause
import com.google.android.gms.common.ConnectionResult


case class SocialProfileProcessException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

case class SocialProfileConnectionSuspendedServicesException(message: String, googleCauseCode: ConnectionSuspendedCause, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

case class SocialProfileConnectionFailedServicesException(message: String, connectionResult: Option[ConnectionResult], cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}