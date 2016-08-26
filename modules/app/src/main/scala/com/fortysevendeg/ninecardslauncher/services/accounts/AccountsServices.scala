package com.fortysevendeg.ninecardslauncher.services.accounts

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, AccountType}
import macroid.{ActivityContextWrapper, ContextWrapper}

trait AccountsServices {

  /**
    * Get the accounts in the device.
    * @param maybeAccountType the type of the account. None for all accounts
    * @return a sequence of accounts
    * @throws AccountsServicesPermissionException if the user didn't grant permission for reading the accounts
    * @throws AccountsServicesException if the service found a problem getting the accounts
    */
  def getAccounts(maybeAccountType: Option[AccountType])(implicit contextWrapper: ContextWrapper): TaskService[Seq[Account]]

  /**
    * Get the auth token associated to the specified account and token
    * @param account the account
    * @param scope the scope
    * @return the token
    * @throws AccountsServicesOperationCancelledException if the user cancelled the token request
    * @throws AccountsServicesException if the service found a problem getting the token
    */
  def getAuthToken(account: Account, scope: String)(implicit contextWrapper: ActivityContextWrapper): TaskService[String]

  /**
    * Invalidates the token associated to the specified account
    * @param accountType the account type
    * @param token the token to invalidate
    * @throws AccountsServicesException if the service found a problem invalidating the token
    */
  def invalidateToken(accountType: String, token: String)(implicit contextWrapper: ContextWrapper): TaskService[Unit]

}
