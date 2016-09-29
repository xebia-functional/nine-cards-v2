package com.fortysevendeg.ninecardslauncher.process.accounts.impl

import android.accounts.{Account, AccountManager, AccountManagerCallback, AccountManagerFuture, OperationCanceledException}
import android.app.Activity
import android.os.Bundle
import cats.syntax.either._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons._
import cards.nine.commons.contexts.{ActivityContextSupport, ContextSupport}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.accounts.{UserAccountsProcessException, _}
import cards.nine.services.permissions.{PermissionDenied, PermissionGranted, PermissionsServices}
import monix.eval.{Callback, Task}
import monix.execution.Cancelable

import scala.util.{Failure, Success, Try}

class UserAccountsProcessImpl(permissionsServices: PermissionsServices)
  extends UserAccountsProcess
  with ImplicitsAccountsProcessExceptions {

  val googleAccountType = "com.google"

  override def getGoogleAccounts(implicit contextSupport: ContextSupport) = {

    def safeNullArrayToSeq[T](array: Array[T]): Seq[T] = Option(array) map (_.toSeq) getOrElse Seq.empty

    def getAccounts: Seq[Account] =
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

    def readAuthToken(callback: Callback[Either[UserAccountsProcessException, String]], activity: Activity): Unit = {
      val androidAccount = new Account(accountName, googleAccountType)
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
    }

    TaskService {
      Task.async[UserAccountsProcessException Either String] { (scheduler, callback) =>
        contextSupport.getActivity match {
          case Some(activity) => readAuthToken(callback, activity)
          case None => callback(Success(Left(UserAccountsProcessExceptionImpl("Activity instance is null", None))))
        }
        Cancelable.empty
      }
    }
  }

  override def invalidateToken(token: String)(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[UserAccountsProcessExceptionImpl](contextSupport.getAccountManager.invalidateAuthToken(googleAccountType, token))
    }

  override def havePermission(permission: AppPermission)(implicit contextSupport: ContextSupport) =
    havePermissions(Seq(permission)) map (findPermissionOrFalse(permission, _))

  override def havePermissions(permissions: Seq[AppPermission])(implicit contextSupport: ContextSupport) =
    permissionsServices.checkPermissions(permissions.map(_.value))
      .map { result =>
        permissions map { permission =>
          val permissionResult = result.getOrElse(permission.value, PermissionDenied)
          PermissionResult(permission, permissionResult == PermissionGranted)
        }
      }
      .resolve[UserAccountsProcessExceptionImpl]

  override def shouldRequestPermission(permission: AppPermission)(implicit contextSupport: ActivityContextSupport) =
    shouldRequestPermissions(Seq(permission)) map (findPermissionOrFalse(permission, _))

  override def shouldRequestPermissions(permissions: Seq[AppPermission])(implicit contextSupport: ActivityContextSupport) =
    permissionsServices.shouldShowRequestPermissions(permissions.map(_.value))
      .map { result =>
        permissions map { permission =>
          PermissionResult(permission, result.getOrElse(permission.value, false))
        }
      }

  override def requestPermission(permissionRequestCode: Int, permission: AppPermission)(implicit contextSupport: ActivityContextSupport) =
    requestPermissions(permissionRequestCode, Seq(permission))

  override def requestPermissions(permissionRequestCode: Int, permissions: Seq[AppPermission])(implicit contextSupport: ActivityContextSupport) =
    permissionsServices.requestPermissions(permissionRequestCode, permissions.map(_.value))
      .resolve[UserAccountsProcessExceptionImpl]

  def parsePermissionsRequestResult(permissions: Array[String], grantResults: Array[Int]) = {

    def parsePermission(value: String): Option[AppPermission] = AppPermission.values.find(_.value == value)

    permissionsServices.readPermissionsRequestResult(permissions.toSeq, grantResults.toSeq)
      .map { result =>
        permissions flatMap { permission =>
          parsePermission(permission) map (p => PermissionResult(p, result.getOrElse(p.value, PermissionDenied) == PermissionGranted))
        }
      }
  }

  private[this] def findPermissionOrFalse(permission: AppPermission, permissions: Seq[PermissionResult]): PermissionResult =
    permissions.find(_.permission == permission) match {
      case Some(result) => result
      case None => PermissionResult(permission, result = false)
    }

}
