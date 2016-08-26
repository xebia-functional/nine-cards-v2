package com.fortysevendeg.ninecardslauncher.process.accounts

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

trait AccountsProcessException extends NineCardException

case class AccountsProcessExceptionImpl(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
    with AccountsProcessException {
  cause map initCause
}

case class AccountsProcessPermissionException(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
    with AccountsProcessException {
  cause map initCause
}

case class AccountsProcessOperationCancelledException(message: String, cause: Option[Throwable])
  extends RuntimeException(message)
    with AccountsProcessException {
  cause map initCause
}