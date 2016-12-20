package cards.nine.services.connectivity.impl

import android.content.Context
import android.net.wifi.{WifiConfiguration, WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.test.TaskServiceTestOps._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.JavaConversions._

trait ConnectivityImplSpecification
    extends Specification
    with Mockito
    with ConnectivityServicesImplData {

  trait ConnectivityImplScope extends Scope {

    val mockContextSupport = mock[ContextSupport]
    val mockContext        = mock[Context]
    mockContextSupport.context returns mockContext

    val mockConnectivityManager  = mock[ConnectivityManager]
    val mockNetWorkInfo          = mock[NetworkInfo]
    val mockWifiManager          = mock[WifiManager]
    val mockWifiInfo             = mock[WifiInfo]
    val connectivityServicesImpl = new ConnectivityServicesImpl
  }

}

class ConnectivityServicesImplSpec extends ConnectivityImplSpecification {

  "getCurrentSSID" should {
    "returns the current SSID" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssid

        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(Some(ssidResult))
      }

    "returns the current SSID with quotes" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithQuotes

        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(Some(ssidWithQuotesResult))
      }

    "returns the current SSID without quotes" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithoutQuotes

        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(Some(ssidWithoutQuotes))
      }

    "returns None if SSID is empty " in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithError

        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if there isn't connectivity manager" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns javaNull
        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if there isn't active network" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns javaNull
        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if it is not connected" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns false
        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }

    "returns None if type isn't WIFI" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_MOBILE
        val result = connectivityServicesImpl.getCurrentSSID(mockContextSupport).value.run
        result shouldEqual Right(None)
      }
  }

  "getConfiguredNetworks" should {

    "returns list of networks sorted" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.WIFI_SERVICE) returns mockWifiManager
        mockWifiManager.getConfiguredNetworks returns wifiConfigurations

        val result = connectivityServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(networksSorted)
      }

    "returns empty list if android don't return data" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns Seq.empty[WifiConfiguration]
        val result = connectivityServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "returns empty list if android returns null" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns javaNull
        val result = connectivityServicesImpl.getConfiguredNetworks(mockContextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

  }

}
