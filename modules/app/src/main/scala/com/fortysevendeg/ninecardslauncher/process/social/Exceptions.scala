package com.fortysevendeg.ninecardslauncher.process.social

import scalaz.Scalaz._

case class SocialProfileProcessException(
  message: String,
  cause: Option[Throwable] = None) extends RuntimeException(message) {

  cause foreach initCause

}

trait ImplicitsSocialProfileProcessExceptions {

  implicit def googlePlusExceptionConverter = (t: Throwable) => SocialProfileProcessException(t.getMessage, t.some)

}