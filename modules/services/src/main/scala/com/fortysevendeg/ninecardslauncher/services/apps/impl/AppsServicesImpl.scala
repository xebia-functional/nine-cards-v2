package com.fortysevendeg.ninecardslauncher.services.apps.impl

import java.io.IOException

import android.content.Intent
import android.content.pm.ResolveInfo
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.apps._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

import scala.collection.JavaConversions._
import scalaz.concurrent.Task
import scalaz.Scalaz._

class AppsServicesImpl
  extends AppsServices {

  implicit def uncaughtConverter = (t: Throwable) => AppsInstalledException(t.getMessage, t.some)

  override def getInstalledApps(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[AppsInstalledException] {
        val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val packageManager = context.getPackageManager

        val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(mainIntent, 0).toSeq

        apps map {
          resolveInfo =>
            Application(
              name = resolveInfo.loadLabel(packageManager).toString,
              packageName = resolveInfo.activityInfo.applicationInfo.packageName,
              className = resolveInfo.activityInfo.name,
              icon = resolveInfo.activityInfo.icon)
        }
      }
    }
  }
}
