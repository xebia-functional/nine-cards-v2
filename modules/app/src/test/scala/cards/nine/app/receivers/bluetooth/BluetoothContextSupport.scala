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
