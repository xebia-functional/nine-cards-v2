package cards.nine.process.userv1

import cards.nine.commons.services.TaskService.NineCardException

case class UserV1Exception(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class UserV1ConfigurationException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsUserV1Exception {
  implicit def userConfigException = (t: Throwable) => UserV1Exception(t.getMessage, Option(t))
}
