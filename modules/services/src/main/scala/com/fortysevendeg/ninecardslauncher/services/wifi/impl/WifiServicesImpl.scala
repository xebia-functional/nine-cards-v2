package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.wifi.{WifiServicesException, ImplicitsWifiExceptions, WifiServices}

import scalaz.concurrent.Task

class WifiServicesImpl(implicit contextSupport: ContextSupport)
  extends WifiServices
  with ImplicitsWifiExceptions {

  override def getCurrentSSID = Service {
    Task {
      CatchAll[WifiServicesException] {

        val connManager = contextSupport.context.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        val ssid =
          if (networkInfo.isConnected) {
            val wifiManager = contextSupport.context.getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]
            val connectionInfo = Option(wifiManager.getConnectionInfo)
            connectionInfo map (_.getSSID.replace("\"", ""))
          } else None
        ssid
      }
    }
  }
}
