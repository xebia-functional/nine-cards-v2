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

package cards.nine.app.ui.commons.dialogs.editmoment

import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait EditMomentDOM { self: TypedFindView =>

  lazy val iconLinkCollection = findView(TR.edit_moment_icon_link_collection)

  lazy val nameLinkCollection = findView(TR.edit_moment_name_link_collection)

  lazy val momentCollection = findView(TR.edit_moment_collection)

  lazy val iconInfo = findView(TR.edit_moment_collection_info)

  lazy val wifiRoot = findView(TR.edit_moment_wifi_root)

  lazy val wifiContent = findView(TR.edit_moment_wifi_content)

  lazy val iconWifi = findView(TR.edit_moment_icon_wifi)

  lazy val nameWifi = findView(TR.edit_moment_name_wifi)

  lazy val addWifiAction = findView(TR.edit_moment_add_wifi)

  lazy val bluetoothRoot = findView(TR.edit_moment_bluetooth_root)

  lazy val bluetoothContent = findView(TR.edit_moment_bluetooth_content)

  lazy val iconBluetooth = findView(TR.edit_moment_icon_bluetooth)

  lazy val nameBluetooth = findView(TR.edit_moment_name_bluetooth)

  lazy val addBluetoothAction = findView(TR.edit_moment_add_bluetooth)

  lazy val hourRoot = findView(TR.edit_moment_hours_root)

  lazy val hourContent = findView(TR.edit_moment_hour_content)

  lazy val addHourAction = findView(TR.edit_moment_add_hour)

  lazy val iconHour = findView(TR.edit_moment_icon_hour)

  lazy val nameHour = findView(TR.edit_moment_name_hour)

  lazy val messageRoot = findView(TR.edit_moment_message_root)

  lazy val messageIcon = findView(TR.edit_moment_icon_message)

  lazy val messageName = findView(TR.edit_moment_name_message)

  lazy val messageText = findView(TR.edit_moment_message)

}

trait EditMomentListener {

  def addWifi(): Unit

  def addWifi(wifi: String): Unit

  def addBluetooth(): Unit

  def addBluetooth(device: String): Unit

  def addHour(): Unit

  def saveMoment(): Unit

  def setCollectionId(collectionId: Option[Int]): Unit

  def removeHour(position: Int): Unit

  def removeWifi(position: Int): Unit

  def removeBluetooth(position: Int): Unit

  def changeFromHour(position: Int, hour: String): Unit

  def changeToHour(position: Int, hour: String): Unit

  def swapDay(position: Int, index: Int): Unit

}
