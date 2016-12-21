package cards.nine.app.receivers.bluetooth

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import macroid.ContextWrapper

class BluetoothJobs(implicit contextWrapper: ContextWrapper) extends Jobs {

  def addBluetoothDevice(device: String): TaskService[Unit] = TaskService.right {
    contextSupport.addBluetoothDevice(device)
  }

  def removeBluetoothDevice(device: String): TaskService[Unit] = TaskService.right {
    contextSupport.removeBluetoothDevice(device)
  }

}
