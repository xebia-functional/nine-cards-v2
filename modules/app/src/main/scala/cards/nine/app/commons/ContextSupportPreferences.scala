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

package cards.nine.app.commons

import java.io.File

import android.content.{Context, SharedPreferences}
import cards.nine.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.R

trait ContextSupportPreferences { self: ContextSupport =>

  override def getAppIconsDir: File =
    context.getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE)

  override def getSharedPreferences: SharedPreferences =
    context.getSharedPreferences(
      getResources.getString(R.string.shared_preferences_key),
      Context.MODE_PRIVATE)

  override def getActiveUserId: Option[Int] = {
    val key  = getResources.getString(R.string.user_id_key)
    val pref = getSharedPreferences
    if (pref.contains(key)) {
      Option(getSharedPreferences.getInt(key, 0))
    } else {
      None
    }
  }

  override def setActiveUserId(id: Int): Unit = {
    val editor = getSharedPreferences.edit()
    editor.putInt(getResources.getString(R.string.user_id_key), id)
    editor.apply()
  }

  override def addBluetoothDevice(device: String): Unit = {
    import scala.collection.JavaConverters._
    putBluetoothDevice((getBluetoothDevicesConnected + device).asJava)
  }

  override def removeBluetoothDevice(device: String): Unit = {
    import scala.collection.JavaConverters._
    putBluetoothDevice((getBluetoothDevicesConnected - device).asJava)
  }

  override def clearBluetoothDevices(): Unit = {
    import scala.collection.JavaConverters._
    putBluetoothDevice(Set.empty[String].asJava)
  }

  override def getBluetoothDevicesConnected: Set[String] = {
    val key  = getResources.getString(R.string.bluetooth_key)
    val pref = getSharedPreferences
    if (pref.contains(key)) {
      import scala.collection.JavaConversions._
      getSharedPreferences.getStringSet(key, new java.util.TreeSet[String]()).toSet
    } else {
      Set.empty
    }
  }

  private[this] def putBluetoothDevice(devices: java.util.Set[String]): Unit = {
    val editor = getSharedPreferences.edit()
    editor.putStringSet(getResources.getString(R.string.bluetooth_key), devices)
    editor.apply()
  }

}
