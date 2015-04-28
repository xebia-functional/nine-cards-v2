package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.api.{ApiServicesComponent, ApiServices}
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.{GetCacheCategoryResponse, GetCacheCategoryRequest, RepositoryServicesComponent}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Promise, Future}

trait AppManagerServicesComponentImpl
  extends AppManagerServicesComponent {

  self: AppContextProvider
      with ImageServicesComponent
      with RepositoryServicesComponent
      with ApiServicesComponent =>

  lazy val appManagerServices = new AppManagerServicesImpl

  class AppManagerServicesImpl
    extends AppManagerServices {

    val packageManager = appContextProvider.get.getPackageManager

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
                imagePath = imageServices.createAppBitmap(name, resolveInfo),
                intent = "")
          }

          GetAppsResponse(appitems)
        }

    override def getCategorizedApps: Service[GetCategorizedAppsRequest, GetCategorizedAppsResponse] =
      request =>
        for {
          GetCacheCategoryResponse(cacheCategory) <- repositoryServices.getCacheCategory(GetCacheCategoryRequest())
          GetAppsResponse(apps) <- getApps(GetAppsRequest())
        } yield {
          val categorizedApps = apps map {
            app =>
              app.copy(category = cacheCategory.find(_.packageName == app.packageName).map(_.category))
          }
          GetCategorizedAppsResponse(categorizedApps)
        }

    override def getAppsByCategory: Service[GetAppsByCategoryRequest, GetAppsByCategoryResponse] =
      request =>
        getCategorizedApps(GetCategorizedAppsRequest()) map {
          response =>
            GetAppsByCategoryResponse(response.apps.filter(_.category == request.category))
        }

    override def categorizeApps: Service[CategorizeAppsRequest, CategorizeAppsResponse] =
      request => {
        val promise = Promise[CategorizeAppsResponse]()
        getCategorizedApps(GetCategorizedAppsRequest()) map {
          response =>
            response.apps.filter(_.category.isEmpty) map {
              app =>
                app
            }
        }
        promise.future
      }

  }

}
