package com.fortysevendeg.rest.client

import cards.nine.commons.services.TaskService.NineCardException


case class ServiceClientException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause map initCause
}

trait ImplicitsServiceClientExceptions {

  implicit def accountsServicesExceptionConverter =
    (t: Throwable) => ServiceClientException(t.getMessage, Option(t))
}