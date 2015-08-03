package com.fortysevendeg.rest.client.http

import scalaz.Scalaz._

trait HttpClientException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class HttpClientExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with HttpClientException {
  cause map initCause
}

trait ImplicitsHttpClientExceptions {
  implicit def httpClientExceptionConverter = (t: Throwable) => HttpClientExceptionImpl(t.getMessage, t.some)
}