package com.fortysevendeg.ninecardslauncher.services.shortcuts.impl

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.ShortCut
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ImplicitsShortCutsExceptions, ShortCutException, ShortCutsServices}

import scala.collection.JavaConversions._
import scalaz.concurrent.Task

class ShortCutsServicesImpl
  extends ShortCutsServices
  with ImplicitsShortCutsExceptions {

  override def getShortCuts(implicit context: ContextSupport): ServiceDef2[Seq[ShortCut], ShortCutException] = Service {
    Task {
      CatchAll[ShortCutException] {
        val packageManager = context.getPackageManager

        val shortcuts = packageManager.queryIntentActivities(shortCutsIntent(), 0).toSeq

        shortcuts map { resolveInfo =>
          val activityInfo = resolveInfo.activityInfo
          ShortCut(
            title = resolveInfo.loadLabel(packageManager).toString,
            icon = activityInfo.icon,
            name = activityInfo.name,
            packageName = activityInfo.applicationInfo.packageName
          )
        } sortBy(_.title)

      }
    }
  }

  protected def shortCutsIntent(): Intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)

}
