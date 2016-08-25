package com.fortysevendeg.ninecardslauncher.process.accounts

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServicesOperationCancelledException, AccountsServicesPermissionException}
import com.fortysevendeg.ninecardslauncher.services.plus.GooglePlusServicesException

import scalaz.Scalaz._

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

trait ImplicitsSocialProfileProcessExceptions {

  implicit def accountsProcessExceptionConverter = (throwable: Throwable) =>
    throwable match {
      case e: AccountsServicesPermissionException =>
        AccountsProcessPermissionException(e.message, Some(e))
      case e: AccountsServicesOperationCancelledException =>
        AccountsProcessOperationCancelledException(e.message, Some(e))
      case e =>
        AccountsProcessExceptionImpl(e.getMessage, Option(e))
    }

}