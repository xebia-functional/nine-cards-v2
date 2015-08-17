package com.fortysevendeg.ninecardslauncher.services.shortcuts

import scalaz.Scalaz._

case class ShortCutException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsShortCutsExceptions {
  implicit def shortCutException = (t: Throwable) => ShortCutException(t.getMessage, t.some)
}