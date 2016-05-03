package com.fortysevendeg.ninecardslauncher.services.plus

import scalaz.Scalaz._

case class GooglePlusServicesException(
  message: String,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause foreach initCause

}

trait ImplicitsGooglePlusProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => GooglePlusServicesException(t.getMessage, t.some)

}