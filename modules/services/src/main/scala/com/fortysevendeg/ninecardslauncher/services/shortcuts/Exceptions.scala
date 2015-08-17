package com.fortysevendeg.ninecardslauncher.services.shortcuts

import scalaz.Scalaz._

case class ShortCutServicesException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsShortCutsExceptions {
  implicit def shortCutServicesException = (t: Throwable) => ShortCutServicesException(t.getMessage, t.some)
}