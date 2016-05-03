package com.fortysevendeg.ninecardslauncher.services.plus

import scalaz.Scalaz._

case class GooglePlusProcessException(
  message: String,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause foreach initCause

}

trait ImplicitsGooglePlusProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => GooglePlusProcessException(t.getMessage, t.some)

}