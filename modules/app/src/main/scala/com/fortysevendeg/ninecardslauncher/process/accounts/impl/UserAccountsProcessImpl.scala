package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.accounts._
import com.fortysevendeg.ninecardslauncher.process.social.Conversions
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServices, AccountsServicesOperationCancelledException, AccountsServicesPermissionException}
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, GoogleAccount}
import macroid.{ActivityContextWrapper, ContextWrapper}

class UserAccountsProcessImpl(accountsServices: AccountsServices)
  extends UserAccountsProcess
  with Conversions {

  def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
    case e: AccountsServicesPermissionException =>
      AccountsProcessPermissionException(e.message, Some(e))
    case e: AccountsServicesOperationCancelledException =>
      AccountsProcessOperationCancelledException(e.message, Some(e))
    case e =>
      AccountsProcessExceptionImpl(e.getMessage, Option(e))
  }

  override def getGoogleAccounts(implicit contextWrapper: ContextWrapper) =
    accountsServices.getAccounts(Some(GoogleAccount)).map(_.map(_.accountName)).leftMap(mapServicesException)

  override def getAuthToken(accountName: String, scope: String)(implicit contextWrapper: ActivityContextWrapper) =
    accountsServices.getAuthToken(Account(GoogleAccount.value, accountName), scope).leftMap(mapServicesException)

  override def invalidateToken(token: String)(implicit contextWrapper: ContextWrapper) =
    accountsServices.invalidateToken(GoogleAccount.value, token).leftMap(mapServicesException)
}
