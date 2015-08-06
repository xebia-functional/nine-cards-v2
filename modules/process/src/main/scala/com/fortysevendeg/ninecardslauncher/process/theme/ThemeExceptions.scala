package com.fortysevendeg.ninecardslauncher.process.theme

import scalaz.Scalaz._

case class ThemeException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}
trait ImplicitsThemeException {
  implicit def themeException = (t: Throwable) => ThemeException(t.getMessage, t.some)
}