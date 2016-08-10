package com.fortysevendeg.ninecardslauncher.app.ui.launcher.exceptions

import scalaz.Scalaz._

trait SpaceException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class SpaceExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with SpaceException {
  cause map initCause
}

trait ImplicitsSpaceException {
  implicit def momentException = (t: Throwable) => SpaceExceptionImpl(t.getMessage, t.some)
}