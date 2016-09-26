package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import android.accounts.{Account => AndroidAccount, AccountManager, OperationCanceledException}
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.accounts._
import monix.eval.Task

class UserAccountsProcessImpl
  extends UserAccountsProcess
  with ImplicitsAccountsProcessExceptions {

  val googleAccountType = "com.google"

  override def getGoogleAccounts(implicit contextSupport: ContextSupport) = {

    def safeNullArrayToSeq[T](array: Array[T]): Seq[T] = Option(array) map (_.toSeq) getOrElse Seq.empty

    def getAccounts: Seq[android.accounts.Account] =
      safeNullArrayToSeq(contextSupport.getAccountManager.getAccountsByType(googleAccountType))

    TaskService {
      Task {
        Either.catchNonFatal(getAccounts map (_.name)) leftMap {
          case e: SecurityException => UserAccountsProcessPermissionException(e.getMessage, Some(e))
          case t: Throwable => UserAccountsProcessExceptionImpl(t.getMessage, Some(t))
        }
      }
    }
  }

  override def getAuthToken(accountName: String, scope: String)(implicit contextSupport: ActivityContextSupport) = {
    TaskService {
      Task {
        Either.catchNonFatal {
          val activity = contextSupport.getActivity.getOrElse(throw new IllegalStateException("Activity instance is null"))
          val result = contextSupport.getAccountManager.getAuthToken(new AndroidAccount(accountName, googleAccountType), scope, javaNull, activity, javaNull, javaNull).getResult
          Option(result.getString(AccountManager.KEY_AUTHTOKEN)) getOrElse (throw new RuntimeException("Received null token"))
        } leftMap {
          case e: OperationCanceledException => UserAccountsProcessOperationCancelledException(e.getMessage, Some(e))
          case t: Throwable => UserAccountsProcessExceptionImpl(t.getMessage, Some(t))
        }
      }
    }
  }

  override def invalidateToken(token: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[UserAccountsProcessExceptionImpl](contextSupport.getAccountManager.invalidateAuthToken(googleAccountType, token))
    }
}
