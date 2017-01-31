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

package cards.nine.models.types

import android.bluetooth.BluetoothClass

sealed trait BluetoothType

case object AudioAndVideoBluetooth extends BluetoothType

case object ComputerBluetooth extends BluetoothType

case object HealthBluetooth extends BluetoothType

case object NetworkingBluetooth extends BluetoothType

case object PeripheralBluetooth extends BluetoothType

case object PhoneBluetooth extends BluetoothType

case object ToyBluetooth extends BluetoothType

case object WearableBluetooth extends BluetoothType

case object UnknownBluetooth extends BluetoothType

object BluetoothType {

  def apply(t: Int): BluetoothType = t match {
    case BluetoothClass.Device.Major.AUDIO_VIDEO => AudioAndVideoBluetooth
    case BluetoothClass.Device.Major.COMPUTER    => ComputerBluetooth
    case BluetoothClass.Device.Major.HEALTH      => HealthBluetooth
    case BluetoothClass.Device.Major.NETWORKING  => NetworkingBluetooth
    case BluetoothClass.Device.Major.PERIPHERAL  => PeripheralBluetooth
    case BluetoothClass.Device.Major.PHONE       => PhoneBluetooth
    case BluetoothClass.Device.Major.TOY         => ToyBluetooth
    case BluetoothClass.Device.Major.WEARABLE    => WearableBluetooth
    case _                                       => UnknownBluetooth
  }

}
