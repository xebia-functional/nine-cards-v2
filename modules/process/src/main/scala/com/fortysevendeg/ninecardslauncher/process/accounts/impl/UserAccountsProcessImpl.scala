package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import android.accounts.{AccountManager, AccountManagerCallback, AccountManagerFuture, OperationCanceledException}
import android.os.Bundle
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.accounts._
import monix.eval.Task
import monix.execution.Cancelable

import scala.util.{Failure, Success, Try}

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
      Task.async[UserAccountsProcessException Either String] { (scheduler, callback) =>
        val activity = contextSupport.getActivity.getOrElse(throw new IllegalStateException("Activity instance is null"))
        val androidAccount = new android.accounts.Account(accountName, googleAccountType)
        contextSupport.getAccountManager.getAuthToken(androidAccount, scope, javaNull, activity, new AccountManagerCallback[Bundle] {
          override def run(future: AccountManagerFuture[Bundle]): Unit = {
            Try {
              val bundle = future.getResult
              Option(bundle.getString(AccountManager.KEY_AUTHTOKEN)) getOrElse (throw new RuntimeException("Received null token"))
            } match {
              case Success(token) =>
                callback(Success(Right(token)))
              case Failure(e: OperationCanceledException) =>
                callback(Success(Left(UserAccountsProcessOperationCancelledException(e.getMessage, Some(e)))))
              case Failure(e: SecurityException) =>
                callback(Success(Left(UserAccountsProcessPermissionException(e.getMessage, Some(e)))))
              case Failure(e) =>
                callback(Success(Left(UserAccountsProcessExceptionImpl(e.getMessage, Some(e)))))
            }
          }
        }, javaNull)
        Cancelable.empty
      }
    }
  }

  override def invalidateToken(token: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[UserAccountsProcessExceptionImpl](contextSupport.getAccountManager.invalidateAuthToken(googleAccountType, token))
    }
}
