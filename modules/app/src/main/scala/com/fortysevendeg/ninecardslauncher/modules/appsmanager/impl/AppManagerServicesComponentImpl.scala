package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import android.content.pm.ResolveInfo
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AppManagerServicesComponentImpl
  extends AppManagerServicesComponent {

  self: AppContextProvider with ImageServicesComponent =>

  lazy val appManagerServices = new AppManagerServicesImpl

  class AppManagerServicesImpl
    extends AppManagerServices {

    val packageManager = appContextProvider.get.getPackageManager

    override def getApps: Service[GetAppsRequest, GetAppsResponse] =
      request =>
        Future {
          val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
          mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
          val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(mainIntent, 0)

          val appitems = apps map {
            resolveInfo =>
              val name = resolveInfo.loadLabel(packageManager).toString
              AppItem(
                name = name,
                packageName = resolveInfo.activityInfo.applicationInfo.packageName,
                imagePath = imageServices.createAppBitmap(name, resolveInfo),
                intent = "")
          }

          GetAppsResponse(appitems)
        }

  }

}
