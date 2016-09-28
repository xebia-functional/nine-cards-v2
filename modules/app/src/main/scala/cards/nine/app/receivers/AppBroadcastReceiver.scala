package cards.nine.app.receivers

import android.content.Intent._
import android.content.{BroadcastReceiver, Context, Intent}
import cards.nine.app.commons.{ContextSupportImpl, ContextSupportPreferences}
import cards.nine.app.receivers.jobs.AppBroadcastJobs
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.ContextWrapper

class AppBroadcastReceiver
  extends BroadcastReceiver {

  override def onReceive(context: Context, intent: Intent): Unit = {
    val packageName = getPackageName(intent)
    // We don't want to change 9Cards App
    if (!context.getPackageName.equals(packageName)) {
      val action = intent.getAction
      val replacing = intent.getBooleanExtra(EXTRA_REPLACING, false)

      implicit val contextWrapper = ContextWrapper(context)

      val jobs = new AppBroadcastJobs

      (action, replacing) match {
        case (ACTION_PACKAGE_ADDED, false) => jobs.addApp(packageName).resolveAsync2()
        case (ACTION_PACKAGE_REMOVED, false) => jobs.deleteApp(packageName).resolveAsync2()
        case (ACTION_PACKAGE_CHANGED | ACTION_PACKAGE_REPLACED, _) => jobs.updateApp(packageName).resolveAsync2()
        case (_, _) =>
      }
    }
  }

  private[this] def getPackageName(intent: Intent): String = {
    intent.getData.toString.replace("package:", "")
  }
}
