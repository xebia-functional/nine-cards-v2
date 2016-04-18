package com.fortysevendeg.ninecardslauncher.services.wifi

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

trait WifiServices {

  /**
    * Get the current SSID if it is available
    *
    * @return an Option[String] that contains the name of the SSID
    * @throws WifiServicesException if exist some problem to get the current SSID
    */
  def getCurrentSSID(implicit contextSupport: ContextSupport): ServiceDef2[Option[String], WifiServicesException]

}
