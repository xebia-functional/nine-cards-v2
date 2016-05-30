package com.fortysevendeg.ninecardslauncher.process.moment

import scalaz.Scalaz._

trait MomentException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class MomentExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with MomentException {
  cause map initCause
}

trait ImplicitsMomentException {
  implicit def momentException = (t: Throwable) => MomentExceptionImpl(t.getMessage, t.some)
}