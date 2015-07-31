package com.fortysevendeg.rest.client

case class ServiceClientException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}