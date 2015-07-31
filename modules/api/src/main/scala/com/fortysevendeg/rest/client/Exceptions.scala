package com.fortysevendeg.rest.client

trait ServiceClientException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class ServiceClientExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with ServiceClientException {

  cause map initCause
}