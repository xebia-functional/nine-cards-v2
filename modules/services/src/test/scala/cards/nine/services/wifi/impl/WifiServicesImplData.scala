package cards.nine.services.wifi.impl

import android.net.wifi.WifiConfiguration

import scala.util.Random

trait WifiServicesImplData {

  val ssidResult = "My Wifi"

  val ssid: String = "\"" + ssidResult + "\""

  val ssidWithQuotesResult = "My wifi with \"quotes\""

  val ssidWithQuotes: String = "\"" + ssidWithQuotesResult + "\""

  val ssidWithoutQuotes: String = ssidResult

  val ssidWithError: String = "\"\""

  val networksUnsorted = Seq("znf", "Abc", "47 deg", "trn", "bcb", "BB", "ant")

  val wifiConfigurations = networksUnsorted map { network =>
    val wc = new WifiConfiguration
    wc.SSID = network
    wc
  }

  val networksSorted = Seq("47 deg", "Abc", "ant", "BB", "bcb", "trn", "znf")

}
