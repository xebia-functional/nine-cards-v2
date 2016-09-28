package cards.nine.services.wifi

import cards.nine.commons.services.TaskService.NineCardException

case class WifiServicesException(message: String, cause : Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsWifiExceptions {
  implicit def wifiServicesException = (t: Throwable) => WifiServicesException(t.getMessage, Option(t))
}