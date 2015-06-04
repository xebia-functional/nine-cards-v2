package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.services.apps._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import macroid.ContextWrapper
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AppsServicesImpl
  extends AppsServices {

  private[this] def getPackageManager(implicit context: ContextWrapper) = context.application.getPackageManager

  override def getInstalledApps(request: GetInstalledAppsRequest)(implicit context: ContextWrapper): Future[GetInstalledAppsResponse] =
      Future {
        val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val packageManager = getPackageManager

        val l = packageManager.queryIntentActivities(mainIntent, 0).toSeq
        val apps = Seq(l: _*)

        val appItems: Seq[Application] = apps map {
          resolveInfo =>
            Application(
              name = resolveInfo.loadLabel(packageManager).toString,
              packageName = resolveInfo.activityInfo.applicationInfo.packageName,
              className = resolveInfo.activityInfo.name)
        }

        GetInstalledAppsResponse(appItems)
      }


}
