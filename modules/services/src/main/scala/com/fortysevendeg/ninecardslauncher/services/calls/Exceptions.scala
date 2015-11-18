package com.fortysevendeg.ninecardslauncher.services.calls

import scalaz.Scalaz._

case class CallsServicesException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsCallsExceptions {
  implicit def callsServicesException = (t: Throwable) => CallsServicesException(t.getMessage, t.some)
}