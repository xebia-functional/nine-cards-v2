package cards.nine.process.user

import cards.nine.commons.services.TaskService.NineCardException

case class UserException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsUserException {
  implicit def userException = (t: Throwable) => UserException(t.getMessage, Option(t))
}
