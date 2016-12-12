package cards.nine.process.accounts

import cards.nine.commons.services.TaskService.NineCardException

trait UserAccountsProcessException extends NineCardException

case class UserAccountsProcessExceptionImpl(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

case class UserAccountsProcessPermissionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

case class UserAccountsProcessOperationCancelledException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

trait ImplicitsAccountsProcessExceptions {

  implicit def accountsServicesExceptionConverter =
    (t: Throwable) => UserAccountsProcessExceptionImpl(t.getMessage, Option(t))
}