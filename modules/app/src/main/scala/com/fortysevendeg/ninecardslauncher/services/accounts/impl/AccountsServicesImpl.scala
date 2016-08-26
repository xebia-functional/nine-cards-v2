package com.fortysevendeg.ninecardslauncher.services.accounts.impl

import android.accounts.{AccountManager, OperationCanceledException, Account => AndroidAccount}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, AccountType}
import com.fortysevendeg.ninecardslauncher.services.accounts._
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

    TaskService {
      Task {
        Xor.catchNonFatal(getAccounts(getAccountManager) map toServiceAccount) leftMap {
          case e: SecurityException => AccountsServicesPermissionException(e.getMessage, Some(e))
          case t: Throwable => AccountsServicesExceptionImpl(t.getMessage, Some(t))
        }
      }
    }
  }

  override def getAuthToken(account: Account, scope: String)(implicit contextWrapper: ActivityContextWrapper): TaskService[String] =
    TaskService {
      Task {
        Xor.catchNonFatal {
          val activity = contextWrapper.original.get.getOrElse(throw new IllegalStateException("Activity instance is null"))
          val result = getAccountManager.getAuthToken(account.toAndroid, scope, javaNull, activity, javaNull, javaNull).getResult
          Option(result.getString(AccountManager.KEY_AUTHTOKEN)) getOrElse (throw new RuntimeException("Received null token"))
        } leftMap {
          case e: OperationCanceledException => AccountsServicesOperationCancelledException(e.getMessage, Some(e))
          case t: Throwable => AccountsServicesExceptionImpl(t.getMessage, Some(t))
        }
      }
    }

  override def invalidateToken(accountType: String, token: String)(implicit contextWrapper: ContextWrapper): TaskService[Unit] =
    TaskService {
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