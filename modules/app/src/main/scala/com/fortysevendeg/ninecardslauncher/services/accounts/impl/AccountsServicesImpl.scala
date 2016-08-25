package com.fortysevendeg.ninecardslauncher.services.accounts.impl

import android.accounts.{AccountManager, OperationCanceledException, Account => AndroidAccount}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, AccountType}
import com.fortysevendeg.ninecardslauncher.services.accounts.{AccountsServices, AccountsServicesExceptionImpl, ImplicitsAccountsServicesExceptions}
import macroid.{ActivityContextWrapper, ContextWrapper}

import scalaz.concurrent.Task

class AccountsServicesImpl
  extends AccountsServices
  with ImplicitsAccountsServicesExceptions {

  import AccountsServicesImpl._

  def getAccountManager(implicit contextWrapper: ContextWrapper): AccountManager =
    Option(AccountManager.get(contextWrapper.bestAvailable)) match {
      case Some(am) => am
      case None => throw new IllegalStateException("Can't access to AccountManager")
    }

  override def getAccounts(maybeAccountType: Option[AccountType])(implicit contextWrapper: ContextWrapper) = {

    def safeNullArrayToSeq[T](array: Array[T]): Seq[T] = Option(array) map (_.toSeq) getOrElse Seq.empty

    def getAccounts(accountManager: AccountManager): Seq[android.accounts.Account] = maybeAccountType match {
      case Some(accountType) => safeNullArrayToSeq(accountManager.getAccountsByType(accountType.value))
      case None => safeNullArrayToSeq(accountManager.getAccounts)
    }

    def toServiceAccount(androidAccount: AndroidAccount): Account =
      Account(accountType = androidAccount.`type`, accountName = androidAccount.name)

    CatsService {
      Task {
        Xor.catchNonFatal(getAccounts(getAccountManager) map toServiceAccount) leftMap {
          case e: SecurityException => accountsServicesPermissionExceptionConverter(e)
          case t: Throwable => accountsServicesExceptionConverter(t)
        }
      }
    }
  }

  override def getAuthToken(account: Account, scope: String)(implicit contextWrapper: ActivityContextWrapper): CatsService[String] =
    CatsService {
      Task {
        Xor.catchNonFatal {
          val result = getAccountManager.getAuthToken(account.toAndroid, scope, javaNull, contextWrapper.getOriginal, javaNull, javaNull).getResult
          Option(result.getString(AccountManager.KEY_AUTHTOKEN)) getOrElse (throw new RuntimeException("Received null token"))
        } leftMap {
          case e: OperationCanceledException => accountsServicesOperationCancelledExceptionConverter(e)
          case t: Throwable => accountsServicesExceptionConverter(t)
        }
      }
    }

  override def invalidateToken(accountType: String, token: String)(implicit contextWrapper: ContextWrapper): CatsService[Unit] =
    CatsService {
      Task {
        XorCatchAll[AccountsServicesExceptionImpl](getAccountManager.invalidateAuthToken(accountType, token))
      }
    }
}

object AccountsServicesImpl {

  implicit class AccountOps(account: Account) {

    def toAndroid: AndroidAccount = new AndroidAccount(account.accountType, account.accountName)

  }

}