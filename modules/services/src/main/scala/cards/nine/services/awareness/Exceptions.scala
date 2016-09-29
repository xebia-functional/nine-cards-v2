package cards.nine.services.awareness

import cards.nine.commons.services.TaskService.NineCardException

case class AwarenessException(message: String,cause: Option[Throwable] = None)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsAwarenessExceptions {
  implicit def awarenessExceptionConverter = (t: Throwable) => AwarenessException(t.getMessage, Option(t))
}