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

import android.bluetooth.{BluetoothAdapter, BluetoothDevice}
import android.content.{BroadcastReceiver, Context, Intent}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper

class BluetoothReceiver extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val jobs = new BluetoothJobs

    (intent.getAction,
     Option(intent.getParcelableExtra[BluetoothDevice](BluetoothDevice.EXTRA_DEVICE)),
     Option(BluetoothAdapter.getDefaultAdapter)) match {
      case (action, Some(device: BluetoothDevice), _)
          if action.equals(BluetoothDevice.ACTION_ACL_CONNECTED) =>
        jobs.addBluetoothDevice(device.getName).resolveAsync()
      case (action, Some(device: BluetoothDevice), _)
          if action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) =>
        jobs.removeBluetoothDevice(device.getName).resolveAsync()
      case (action, _, Some(adapter))
          if action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && adapter.getState == BluetoothAdapter.STATE_OFF =>
        jobs.removeAllBluetoothDevices().resolveAsync()
      case _ =>
    }
  }

}
