package com.fortysevendeg.ninecardslauncher.process.user

import scalaz.Scalaz._

case class UserException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsUserException {
  implicit def userException = (t: Throwable) => UserException(t.getMessage, t.some)
}