package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import scalaz.Scalaz._

case class AuthTokenOperationCancelledException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause foreach initCause
}

case class AuthTokenException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause foreach initCause
}

case class ServiceConnectionException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause foreach initCause
}

trait ImplicitsWizardTasksExceptions {
  implicit def wizardTasksExceptionConverter = (t: Throwable) => ServiceConnectionException(t.getMessage, cause = t.some)
}