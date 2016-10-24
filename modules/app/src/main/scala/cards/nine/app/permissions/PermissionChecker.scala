package cards.nine.app.permissions

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import macroid.ActivityContextWrapper

class PermissionChecker extends ImplicitsPermissionCheckerException {

  import PermissionChecker._

  private[this] def parsePermission(value: String): Option[AppPermission] =
    value match {
      case GetAccounts.value => Some(GetAccounts)
      case ReadContacts.value => Some(ReadContacts)
      case ReadCallLog.value => Some(ReadCallLog)
      case CallPhone.value => Some(CallPhone)
      case _ => None
    }

  def havePermission(permission: AppPermission)(implicit contextWrapper: ActivityContextWrapper): Boolean =
    ContextCompat.checkSelfPermission(contextWrapper.bestAvailable, permission.value) match {
      case PackageManager.PERMISSION_GRANTED => true
      case _ => false
    }

  def havePermissions(permissions: Seq[AppPermission])(implicit contextWrapper: ActivityContextWrapper): Seq[PermissionResult] =
    permissions map (permission => PermissionResult(permission, havePermission(permission)))

  def shouldRequestPermission(permission: AppPermission)(implicit contextWrapper: ActivityContextWrapper): Boolean =
    contextWrapper.original.get exists { activity =>
      ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.value)
    }

  def shouldRequestPermissions(permissions: Seq[AppPermission])(implicit contextWrapper: ActivityContextWrapper): Seq[PermissionResult] =
    permissions map (permission => PermissionResult(permission, havePermission(permission)))

  def requestPermissions(permissionRequestCode: Int, permissions: Seq[AppPermission])(implicit contextWrapper: ActivityContextWrapper): Unit =
    contextWrapper.original.get foreach { activity =>
      ActivityCompat.requestPermissions(activity, (permissions map (_.value)).toArray, permissionRequestCode)
    }

  def requestPermissionTask(permissionRequestCode: Int, permission: AppPermission)(implicit contextWrapper: ActivityContextWrapper): TaskService[Unit] =
    TaskService {
      CatchAll[PermissionCheckerException] {
        requestPermissions(permissionRequestCode, Array(permission))
      }
    }

  def readPermissionRequestResultTask(permissions: Array[String], grantResults: Array[Int]): TaskService[Seq[PermissionResult]] =
    TaskService {
      CatchAll[PermissionCheckerException] {
        (permissions zip grantResults) flatMap {
          case (permission, grantResult) =>
            parsePermission(permission) map (PermissionResult(_, grantResult == PackageManager.PERMISSION_GRANTED))
        }
      }
    }

}

object PermissionChecker {

  sealed trait AppPermission {
    val value: String
  }

  case object GetAccounts extends AppPermission {
    override val value: String = android.Manifest.permission.GET_ACCOUNTS
  }

  case object ReadContacts extends AppPermission {
    override val value: String = android.Manifest.permission.READ_CONTACTS
  }

  case object ReadCallLog extends AppPermission {
    override val value: String = android.Manifest.permission.READ_CALL_LOG
  }

  case object CallPhone extends AppPermission {
    override val value: String = android.Manifest.permission.CALL_PHONE
  }

  case class PermissionResult(permission: AppPermission, result: Boolean) {
    def hasPermission(p: AppPermission): Boolean = permission == p && result
  }

}
