package cards.nine.services.connectivity.impl

import java.util

import android.bluetooth.{BluetoothAdapter, BluetoothDevice}
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.models.NineCardsBluetoothDevice
import cards.nine.models.types.BluetoothType
import cards.nine.services.connectivity._

class ConnectivityServicesImpl
    extends ConnectivityServices
    with ImplicitsWifiExceptions
    with ImplicitsBluetoothExceptions {

  override def getCurrentSSID(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[WifiServicesException] {
        val connManager = getConnectivityManager
        val networkInfo = connManager flatMap (manager => Option(manager.getActiveNetworkInfo))

        def nonEmpty(s: String): Boolean = Option(s) match {
          case Some(string) if string.nonEmpty => true
          case _                               => false
        }

        networkInfo match {
          case Some(n)
              if n.isConnected &&
                n.getType == ConnectivityManager.TYPE_WIFI =>
            val regex = "((\"(.*)\")|(.*))".r
            Option(n.getExtraInfo) find (_.nonEmpty) flatMap {
              case regex(_, _, g1, g2) if nonEmpty(g1) => Some(g1)
              case regex(_, _, g1, g2) if nonEmpty(g2) => Some(g2)
              case _                                   => None
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
        val networks = wifiManager flatMap (manager =>
                                              Option(manager.getConfiguredNetworks)) map (_.toList) getOrElse List.empty
        networks map (_.SSID.replace("\"", "")) sortWith (_.toLowerCase() < _.toLowerCase())
      }
    }

  override def getPairedDevices =
    TaskService {
      CatchAll[BluetoothServicesException] {
        import scala.collection.JavaConversions._
        getBondedDevices.map { device =>
          NineCardsBluetoothDevice(
            name = device.getName,
            address = device.getAddress,
            bluetoothType = BluetoothType(device.getBluetoothClass.getMajorDeviceClass))
        }.toSeq
      }
    }

  protected def getBondedDevices: util.Set[BluetoothDevice] =
    BluetoothAdapter.getDefaultAdapter.getBondedDevices

  private[this] def getConnectivityManager(
      implicit contextSupport: ContextSupport): Option[ConnectivityManager] =
    contextSupport.context.getSystemService(Context.CONNECTIVITY_SERVICE) match {
      case conn: ConnectivityManager => Some(conn)
      case _                         => None
    }

  private[this] def getWifiManager(implicit contextSupport: ContextSupport): Option[WifiManager] =
    contextSupport.context.getSystemService(Context.WIFI_SERVICE) match {
      case manager: WifiManager => Some(manager)
      case _                    => None
    }

}
