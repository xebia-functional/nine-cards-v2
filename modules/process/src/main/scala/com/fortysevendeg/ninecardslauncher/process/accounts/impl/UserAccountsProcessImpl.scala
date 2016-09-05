package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.accounts._
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServices, AccountsServicesOperationCancelledException, AccountsServicesPermissionException}
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, GoogleAccount}

class UserAccountsProcessImpl(accountsServices: AccountsServices)
  extends UserAccountsProcess {

  def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
    case e: AccountsServicesPermissionException =>
      AccountsProcessPermissionException(e.message, Some(e))
    case e: AccountsServicesOperationCancelledException =>
      AccountsProcessOperationCancelledException(e.message, Some(e))
    case e =>
      AccountsProcessExceptionImpl(e.getMessage, Option(e))
  }

  override def getGoogleAccounts(implicit contextSupport: ContextSupport) =
    accountsServices.getAccounts(Some(GoogleAccount)).map(_.map(_.accountName)).leftMap(mapServicesException)

  override def getAuthToken(accountName: String, scope: String)(implicit contextSupport: ActivityContextSupport) =
    accountsServices.getAuthToken(Account(GoogleAccount.value, accountName), scope).leftMap(mapServicesException)

  override def invalidateToken(token: String)(implicit contextSupport: ContextSupport) =
    accountsServices.invalidateToken(GoogleAccount.value, token).leftMap(mapServicesException)
}
