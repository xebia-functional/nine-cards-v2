package cards.nine.app.receivers.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.{BroadcastReceiver, Context, Intent}
import macroid.ContextWrapper

class BluetoothReceiver extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val jobs = new BluetoothJobs

    (intent.getAction,
     Option(intent.getParcelableExtra[BluetoothDevice](BluetoothDevice.EXTRA_DEVICE))) match {
      case (action, Some(device: BluetoothDevice))
          if action.equals(BluetoothDevice.ACTION_ACL_CONNECTED) =>
        jobs.addBluetoothDevice(device.getName)
      case (action, Some(device: BluetoothDevice))
          if action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) =>
        jobs.removeBluetoothDevice(device.getName)
      case _ =>
    }
  }

}
