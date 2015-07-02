package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.content.Intent
import android.content.pm.ResolveInfo
import com.fortysevendeg.ninecardslauncher.services.apps._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AppsServicesImpl
  extends AppsServices {

  override def getInstalledApps(request: GetInstalledAppsRequest)(implicit context: ContextSupport): Future[GetInstalledAppsResponse] =
      Future {
        val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val packageManager = context.getPackageManager

        val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(mainIntent, 0).toSeq

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
