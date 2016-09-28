package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ImplicitsShortcutsExceptions, ShortcutServicesException, ShortcutsServices}

import scala.collection.JavaConversions._

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
          Shortcut(
            title = resolveInfo.loadLabel(packageManager).toString,
            icon = activityInfo.icon,
            name = activityInfo.name,
            packageName = activityInfo.applicationInfo.packageName)
        } sortBy (_.title)
      }
    }
}
