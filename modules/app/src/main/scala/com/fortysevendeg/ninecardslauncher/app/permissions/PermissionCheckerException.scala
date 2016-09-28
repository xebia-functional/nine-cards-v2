package com.fortysevendeg.ninecardslauncher.app.permissions

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

case class PermissionCheckerException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsPermissionCheckerException {
  implicit def permissionCheckerExceptionConverter = (t: Throwable) => PermissionCheckerException(t.getMessage, Option(t))
}