package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.net.wifi.{WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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

    mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) returns mockNetWorkInfo
    mockNetWorkInfo.isConnected returns true

    mockWifiManager.getConnectionInfo returns mockWifiInfo
    mockWifiInfo.getSSID returns ssid

  }

  trait WifiErrorScope {
    self : WifiImplScope =>

    mockWifiInfo.getSSID returns ""

  }
}

class WifiServicesImplSpec
  extends  WifiImplSpecification {

  "returns the current SSID" in
    new WifiImplScope {
      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
      result must beLike {
        case Xor.Right(resultSSID) => resultSSID shouldEqual Some(ssid)
      }
    }

  "returns an empty SSID if it is not connected" in
    new WifiImplScope with WifiErrorScope {
      val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
      result must beLike {
        case Xor.Right(resultSSID) => resultSSID shouldEqual Some("")
      }
    }
}
