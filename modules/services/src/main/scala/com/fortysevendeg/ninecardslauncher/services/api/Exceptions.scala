package com.fortysevendeg.ninecardslauncher.services.api

import scalaz.Scalaz._

case class ApiServiceException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsApiServiceExceptions {
  implicit def apiServiceExceptionConverter = (t: Throwable) => ApiServiceException(t.getMessage, t.some)
}
