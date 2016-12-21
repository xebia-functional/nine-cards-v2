package cards.nine.services.connectivity.impl

import java.util

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.net.wifi.{WifiConfiguration, WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.TaskServiceTestOps._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scala.collection.JavaConversions._

trait ConnectivityImplSpecification
    extends TaskServiceSpecification
    with Mockito
    with ConnectivityServicesImplData {

  trait ConnectivityImplScope extends Scope {

    val bluetoothDevice: util.Set[BluetoothDevice] = new java.util.TreeSet[BluetoothDevice]()

    val mockContextSupport = mock[ContextSupport]
    mockContextSupport.getBluetoothDevicesConnected returns Set.empty

    val mockContext = mock[Context]
    mockContextSupport.context returns mockContext

    val mockConnectivityManager = mock[ConnectivityManager]
    val mockNetWorkInfo         = mock[NetworkInfo]
    val mockWifiManager         = mock[WifiManager]
    val mockWifiInfo            = mock[WifiInfo]

    val connectivityServicesImpl = new ConnectivityServicesImpl {
      override protected def getBondedDevices: util.Set[BluetoothDevice] = bluetoothDevice
    }
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

        connectivityServicesImpl
          .getCurrentSSID(mockContextSupport)
          .mustRight(_ shouldEqual Some(ssidResult))

      }

    "returns the current SSID with quotes" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithQuotes

        connectivityServicesImpl
          .getCurrentSSID(mockContextSupport)
          .mustRight(_ shouldEqual Some(ssidWithQuotesResult))
      }

    "returns the current SSID without quotes" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithoutQuotes

        connectivityServicesImpl
          .getCurrentSSID(mockContextSupport)
          .mustRight(_ shouldEqual Some(ssidWithoutQuotes))
      }

    "returns None if SSID is empty " in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns wifiConfigurations
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_WIFI
        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getExtraInfo returns ssidWithError

        connectivityServicesImpl.getCurrentSSID(mockContextSupport).mustRightNone
      }

    "returns None if there isn't connectivity manager" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns javaNull
        connectivityServicesImpl.getCurrentSSID(mockContextSupport).mustRightNone
      }

    "returns None if there isn't active network" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns javaNull
        connectivityServicesImpl.getCurrentSSID(mockContextSupport).mustRightNone
      }

    "returns None if it is not connected" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns false
        connectivityServicesImpl.getCurrentSSID(mockContextSupport).mustRightNone
      }

    "returns None if type isn't WIFI" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) returns mockConnectivityManager
        mockConnectivityManager.getActiveNetworkInfo returns mockNetWorkInfo
        mockNetWorkInfo.isConnected returns true
        mockNetWorkInfo.getType returns ConnectivityManager.TYPE_MOBILE
        connectivityServicesImpl.getCurrentSSID(mockContextSupport).mustRightNone
      }
  }

  "getConfiguredNetworks" should {

    "returns list of networks sorted" in
      new ConnectivityImplScope {

        mockContext.getSystemService(Context.WIFI_SERVICE) returns mockWifiManager
        mockWifiManager.getConfiguredNetworks returns wifiConfigurations

        connectivityServicesImpl
          .getConfiguredNetworks(mockContextSupport)
          .mustRight(_ shouldEqual networksSorted)
      }

    "returns empty list if android don't return data" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns Seq.empty[WifiConfiguration]
        connectivityServicesImpl
          .getConfiguredNetworks(mockContextSupport)
          .mustRight(_ shouldEqual Seq.empty)
      }

    "returns empty list if android returns null" in
      new ConnectivityImplScope {

        mockWifiManager.getConfiguredNetworks returns javaNull
        connectivityServicesImpl
          .getConfiguredNetworks(mockContextSupport)
          .mustRight(_ shouldEqual Seq.empty)
      }

  }

  "getPairedDevices" should {

    "returns empty list of devices if doesn't found paired bluetooth" in
      new ConnectivityImplScope {
        connectivityServicesImpl.getPairedDevices.mustRight(_ shouldEqual Seq.empty)
      }

  }

  "getBluetoothConnected" should {

    "returns empty list of devices if there aren't devices connected" in
      new ConnectivityImplScope {
        connectivityServicesImpl
          .getBluetoothConnected(mockContextSupport)
          .mustRight(_ shouldEqual Set.empty)

        there was one(mockContextSupport).getBluetoothDevicesConnected
      }

    "returns list of devices if there are devices connected" in
      new ConnectivityImplScope {
        val devices = Set("My Bluetooth 1", "My Bluetooth 2")

        mockContextSupport.getBluetoothDevicesConnected returns devices

        connectivityServicesImpl
          .getBluetoothConnected(mockContextSupport)
          .mustRight(_ shouldEqual devices)

        there was one(mockContextSupport).getBluetoothDevicesConnected
      }

  }

}
