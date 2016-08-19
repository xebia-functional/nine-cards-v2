package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException


case class ServiceClientException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{

  cause map initCause
}