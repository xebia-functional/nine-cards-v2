package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.wifi.{WifiServicesException, ImplicitsWifiExceptions, WifiServices}

import scalaz.concurrent.Task

class WifiServicesImpl
  extends WifiServices
  with ImplicitsWifiExceptions {

  override def getCurrentSSID(implicit contextSupport: ContextSupport) = Service {
    Task {
      CatchAll[WifiServicesException] {

        val connManager = getConnManager
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        val ssid =
          if (networkInfo.isConnected) {
            val wifiManager = getWifiManager
            val connectionInfo = Option(wifiManager.getConnectionInfo)
            connectionInfo map (_.getSSID.replace("\"", ""))
          } else None
        ssid
      }
    }
  }

  protected def getConnManager(implicit contextSupport: ContextSupport) = contextSupport.context.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]

  protected def getWifiManager(implicit contextSupport: ContextSupport) = contextSupport.context.getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]

}
