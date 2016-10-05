package cards.nine.app.ui.commons

import cards.nine.commons.services.TaskService.NineCardException

case class UiException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsUiExceptions {
  implicit def uiExceptionConverter = (t: Throwable) => UiException(t.getMessage, Option(t))
}

case class ObserverException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsObserverExceptions {
  implicit def observerExceptionConverter = (t: Throwable) => ObserverException(t.getMessage, Option(t))
}

case class JobException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsJobExceptions {
  implicit def jobExceptionConverter = (t: Throwable) => JobException(t.getMessage, Option(t))
}