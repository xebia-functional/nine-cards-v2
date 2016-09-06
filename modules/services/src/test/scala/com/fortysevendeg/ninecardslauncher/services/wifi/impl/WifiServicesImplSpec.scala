package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.wifi.{WifiConfiguration, WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.JavaConversions._

trait WifiImplSpecification
  extends Specification
    with Mockito
    with WifiServicesImplData {

  trait WifiImplScope
    extends Scope {

    val mockContextSupport = mock[ContextSupport]
    val mockContext = mock[Context]
    mockContextSupport.context returns mockContext

    val mockConnectivityManager = mock[ConnectivityManager]
    val mockNetWorkInfo = mock[NetworkInfo]
    val mockWifiManager = mock[WifiManager]
    val mockWifiInfo = mock[WifiInfo]
    val wifiServicesImpl = new WifiServicesImpl
  }

}

class WifiServicesImplSpec
  extends WifiImplSpecification {

  "getCurrentSSID" should {
    "returns the current SSID" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssid

        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(Some(ssid))
      }

    "returns None if there isn't active network" in
      new WifiImplScope {

        mockConnectivityManager.getActiveNetworkInfo returns javaNull
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(None)
      }

    "returns None if it is not connected" in
      new WifiImplScope {

        mockNetWorkInfo.isConnected returns false
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(None)
      }

    "returns None if type isn't WIFI" in
      new WifiImplScope {

        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_MOBILE
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(None)
      }

    "returns None if SSID is empty" in
      new WifiImplScope {

        mockNetWorkInfo.getExtraInfo returns ""
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(None)
      }

    "returns None if SSID is null" in
      new WifiImplScope {

        mockNetWorkInfo.getExtraInfo returns javaNull
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Xor.Right(None)
      }
  }

  "getConfiguredNetworks" should {

    "returns list of networks sorted" in
      new WifiImplScope {

        mockContext.getSystemService(Context.WIFI_SERVICE) returns mockWifiManager
        mockWifiManager.getConfiguredNetworks returns wifiConfigurations

        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Xor.Right(networksSorted)
      }

    "returns empty list if android don't return data" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns Seq.empty[WifiConfiguration]
        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Xor.Right(Seq.empty)
      }

    "returns empty list if android returns null" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns javaNull
        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Xor.Right(Seq.empty)
      }

  }

}
