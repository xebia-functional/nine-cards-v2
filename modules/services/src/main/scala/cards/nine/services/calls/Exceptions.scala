package cards.nine.services.calls

import cards.nine.commons.services.TaskService.NineCardException

case class CallsServicesException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

case class CallsServicesPermissionException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsCallsExceptions {
  implicit def callsServicesException = (t: Throwable) => CallsServicesException(t.getMessage, Option(t))
}