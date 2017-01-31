/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  override def getBluetoothConnected(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[BluetoothServicesException] {
        contextSupport.getBluetoothDevicesConnected
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
