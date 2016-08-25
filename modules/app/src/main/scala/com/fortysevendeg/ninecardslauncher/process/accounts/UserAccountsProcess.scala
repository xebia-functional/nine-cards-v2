package com.fortysevendeg.ninecardslauncher.process.accounts

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import macroid.{ActivityContextWrapper, ContextWrapper}

trait UserAccountsProcess {

  /**
    * Get the Google accounts in the device.
    * @return a sequence of accounts
    * @throws AccountsProcessPermissionException if the user didn't grant permission for reading the accounts
    * @throws AccountsProcessException if the service found a problem getting the accounts
    */
  def getGoogleAccounts(implicit contextWrapper: ContextWrapper): CatsService[Seq[String]]

  /**
    * Get the auth token associated to the specified account and token
    * @param accountName the account email
    * @param scope the scope
    * @return the token
    * @throws AccountsProcessOperationCancelledException if the user cancelled the token request
    * @throws AccountsProcessException if the service found a problem getting the token
    */
  def getAuthToken(accountName: String, scope: String)(implicit contextWrapper: ActivityContextWrapper): CatsService[String]

  /**
    * Invalidates the token
    * @param token the token to invalidate
    * @throws AccountsProcessException if the service found a problem invalidating the token
    */
  def invalidateToken(token: String)(implicit contextWrapper: ContextWrapper): CatsService[Unit]

}
