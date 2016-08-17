package com.fortysevendeg.ninecardslauncher.process.userv1

import scalaz.Scalaz._

case class UserV1Exception(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsUserV1Exception {
  implicit def userConfigException = (t: Throwable) => UserV1Exception(t.getMessage, t.some)
}