package com.fortysevendeg.ninecardslauncher.process.userconfig

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class UserConfigException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsUserConfigException {
  implicit def userConfigException = (t: Throwable) => UserConfigException(t.getMessage, Option(t))
}