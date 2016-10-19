package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper

class MomentBroadcastReceiver
  extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val connectionStatusChangedJobs = new ConnectionStatusChangedJobs

    Option(intent) flatMap (i => Option(i.getAction)) match {
      case Some(ConnectionStatusChangedJobs.action) =>
        connectionStatusChangedJobs.connectionStatusChanged(intent).resolveAsync()
      case _ =>
    }


    Option(intent) foreach { i =>
      val networkInfo = i.getExtras.get("networkInfo")
      android.util.Log.i("9cards", s"NetworkInfo ${networkInfo.getClass.getName}")
//      android.util.Log.i("9cards", s"Received intent with action ${i.getAction}, data ${i.getDataString} and component ${i.getComponent.flattenToString()}")
//      val extrasNames = i.getExtras.keySet().toArray
//      extrasNames foreach { key =>
//        android.util.Log.i("9cards", s"Received $key = ${i.getExtras.get(key.toString).toString}")
//      }
    }
  }

}
