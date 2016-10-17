package cards.nine.process.thirdparty

import cards.nine.commons.services.TaskService.NineCardException
import cards.nine.process.commons.ConnectionSuspendedCause
import com.google.android.gms.common.ConnectionResult

case class ExternalServicesProcessException(message: String, cause: Option[Throwable] = None, recoverable: Boolean = false)
  extends RuntimeException(message)
  with NineCardException{

  cause foreach initCause

}

trait ImplicitsExternalServicesProcessException {
  implicit def externalServicesProcessException = (t: Throwable) => ExternalServicesProcessException(t.getMessage, Option(t))
}
