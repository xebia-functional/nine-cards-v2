package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class ApiServiceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ApiServiceV1ConfigurationException(message: String)
  extends RuntimeException(message)
  with NineCardException {
  override def cause: Option[Throwable] = None
}

case class ApiServiceConfigurationException(message: String)
  extends RuntimeException(message)
  with NineCardException {
  override def cause: Option[Throwable] = None
}

trait ImplicitsApiServiceExceptions {
  implicit def apiServiceExceptionConverter = (t: Throwable) => ApiServiceException(t.getMessage, Option(t))
}
