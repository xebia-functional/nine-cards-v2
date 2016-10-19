package cards.nine.app.receivers.moments

import android.content.Intent
import android.net.ConnectivityManager
import cards.nine.app.ui.commons.action_filters.MomentForceBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class ConnectionStatusChangedJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def connectionStatusChanged(intent: Intent): Option[TaskService[Unit]] = {
    val maybeNetworkInfo = Option(intent) flatMap { i =>
      Option(i.getParcelableExtra[android.net.NetworkInfo]("networkInfo"))
    }
    maybeNetworkInfo match {
      case Some(networkInfo) if networkInfo.getType == ConnectivityManager.TYPE_WIFI =>
        Some(sendBroadCastTask(BroadAction(MomentForceBestAvailableActionFilter.action)))
      case _ => None
    }
  }

}

object ConnectionStatusChangedJobs {

  val action = "android.net.conn.CONNECTIVITY_CHANGE"

}