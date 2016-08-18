package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

trait ServiceClientException
  extends RuntimeException
    with NineCardException{

  val message: String

  val cause: Option[Throwable]

}

case class ServiceClientExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with ServiceClientException
  with NineCardException{

  cause map initCause
}