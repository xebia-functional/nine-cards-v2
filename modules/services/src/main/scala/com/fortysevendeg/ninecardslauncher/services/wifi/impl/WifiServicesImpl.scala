package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.wifi.{ImplicitsWifiExceptions, WifiServices, WifiServicesException}

import scalaz.concurrent.Task

class WifiServicesImpl
  extends WifiServices
  with ImplicitsWifiExceptions {

  override def getCurrentSSID(implicit contextSupport: ContextSupport) = TaskService {
    Task {
      XorCatchAll[WifiServicesException] {
        val connManager = getConnectivityManager
        val networkInfo = connManager flatMap (manager => Option(manager.getActiveNetworkInfo))

        networkInfo match {
          case Some(n) if n.isConnected &&
            n.getType == ConnectivityManager.TYPE_WIFI &&
            n.getExtraInfo != "" &&
            Option(n.getExtraInfo).nonEmpty =>
            // Android sends SSIDs with quotes... I don't know why. We only remove them
            // if the name starts or ends with quotes
            val name = n.getExtraInfo
            val startWithQuotes = name.startsWith("\"")
            val endWithQuotes = name.endsWith("\"")
            val start = if (startWithQuotes) 1 else 0
            val end = if (endWithQuotes) name.length - 1 else name.length
            Option(name.substring(start, end))
          case _ => None
        }
      }
    }
  }

  override def getConfiguredNetworks(implicit contextSupport: ContextSupport) = TaskService {
    Task {
      XorCatchAll[WifiServicesException] {
        import scala.collection.JavaConversions._
        val wifiManager = getWifiManager
        val networks = wifiManager flatMap (manager => Option(manager.getConfiguredNetworks)) map (_.toList) getOrElse List.empty
        networks map (_.SSID.replace("\"", "")) sortWith(_.toLowerCase() < _.toLowerCase())
      }
    }
  }

  private[this] def getConnectivityManager(implicit contextSupport: ContextSupport): Option[ConnectivityManager] =
    contextSupport.context.getSystemService(Context.CONNECTIVITY_SERVICE) match {
      case conn : ConnectivityManager => Some(conn)
      case _ => None
    }

  private[this] def getWifiManager(implicit contextSupport: ContextSupport): Option[WifiManager] =
    contextSupport.context.getSystemService(Context.WIFI_SERVICE) match {
      case manager : WifiManager => Some(manager)
      case _ => None
    }

}
