package cards.nine.services.persistence

import cards.nine.commons.services.TaskService.NineCardException

case class AndroidIdNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class InstallationNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class PersistenceServiceException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class UserNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsPersistenceServiceExceptions {

  implicit def androidIdNotFoundException =
    (t: Throwable) => AndroidIdNotFoundException(t.getMessage, Option(t))

  implicit def installationNotFoundException =
    (t: Throwable) => InstallationNotFoundException(t.getMessage, Option(t))

  implicit def persistenceServiceException =
    (t: Throwable) => PersistenceServiceException(t.getMessage, Option(t))

  implicit def userNotFoundException =
    (t: Throwable) => UserNotFoundException(t.getMessage, Option(t))
}
