package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.AppItem
import com.fortysevendeg.ninecardslauncher.modules.api.{ApiServicesComponent, GooglePlaySimplePackagesRequest}
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServicesComponent
import com.fortysevendeg.ninecardslauncher.modules.repository.{InsertCacheCategoryRequest, GetCacheCategoryRequest, GetCacheCategoryResponse, RepositoryServicesComponent}
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.Success
import macroid.Logging._

trait AppManagerServicesComponentImpl
    extends AppManagerServicesComponent {

  self: AppContextProvider
      with ImageServicesComponent
      with RepositoryServicesComponent
      with UserServicesComponent
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
            val apps = response.apps.filter(_.category == Some(request.category))
            GetAppsByCategoryResponse(apps)
        }

    override def categorizeApps: Service[CategorizeAppsRequest, CategorizeAppsResponse] =
      request => {
        val promise = Promise[CategorizeAppsResponse]()
        getCategorizedApps(GetCategorizedAppsRequest()) map {
          response =>
            val packagesWithoutCategory = response.apps.filter(_.category.isEmpty) map (_.packageName)
            if (packagesWithoutCategory.isEmpty) {
              promise.complete(Success(CategorizeAppsResponse(true)))
            } else {
              (for {
                user <- userServices.getUser
                token <- user.sessionToken
                androidId <- userServices.getAndroidId
              } yield {
                  apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(androidId, token, packagesWithoutCategory)) map {
                    response =>
                      val futures = response.apps.items map {
                        app =>
                          repositoryServices.insertCacheCategory(InsertCacheCategoryRequest(
                            packageName = app.packageName,
                            category = app.appCategory,
                            starRating = app.starRating,
                            numDownloads = app.numDownloads,
                            ratingsCount = app.ratingCount,
                            commentCount = app.commentCount
                          ))
                      }
                      Future.sequence(futures) map {
                        seq =>
                          promise.complete(Success(CategorizeAppsResponse(true)))
                      } recover {
                        case _ => promise.complete(Success(CategorizeAppsResponse(false)))
                      }
                  } recover {
                    case _ => promise.complete(Success(CategorizeAppsResponse(false)))
                  }
                }).getOrElse(promise.complete(Success(CategorizeAppsResponse(false))))
            }
        }
        promise.future
      }

  }

}
