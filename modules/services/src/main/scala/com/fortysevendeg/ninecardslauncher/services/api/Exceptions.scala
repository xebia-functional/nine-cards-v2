package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class ApiServiceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsApiServiceExceptions {
  implicit def apiServiceExceptionConverter = (t: Throwable) => ApiServiceException(t.getMessage, Option(t))
}
