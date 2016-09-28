package cards.nine.process.intents

import cards.nine.commons.services.TaskService.NineCardException

case class LauncherExecutorProcessException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class LauncherExecutorProcessPermissionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}