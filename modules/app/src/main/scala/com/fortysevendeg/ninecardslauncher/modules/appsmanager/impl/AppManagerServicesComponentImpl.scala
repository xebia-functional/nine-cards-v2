package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.commons.ContextWrapperProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.models._
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.{StoreImageAppResponse, StoreImageAppRequest, ImageServicesComponent}
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import play.api.libs.json._

trait AppManagerServicesComponentImpl
  extends AppManagerServicesComponent {

  self: ContextWrapperProvider
    with ImageServicesComponent
    with RepositoryServicesComponent
    with UserServicesComponent
    with ApiServicesComponent =>

  lazy val appManagerServices = new AppManagerServicesImpl

  class AppManagerServicesImpl
    extends AppManagerServices {

    val packageManager = contextProvider.application.getPackageManager

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
              val intent = new NineCardIntent(NineCardIntentExtras(
                package_name = Some(resolveInfo.activityInfo.applicationInfo.packageName),
                class_name = Some(resolveInfo.activityInfo.name)
              ))
              intent.setAction(OpenApp)
              import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
              AppItem(
                name = name,
                packageName = resolveInfo.activityInfo.applicationInfo.packageName,
                imagePath = imageServices.createAppBitmap(name, resolveInfo),
                intent = Json.toJson(intent).toString())
          }

          GetAppsResponse(appitems)
        }

    override def createBitmapsForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse] =
      request => {
        val packagesNoFound = (request.intents map {
          intent =>
            if (Option(packageManager.resolveActivity(intent, 0)).isEmpty) {
              intent.extractPackageName()
            } else {
              None
            }
        }).flatten
        (for {
          GooglePlayPackagesResponse(_, packages) <- googlePlayPackages(packagesNoFound)
          storeImageResponses <- storeImages(packages)
        } yield {
          PackagesResponse(storeImageResponses flatMap (_.packageName))
        }).recover {
          case _ => PackagesResponse(Seq.empty)
        }
      }

    private def storeImages(packages: Seq[GooglePlayPackage]): Future[Seq[StoreImageAppResponse]] =
      Future.sequence(packages map {
        p =>
          (p.app.docid, p.app.getIcon)
      } flatMap {
        case (packageName, maybeIcon) => maybeIcon map {
          icon =>
            imageServices.storeImageApp(StoreImageAppRequest(packageName, icon))
        }
      })

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
      request =>
        (for {
          GetCategorizedAppsResponse(apps) <- getCategorizedApps(GetCategorizedAppsRequest())
          packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
          GooglePlaySimplePackagesResponse(_, packages) <- googlePlaySimplePackages(packagesWithoutCategory)
          _ <- insertRespositories(packages)
        } yield CategorizeAppsResponse()).recover {
          case _ => throw CategorizeAppsException()
        }

    private def insertRespositories(packages: GooglePlaySimplePackages): Future[Seq[InsertCacheCategoryResponse]] =
      Future.sequence(packages.items map {
        app =>
          repositoryServices.insertCacheCategory(InsertCacheCategoryRequest(
            packageName = app.packageName,
            category = app.appCategory,
            starRating = app.starRating,
            numDownloads = app.numDownloads,
            ratingsCount = app.ratingCount,
            commentCount = app.commentCount
          ))
      })

    // TODO These methods should be remove when we'll add android and tocken in Api Component in ticket 9C-151
    private def googlePlaySimplePackages(packages: Seq[String]): Future[GooglePlaySimplePackagesResponse] =
      (for {
        user <- userServices.getUser
        token <- user.sessionToken
        androidId <- userServices.getAndroidId
      } yield {
          apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(androidId, token, packages))
        }) getOrElse(throw new RuntimeException("User not found"))

    private def googlePlayPackages(packages: Seq[String]): Future[GooglePlayPackagesResponse] =
      (for {
        user <- userServices.getUser
        token <- user.sessionToken
        androidId <- userServices.getAndroidId
      } yield {
          apiServices.googlePlayPackages(GooglePlayPackagesRequest(androidId, token, packages))
        }) getOrElse(throw new RuntimeException("User not found"))


  }

}
