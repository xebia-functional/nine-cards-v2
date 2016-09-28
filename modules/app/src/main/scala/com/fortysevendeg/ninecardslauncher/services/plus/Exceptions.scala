package com.fortysevendeg.ninecardslauncher.services.plus

import cards.nine.commons.services.TaskService.NineCardException

case class GooglePlusServicesException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsGooglePlusProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => GooglePlusServicesException(t.getMessage, Option(t))

}