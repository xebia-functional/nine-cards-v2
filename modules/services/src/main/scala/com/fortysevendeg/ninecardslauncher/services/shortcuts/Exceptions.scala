package com.fortysevendeg.ninecardslauncher.services.shortcuts

import scalaz.Scalaz._

case class ShortcutServicesException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsShortcutsExceptions {
  implicit def shortcutServicesException = (t: Throwable) => ShortcutServicesException(t.getMessage, t.some)
}