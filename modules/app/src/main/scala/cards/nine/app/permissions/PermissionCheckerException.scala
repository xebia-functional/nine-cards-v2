package cards.nine.app.permissions

import cards.nine.commons.services.TaskService.NineCardException

case class PermissionCheckerException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsPermissionCheckerException {
  implicit def permissionCheckerExceptionConverter = (t: Throwable) => PermissionCheckerException(t.getMessage, Option(t))
}