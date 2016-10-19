package cards.nine.app.receivers.moments

import android.content.{BroadcastReceiver, Context, Intent}

class MomentBroadcastReceiver
  extends BroadcastReceiver {

    override def onReceive(context: Context, intent: Intent): Unit = {
      Option(intent) foreach { i =>
        android.util.Log.i("9cards", s"Received intent with action ${i.getAction}, data ${i.getDataString} and component ${i.getComponent.flattenToString()}")
        val extrasNames = i.getExtras.keySet().toArray
        extrasNames foreach { key =>
          android.util.Log.i("9cards", s"Received $key = ${i.getExtras.get(key.toString).toString}")
        }
      }
    }

  }
