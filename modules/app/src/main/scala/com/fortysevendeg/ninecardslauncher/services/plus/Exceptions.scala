package com.fortysevendeg.ninecardslauncher.services.plus

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException
import com.google.android.gms.common.ConnectionResult

case class GooglePlusServicesException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

case class GooglePlusConnectionSuspendedServicesException(message: String, googleCauseCode: Int, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

case class GooglePlusConnectionFailedServicesException(message: String, connectionResult: Option[ConnectionResult], cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsGooglePlusProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => GooglePlusServicesException(t.getMessage, Option(t))

}