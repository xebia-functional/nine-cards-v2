package cards.nine.app.receivers.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.{BroadcastReceiver, Context, Intent}

class BluetoothReceiver extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit = {
    (intent.getAction,
     Option(intent.getParcelableExtra[BluetoothDevice](BluetoothDevice.EXTRA_DEVICE))) match {
      case (action, Some(device: BluetoothDevice))
          if action.equals(BluetoothDevice.ACTION_ACL_CONNECTED) =>
        android.util.Log.d(
          "9cards",
          s"CONNECTED: name: ${device.getName} - address: ${device.getAddress} - type: ${device.getType}")
      case (action, Some(device: BluetoothDevice))
          if action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) =>
        android.util.Log.d(
          "9cards",
          s"DISCONNECTED: name: ${device.getName} - address: ${device.getAddress} - type: ${device.getType}")
      case (_, d) =>
        android.util.Log.d("9cards", s"VEMOS action: ${intent.getAction} - device: $d")
    }
  }
}
