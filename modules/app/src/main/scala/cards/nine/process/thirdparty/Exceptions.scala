package cards.nine.process.thirdparty

import cards.nine.commons.services.TaskService.NineCardException

case class ExternalServicesProcessException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsExternalServicesProcessException {
  implicit def externalServicesProcessException = (t: Throwable) => ExternalServicesProcessException(t.getMessage, Option(t))
}

case class TokenFirebaseException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
    with NineCardException{

  cause foreach initCause

}

trait ImplicitsTokenFirebaseException {
  implicit def tokenFirebaseException = (t: Throwable) => TokenFirebaseException(t.getMessage, Option(t))
}