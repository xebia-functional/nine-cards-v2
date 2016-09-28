package cards.nine.services.contacts

import cards.nine.commons.services.TaskService.NineCardException

case class ContactsServiceException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ContactNotFoundException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class ContactsServicePermissionException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}
