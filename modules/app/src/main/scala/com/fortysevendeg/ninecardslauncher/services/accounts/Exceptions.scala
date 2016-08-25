package com.fortysevendeg.ninecardslauncher.services.accounts

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException

trait AccountsServicesException extends NineCardException

case class AccountsServicesExceptionImpl(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
  with AccountsServicesException {
  cause map initCause
}

case class AccountsServicesPermissionException(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
  with AccountsServicesException {
  cause map initCause
}

case class AccountsServicesOperationCancelledException(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
  with AccountsServicesException {
  cause map initCause
}

trait ImplicitsAccountsServicesExceptions {

  implicit def accountsServicesExceptionConverter =
    (t: Throwable) => AccountsServicesExceptionImpl(t.getMessage, Option(t))

  implicit def accountsServicesPermissionExceptionConverter =
    (t: Throwable) => AccountsServicesPermissionException(t.getMessage, Option(t))

  implicit def accountsServicesOperationCancelledExceptionConverter =
    (t: Throwable) => AccountsServicesOperationCancelledException(t.getMessage, Option(t))
}