package com.fortysevendeg.rest.client.http

import scalaz.Scalaz._

case class HttpClientException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsHttpClientExceptions {
  implicit def httpClientExceptionConverter = (t: Throwable) => HttpClientException(t.getMessage, t.some)
}