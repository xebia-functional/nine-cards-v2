package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.net.wifi.WifiConfiguration

import scala.util.Random

trait WifiServicesImplData {

  val ssid: String = Random.nextString(10)

  val networksUnsorted = Seq("znf", "Abc", "47 deg", "trn", "bcb", "BB", "ant")

  val wifiConfigurations = networksUnsorted map { network =>
    val wc = new WifiConfiguration
    wc.SSID = network
    wc
  }

  val networksSorted = Seq("47 deg", "Abc", "ant", "BB", "bcb", "trn", "znf")

}
