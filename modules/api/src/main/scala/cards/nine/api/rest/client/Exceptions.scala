package com.fortysevendeg.ninecardslauncher.api.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException


case class ServiceClientException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause map initCause
}

trait ImplicitsServiceClientExceptions {

  implicit def accountsServicesExceptionConverter =
    (t: Throwable) => ServiceClientException(t.getMessage, Option(t))
}