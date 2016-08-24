package com.fortysevendeg.ninecardslauncher.process.userv1

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

case class UserV1Exception(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsUserV1Exception {
  implicit def userConfigException = (t: Throwable) => UserV1Exception(t.getMessage, Option(t))
}