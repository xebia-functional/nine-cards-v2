package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}
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

    Option(intent) flatMap (i => Option(i.getAction)) match {
      case Some(ConnectionStatusChangedJobs.action) =>
        connectionStatusChangedJobs.connectionStatusChanged(intent) foreach { service =>
          connectionStatusTaskRef := service.resolveAsyncDelayed(30.seconds)
        }
      case _ =>
    }

  }

}

object MomentBroadcastReceiver {

  val connectionStatusTaskRef = SerialCancelable()

}