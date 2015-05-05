package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models.{NineCardIntent, AppItem}
import com.fortysevendeg.ninecardslauncher.modules.api.{GooglePlayPackagesRequest, ApiServicesComponent, GooglePlaySimplePackagesRequest}
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.{StoreImageAppRequest, ImageServicesComponent}
import com.fortysevendeg.ninecardslauncher.modules.repository.{InsertCacheCategoryRequest, GetCacheCategoryRequest, GetCacheCategoryResponse, RepositoryServicesComponent}
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import play.api.libs.json._

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
              val extras = Map(
                NineCardExtraPackageName -> resolveInfo.activityInfo.applicationInfo.packageName,
                NineCardExtraClassName -> resolveInfo.activityInfo.name
              )
              val intent = new NineCardIntent(extras)
              intent.setAction(OpenApp)
              val writes = Json.writes[NineCardIntent]
              AppItem(
                name = name,
                packageName = resolveInfo.activityInfo.applicationInfo.packageName,
                imagePath = imageServices.createAppBitmap(name, resolveInfo),
                intent = Json.toJson(intent)(writes).toString())
          }

          GetAppsResponse(appitems)
        }

    override def createBirmapForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse] =
      request => {
        val promise = Promise[PackagesResponse]()
        val packagesNoFound = (request.intents map {
          intent =>
            if (Option(packageManager.resolveActivity(intent, 0)).isEmpty) {
              intent.extractPackageName()
            } else {
              None
            }
        }).flatten
        (for {
          user <- userServices.getUser
          token <- user.sessionToken
          androidId <- userServices.getAndroidId
        } yield {
            apiServices.googlePlayPackages(GooglePlayPackagesRequest(androidId, token, packagesNoFound)) map {
              response =>
                val futures = response.packages map {
                  p =>
                    (p.app.docid, p.app.getIcon)
                } flatMap {
                  case (packageName, maybeIcon) => maybeIcon map {
                    icon =>
                      imageServices.storeImageApp(StoreImageAppRequest(packageName, icon))
                  }
                }
                Future.sequence(futures) map {
                  response =>
                    promise.success(PackagesResponse(response.flatMap (_.packageName)))
                } recover {
                  case _ => promise.success(PackagesResponse(Seq.empty))
                }
            } recover {
              case _ => promise.success(PackagesResponse(Seq.empty))
            }
          }) getOrElse promise.success(PackagesResponse(Seq.empty))
        promise.future
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
              promise.success(CategorizeAppsResponse(true))
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
                          promise.success(CategorizeAppsResponse(true))
                      } recover {
                        case _ => promise.success(CategorizeAppsResponse(false))
                      }
                  } recover {
                    case _ => promise.success(CategorizeAppsResponse(false))
                  }
                }).getOrElse(promise.success(CategorizeAppsResponse(false)))
            }
        }
        promise.future
      }

  }

}
