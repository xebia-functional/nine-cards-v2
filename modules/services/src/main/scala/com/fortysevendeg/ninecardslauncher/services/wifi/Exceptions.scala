package com.fortysevendeg.ninecardslauncher.services.wifi

import scalaz.Scalaz._

case class WifiServicesException(message: String, cause : Option[Throwable] = None) extends RuntimeException(message) {
  cause map initCause
}

trait ImplicitsWifiExceptions {
  implicit def wifiServicesException = (t: Throwable) => WifiServicesException(t.getMessage, t.some)
}