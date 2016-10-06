package cards.nine.services.shortcuts.impl

import android.content.{ComponentName, Intent}
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.models.Shortcut
import cards.nine.services.shortcuts.{ImplicitsShortcutsExceptions, ShortcutServicesException, ShortcutsServices}

import scala.collection.JavaConversions._
import scala.util.Try

class ShortcutsServicesImpl
  extends ShortcutsServices
  with ImplicitsShortcutsExceptions {

  override def getShortcuts(implicit context: ContextSupport) =
    TaskService {
      CatchAll[ShortcutServicesException] {
        val packageManager = context.getPackageManager

        val shortcuts = packageManager.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT), 0).toSeq

        shortcuts map { resolveInfo =>
          val activityInfo = resolveInfo.activityInfo
          val componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
          val drawable = Try(context.getPackageManager.getActivityIcon(componentName)).toOption.flatten
          val intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)
          intent.addCategory(Intent.CATEGORY_DEFAULT)
          intent.setComponent(componentName)
          Shortcut(
            title = resolveInfo.loadLabel(packageManager).toString,
            icon = drawable,
            intent = intent)
        } sortBy (_.title)
      }
    }

}
