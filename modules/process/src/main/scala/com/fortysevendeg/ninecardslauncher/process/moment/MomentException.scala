package com.fortysevendeg.ninecardslauncher.process.moment

import scalaz.Scalaz._

case class MomentException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsMomentException {
  implicit def momentException = (t: Throwable) => MomentException(t.getMessage, t.some)
}