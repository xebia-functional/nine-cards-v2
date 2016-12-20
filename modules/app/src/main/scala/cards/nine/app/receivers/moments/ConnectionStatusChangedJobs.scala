package cards.nine.app.receivers.moments

import cards.nine.app.ui.commons.action_filters.MomentBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.InVehicleFence
import macroid.ContextWrapper

class ConnectionStatusChangedJobs(implicit contextWrapper: ContextWrapper) extends Jobs {

  def connectionStatusChanged(): TaskService[Unit] =
    sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))

  def headphoneStatusChanged(key: String): TaskService[Unit] =
    sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action, Option(key)))

  def inVehicleStatusChanged(): TaskService[Unit] =
    sendBroadCastTask(
      BroadAction(MomentBestAvailableActionFilter.action, Option(InVehicleFence.key)))

}

object ConnectionStatusChangedJobs {

  val action = "android.net.conn.CONNECTIVITY_CHANGE"

}
