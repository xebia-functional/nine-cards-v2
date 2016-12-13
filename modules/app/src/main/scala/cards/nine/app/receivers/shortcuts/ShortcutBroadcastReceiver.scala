package cards.nine.app.receivers.shortcuts

import android.content.{BroadcastReceiver, Context, Intent}
import macroid.ContextWrapper

class ShortcutBroadcastReceiver
  extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {

    val action = intent.getAction

    implicit val contextWrapper = ContextWrapper(context)

    val jobs = new ShortcutBroadcastJobs

    android.util.Log.d("9cards", s"Action $action")
    Option(intent.getExtras).foreach { extras =>
      extras.keySet().toArray.foreach { key =>
        android.util.Log.d("9cards", s"Key $key = ${extras.get(key.toString)}")
      }
    }
  }

}
