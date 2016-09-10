package com.fortysevendeg.ninecardslauncher.services.accounts.impl

import android.accounts.{AccountManager, OperationCanceledException, Account => AndroidAccount}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.services.accounts.models.{Account, AccountType}
import com.fortysevendeg.ninecardslauncher.services.accounts._

import scalaz.concurrent.Task

class AccountsServicesImpl
  extends AccountsServices
  with ImplicitsAccountsServicesExceptions {

  override def getAccounts(maybeAccountType: Option[AccountType])(implicit contextSupport: ContextSupport) = {

    def safeNullArrayToSeq[T](array: Array[T]): Seq[T] = Option(array) map (_.toSeq) getOrElse Seq.empty

    def getAccounts(accountManager: AccountManager): Seq[android.accounts.Account] = maybeAccountType match {
      case Some(accountType) => safeNullArrayToSeq(accountManager.getAccountsByType(accountType.value))
      case None => safeNullArrayToSeq(accountManager.getAccounts)
    }

    def toServiceAccount(androidAccount: AndroidAccount): Account =
      Account(accountType = androidAccount.`type`, accountName = androidAccount.name)

    TaskService {
      Task {
        Xor.catchNonFatal(getAccounts(contextSupport.getAccountManager) map toServiceAccount) leftMap {
          case e: SecurityException => AccountsServicesPermissionException(e.getMessage, Some(e))
          case t: Throwable => AccountsServicesExceptionImpl(t.getMessage, Some(t))
        }
      }
    }
  }

  override def getAuthToken(account: Account, scope: String)(implicit contextSupport: ActivityContextSupport): TaskService[String] =
    TaskService {
      Task {
        Xor.catchNonFatal {
          val activity = contextSupport.getActivity.getOrElse(throw new IllegalStateException("Activity instance is null"))
          val result = contextSupport.getAccountManager.getAuthToken(new AndroidAccount(account.accountName, account.accountType), scope, javaNull, activity, javaNull, javaNull).getResult
          Option(result.getString(AccountManager.KEY_AUTHTOKEN)) getOrElse (throw new RuntimeException("Received null token"))
        } leftMap {
          case e: OperationCanceledException => AccountsServicesOperationCancelledException(e.getMessage, Some(e))
          case t: Throwable => AccountsServicesExceptionImpl(t.getMessage, Some(t))
        }
      }
    }

  override def invalidateToken(accountType: String, token: String)(implicit contextSupport: ContextSupport): TaskService[Unit] =
    TaskService {
      Task {
        CatchAll[AccountsServicesExceptionImpl](contextSupport.getAccountManager.invalidateAuthToken(accountType, token))
      }
    }
}