package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.net.wifi.{WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.Answer

import scala.util.Random

trait WifiImplSpecification
  extends Specification
    with Mockito {

  trait WifiImplScope
    extends Scope {

    val ssid: String = Random.nextString(10)

    val mockContextSupport = mock[ContextSupport]
    val mockConnectivityManager = mock[ConnectivityManager]
    val mockNetWorkInfo = mock[NetworkInfo]
    val mockWifiManager = mock[WifiManager]
    val mockWifiInfo = mock[WifiInfo]

    val wifiServicesImpl = new WifiServicesImpl {

      override protected def getConnectivityManager(implicit contextSupport: ContextSupport) = Option(mockConnectivityManager)

      override protected def getWifiManager(implicit contextSupport: ContextSupport) = Option(mockWifiManager)

    }

    mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
    mockNetWorkInfo.isConnected returns true
    mockNetWorkInfo.getExtraInfo returns ssid
    mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI

  }

}

class WifiServicesImplSpec
  extends  WifiImplSpecification {

  "returns the current SSID" in
    new WifiImplScope {
      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
      result must beLike {
        case Answer(resultSSID) => resultSSID shouldEqual Some(ssid)
      }
    }

  "returns None if it is not connected" in
    new WifiImplScope {

      mockNetWorkInfo.isConnected returns false

      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
      result must beLike {
        case Answer(resultSSID) => resultSSID shouldEqual None
      }
    }

  "returns None if type isn't WIFI" in
    new WifiImplScope {

      mockNetWorkInfo.getType returns 0

      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
      result must beLike {
        case Answer(resultSSID) => resultSSID shouldEqual None
      }
    }

  "returns None SSID if SSID is empty" in
    new WifiImplScope {

      mockNetWorkInfo.getExtraInfo returns ""

      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
      result must beLike {
        case Answer(resultSSID) => resultSSID shouldEqual None
      }
    }

  "returns None SSID if SSID is null" in
    new WifiImplScope {

      mockNetWorkInfo.getExtraInfo returns null

      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
      result must beLike {
        case Answer(resultSSID) => resultSSID shouldEqual None
      }
    }

}
