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

package cards.nine.services.connectivity

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NineCardsBluetoothDevice

trait ConnectivityServices {

  /**
   * Get the current SSID if it is available
   *
   * @return an Option[String] that contains the name of the SSID
   * @throws WifiServicesException if exist some problem to get the current SSID
   */
  def getCurrentSSID(implicit contextSupport: ContextSupport): TaskService[Option[String]]

  /**
   * Get all configured networks sorted by name. The Wifi must be connected
   *
   * @return Seq[String] that contains all SSIDs
   * @throws WifiServicesException if exist some problem getting the information
   */
  def getConfiguredNetworks(implicit contextSupport: ContextSupport): TaskService[Seq[String]]

  /**
   * Get all paired devices by Bluetooth. The Bluetooth must be connected
   *
   * @return Seq[BluetoothDevice] list of devices
   * @throws BluetoothServicesException if exist some problem getting the information
   */
  def getPairedDevices: TaskService[Seq[NineCardsBluetoothDevice]]

  /**
   * Get all bluetooth connected
   *
   * @return Seq[String] list of name of bluetooth
   * @throws BluetoothServicesException if exist some problem getting the information
   */
  def getBluetoothConnected(implicit contextSupport: ContextSupport): TaskService[Set[String]]

}
