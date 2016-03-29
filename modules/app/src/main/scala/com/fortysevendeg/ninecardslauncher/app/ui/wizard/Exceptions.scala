package com.fortysevendeg.ninecardslauncher.app.ui.wizard

trait AuthTokenOperationCancelledException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class AuthTokenOperationCancelledExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with AuthTokenOperationCancelledException {
  cause foreach initCause
}

trait AuthTokenException
  extends RuntimeException {

  val message: String

  val cause: Option[Throwable]

}

case class AuthTokenExceptionImpl(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with AuthTokenException {
  cause foreach initCause
}

trait ImplicitsAuthTokenException {
  implicit def authTokenExceptionConverter = (t: Throwable) => AuthTokenExceptionImpl(t.getMessage, Some(t))
}