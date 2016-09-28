package com.fortysevendeg.ninecardslauncher.services.wifi.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.wifi.{ImplicitsWifiExceptions, WifiServices, WifiServicesException}


class WifiServicesImpl
  extends WifiServices
  with ImplicitsWifiExceptions {

  override def getCurrentSSID(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[WifiServicesException] {
        val connManager = getConnectivityManager
        val networkInfo = connManager flatMap (manager => Option(manager.getActiveNetworkInfo))

        def nonEmpty(s: String): Boolean = Option(s) match {
          case Some(string) if string.nonEmpty => true
          case _ => false
        }

        networkInfo match {
          case Some(n) if n.isConnected &&
            n.getType == ConnectivityManager.TYPE_WIFI =>
            val regex = "((\"(.*)\")|(.*))".r
            Option(n.getExtraInfo) find(_.nonEmpty) flatMap {
              case regex(_, _, g1, g2) if nonEmpty(g1) => Some(g1)
              case regex(_, _, g1, g2) if nonEmpty(g2) => Some(g2)
              case _ => None
            }
          case _ => None
        }
      }
    }

  override def getConfiguredNetworks(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[WifiServicesException] {
        import scala.collection.JavaConversions._
        val wifiManager = getWifiManager
        val networks = wifiManager flatMap (manager => Option(manager.getConfiguredNetworks)) map (_.toList) getOrElse List.empty
        networks map (_.SSID.replace("\"", "")) sortWith(_.toLowerCase() < _.toLowerCase())
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
