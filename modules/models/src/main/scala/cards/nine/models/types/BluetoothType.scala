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
