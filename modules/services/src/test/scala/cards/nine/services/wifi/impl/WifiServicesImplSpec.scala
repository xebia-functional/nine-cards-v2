package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.wifi.{WifiConfiguration, WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
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
        result shouldEqual Right(Some(ssidResult))
      }

    "returns the current SSID with quotes" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithQuotes

        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(Some(ssidWithQuotesResult))
      }

    "returns the current SSID without quotes" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithoutQuotes

        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(Some(ssidWithoutQuotes))
      }

    "returns None if SSID is empty " in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithError

        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if there isn't connectivity manager" in
      new WifiImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns javaNull
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if there isn't active network" in
      new WifiImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns javaNull
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if it is not connected" in
      new WifiImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns false
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if type isn't WIFI" in
      new WifiImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_MOBILE
        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }
  }

  "getConfiguredNetworks" should {

    "returns list of networks sorted" in
      new WifiImplScope {

        mockContext.getSystemService(Context.WIFI_SERVICE) returns mockWifiManager
        mockWifiManager.getConfiguredNetworks returns wifiConfigurations

        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(networksSorted)
      }

    "returns empty list if android don't return data" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns Seq.empty[WifiConfiguration]
        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "returns empty list if android returns null" in
      new WifiImplScope {

        mockWifiManager.getConfiguredNetworks returns javaNull
        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

  }

}
