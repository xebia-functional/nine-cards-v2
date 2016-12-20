package cards.nine.app.ui.launcher.exceptions

import cards.nine.commons.services.TaskService.NineCardException

case class SpaceException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsSpaceException {
  implicit def momentException =
    (t: Throwable) => SpaceException(t.getMessage, Option(t))
}

case class LoadDataException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsLoadDataException {
  implicit def loadDataExceptionConverter =
    (t: Throwable) => LoadDataException(t.getMessage, Option(t))
}

case class ChangeMomentException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsChangeMomentException {
  implicit def changeMomentExceptionConverter =
    (t: Throwable) => ChangeMomentException(t.getMessage, Option(t))
}
