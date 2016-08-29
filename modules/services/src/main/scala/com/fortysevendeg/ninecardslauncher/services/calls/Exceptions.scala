package com.fortysevendeg.ninecardslauncher.services.calls

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class CallsServicesException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsCallsExceptions {
  implicit def callsServicesException = (t: Throwable) => CallsServicesException(t.getMessage, Option(t))
}