package com.fortysevendeg.ninecardslauncher.process.social

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException
import com.fortysevendeg.ninecardslauncher.process.commons.ConnectionSuspendedCause
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