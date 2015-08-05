package com.fortysevendeg.ninecardslauncher.process.userconfig

import scalaz.Scalaz._

case class UserConfigException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsUserConfigException {
  implicit def userConfigException = (t: Throwable) => UserConfigException(t.getMessage, t.some)
}