package cards.nine.app.receivers.bluetooth

import cards.nine.app.ui.commons.action_filters.MomentBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class BluetoothJobs(implicit contextWrapper: ContextWrapper) extends Jobs {

  def addBluetoothDevice(device: String): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.addBluetoothDevice(device))
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

  def removeBluetoothDevice(device: String): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.removeBluetoothDevice(device))
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

  def removeAllBluetoothDevices(): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.clearBluetoothDevices())
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

}
