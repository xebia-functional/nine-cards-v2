package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ImplicitsShortcutsExceptions, ShortcutServicesException, ShortcutsServices}

import scala.collection.JavaConversions._
import scalaz.concurrent.Task

class ShortcutsServicesImpl
  extends ShortcutsServices
  with ImplicitsShortcutsExceptions {

  override def getShortcuts(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[ShortcutServicesException] {
        val packageManager = context.getPackageManager

        val shortcuts = packageManager.queryIntentActivities(shortcutsIntent(), 0).toSeq

        shortcuts map { resolveInfo =>
          val activityInfo = resolveInfo.activityInfo
          Shortcut(
            title = resolveInfo.loadLabel(packageManager).toString,
            icon = activityInfo.icon,
            name = activityInfo.name,
            packageName = activityInfo.applicationInfo.packageName)
        } sortBy(_.title)

      }
    }
  }

  protected def shortcutsIntent(): Intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)

}
