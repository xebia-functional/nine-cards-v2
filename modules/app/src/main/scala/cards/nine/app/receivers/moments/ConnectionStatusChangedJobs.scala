package cards.nine.app.receivers.moments

import cards.nine.app.ui.commons.action_filters.MomentForceBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class ConnectionStatusChangedJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def connectionStatusChanged(): TaskService[Unit] =
    sendBroadCastTask(BroadAction(MomentForceBestAvailableActionFilter.action))

}

object ConnectionStatusChangedJobs {

  val action = "android.net.conn.CONNECTIVITY_CHANGE"

}