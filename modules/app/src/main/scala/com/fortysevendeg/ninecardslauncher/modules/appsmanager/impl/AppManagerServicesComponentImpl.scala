package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent

import scala.annotation.tailrec

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

    @tailrec
    private def convertToSeq[T](list: java.util.List[T], acc: Seq[T]): Seq[T] = {
      if (list.isEmpty) {
        acc
      } else {
        val l = list.remove(0)
        convertToSeq(list, l +: acc)
      }
    }

    override def getApps: Service[GetAppsRequest, GetAppsResponse] =
      request =>
        Future {
          val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
          mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

          val l = packageManager.queryIntentActivities(mainIntent, 0).toSeq
          val apps = Seq(l: _*)

          val appitems: Seq[AppItem] = apps map {
            resolveInfo =>
              val name = resolveInfo.loadLabel(packageManager).toString
              AppItem(
                name = name,
                packageName = resolveInfo.activityInfo.applicationInfo.packageName,
                imagePath = "",
                intent = "")
          }

          GetAppsResponse(appitems)
        }

  }

}
