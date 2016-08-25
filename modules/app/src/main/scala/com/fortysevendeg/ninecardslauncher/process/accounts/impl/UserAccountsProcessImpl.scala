package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.process.accounts._
import com.fortysevendeg.ninecardslauncher.process.social.Conversions
import com.fortysevendeg.ninecardslauncher.services.accounts.AccountsServices
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, GoogleAccount}
import macroid.{ActivityContextWrapper, ContextWrapper}

class UserAccountsProcessImpl(accountsServices: AccountsServices)
  extends UserAccountsProcess
  with ImplicitsSocialProfileProcessExceptions
  with Conversions {

  override def getGoogleAccounts(implicit contextWrapper: ContextWrapper) =
    accountsServices.getAccounts(Some(GoogleAccount)).map(_.map(_.accountName)).resolve[AccountsProcessException]

  override def getAuthToken(accountName: String, scope: String)(implicit contextWrapper: ActivityContextWrapper) =
    accountsServices.getAuthToken(Account(GoogleAccount.value, accountName), scope).resolve[AccountsProcessException]

  override def invalidateToken(token: String)(implicit contextWrapper: ContextWrapper) =
    accountsServices.invalidateToken(GoogleAccount.value, token).resolve[AccountsProcessException]
}
