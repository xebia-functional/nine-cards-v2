package cards.nine.app.receivers.shortcuts

import android.content.{BroadcastReceiver, Context, Intent}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper

class ShortcutBroadcastReceiver
  extends BroadcastReceiver {

  import ShortcutBroadcastReceiver._

  override def onReceive(context: Context, intent: Intent): Unit = {

    implicit val contextWrapper = ContextWrapper(context)

    val jobs = new ShortcutBroadcastJobs

    Option(intent) foreach { i =>
      if (i.getAction == actionInstallShortcut) jobs.addShortcut(i).resolveAsync()
    }
  }

}

object ShortcutBroadcastReceiver {

  val actionInstallShortcut = "com.android.launcher.action.INSTALL_SHORTCUT"

  val shortcutBroadcastPreferences = "shortcutBroadcastPreferences"

  val collectionIdKey = "_collectionsId_"

}