package com.fortysevendeg.ninecardslauncher.api.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException


case class HttpClientException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsHttpClientExceptions {
  implicit def httpClientExceptionConverter = (t: Throwable) => HttpClientException(t.getMessage, Option(t))
}