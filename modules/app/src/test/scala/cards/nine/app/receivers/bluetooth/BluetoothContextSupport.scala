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

package cards.nine.app.receivers.bluetooth

import android.app.Application
import android.content.Context
import cards.nine.app.commons.{ContextSupportImpl, ContextSupportPreferences}

import scala.ref.WeakReference

class BluetoothContextSupport extends ContextSupportImpl with ContextSupportPreferences {

  override def addBluetoothDevice(device: String): Unit = {}

  override def removeBluetoothDevice(device: String): Unit = {}

  override def clearBluetoothDevices(): Unit = {}

  override def application: Application = ???

  override def context: Context = ???

  override def getOriginal: WeakReference[Context] = ???
}
