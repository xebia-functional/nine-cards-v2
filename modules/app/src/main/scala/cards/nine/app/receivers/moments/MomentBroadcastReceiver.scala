package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}
import android.net.ConnectivityManager
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper
import monix.execution.cancelables.SerialCancelable

import scala.concurrent.duration._

class MomentBroadcastReceiver
  extends BroadcastReceiver {

  import MomentBroadcastReceiver._

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val connectionStatusChangedJobs = new ConnectionStatusChangedJobs

    Option(intent) foreach { i =>
      (Option(i.getAction), Option(i.getParcelableExtra[android.net.NetworkInfo]("networkInfo"))) match {
        case (Some(ConnectionStatusChangedJobs.action), Some(networkInfo)) if networkInfo.getType == ConnectivityManager.TYPE_WIFI =>
          connectionStatusTaskRef := connectionStatusChangedJobs.connectionStatusChanged().resolveAsyncDelayed(10.seconds)
        case _ =>
      }
    }

  }

}

object MomentBroadcastReceiver {

  val connectionStatusTaskRef = SerialCancelable()

}