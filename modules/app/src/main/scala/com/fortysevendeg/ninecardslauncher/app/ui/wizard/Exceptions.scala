package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException


case class AuthTokenOperationCancelledException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause foreach initCause
}

case class AuthTokenException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause foreach initCause
}

trait ImplicitsAuthTokenException {
  implicit def authTokenExceptionConverter = (t: Throwable) => AuthTokenException(t.getMessage, Option(t))
}