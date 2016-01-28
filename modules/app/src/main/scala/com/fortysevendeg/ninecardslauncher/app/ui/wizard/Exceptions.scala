package com.fortysevendeg.ninecardslauncher.app.ui.wizard

case class AuthTokenOperationCancelledException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause foreach initCause
}

case class AuthTokenException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause foreach initCause
}