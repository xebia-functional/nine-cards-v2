package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.net.wifi.{WifiConfiguration, WifiInfo, WifiManager}
import android.net.{ConnectivityManager, NetworkInfo}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.services.wifi.WifiServicesException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.collection.JavaConversions._

trait WifiImplSpecification
  extends Specification
    with Mockito
    with WifiServicesImplData {

  trait WifiImplScope
    extends Scope {

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

    mockWifiManager.getConfiguredNetworks returns wifiConfigurations

  }

}

class WifiServicesImplSpec
  extends  WifiImplSpecification {

  "getCurrentSSID" should {
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

        mockNetWorkInfo.getExtraInfo returns javaNull

        val result = wifiServicesImpl.getCurrentSSID(mockContextSupport).run.run
        result must beLike {
          case Answer(resultSSID) => resultSSID shouldEqual None
        }
      }
  }

  "getConfiguredNetworks" should {

    "returns list of networks sorted" in
      new WifiImplScope {
        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).run.run
        result must beLike {
          case Answer(networks) => networks shouldEqual networksSorted
        }
      }

    "returns empty list if android don't return data" in
      new WifiImplScope {
        mockWifiManager.getConfiguredNetworks returns Seq.empty[WifiConfiguration]

        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).run.run
        result must beLike {
          case Answer(networks) => networks shouldEqual Seq.empty
        }
      }

    "returns WifiServicesException if android returns null" in
      new WifiImplScope {
        mockWifiManager.getConfiguredNetworks returns javaNull

        val result = wifiServicesImpl.getConfiguredNetworks(mockContextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WifiServicesException]
          }
        }
      }

  }

}
