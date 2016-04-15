package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.wifi.{WifiServicesException, ImplicitsWifiExceptions, WifiServices}

import scala.util.Try
import scalaz.concurrent.Task

class WifiServicesImpl
  extends WifiServices
  with ImplicitsWifiExceptions {

  override def getCurrentSSID(implicit contextSupport: ContextSupport) = Service {
    Task {
      CatchAll[WifiServicesException] {

        val connManager = getConnectivityManager
        val networkInfo = connManager map (_.getNetworkInfo(ConnectivityManager.TYPE_WIFI))

        val ssid = networkInfo map { n =>
          if (n.isConnected) {
            val wifiManager = getWifiManager
            val connectionInfo = wifiManager map (_.getConnectionInfo)
            connectionInfo map (_.getSSID.replace("\"", ""))
          } else None
        }
        ssid.flatten
      }
    }
  }

  protected def getConnectivityManager(implicit contextSupport: ContextSupport): Option[ConnectivityManager] =
    Try {
      contextSupport.context.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
    }.toOption

  protected def getWifiManager(implicit contextSupport: ContextSupport): Option[WifiManager]  =
    Try {
      contextSupport.context.getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]
    }.toOption

}
