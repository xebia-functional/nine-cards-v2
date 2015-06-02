package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiUserService, ApiUserConfigService}
import com.fortysevendeg.ninecardslauncher.commons.ContextWrapperProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.services.api.impl.{ApiServicesConfig, ApiServicesImpl}
import com.fortysevendeg.ninecardslauncher.services.api.{GooglePlayPackagesRequest, GooglePlaySimplePackagesRequest, GooglePlaySimplePackagesResponse, GooglePlayPackagesResponse}
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.{StoreImageAppResponse, StoreImageAppRequest, ImageServicesComponent}
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.modules.user.UserServicesComponent
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import play.api.libs.json._

trait AppManagerServicesComponentImpl
  extends AppManagerServicesComponent {

  self: ContextWrapperProvider
    with ImageServicesComponent
    with RepositoryServicesComponent
    with UserServicesComponent =>

  lazy val appManagerServices = new AppManagerServicesImpl

  class AppManagerServicesImpl
    extends AppManagerServices {

    val packageManager = contextProvider.application.getPackageManager

    private lazy val serviceClient = new ServiceClient(
      httpClient = new OkHttpClient(),
      baseUrl = contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_base_url))

    private lazy val apiServices = new ApiServicesImpl(
      ApiServicesConfig(
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_app_id),
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_app_key),
        contextProvider.application.getString(com.fortysevendeg.ninecardslauncher2.R.string.api_localization)),
      new ApiUserService(serviceClient),
      new ApiGooglePlayService(serviceClient),
      new ApiUserConfigService(serviceClient))

    override def getApps: Service[GetAppsRequest, GetAppsResponse] =
      request =>
        Future {
          val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
          mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

          val l = packageManager.queryIntentActivities(mainIntent, 0).toSeq
          val apps = Seq(l: _*)

          val appItems: Seq[AppItem] = apps map {
            resolveInfo =>
              val name = resolveInfo.loadLabel(packageManager).toString
              val intent = new NineCardIntent(NineCardIntentExtras(
                package_name = Some(resolveInfo.activityInfo.applicationInfo.packageName),
                class_name = Some(resolveInfo.activityInfo.name)
              ))
              intent.setAction(OpenApp)
              import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntentImplicits._
              AppItem(
                name = name,
                packageName = resolveInfo.activityInfo.applicationInfo.packageName,
                imagePath = imageServices.createAppBitmap(name, resolveInfo),
                intent = Json.toJson(intent).toString())
          }

          GetAppsResponse(appItems)
        }

    override def createBitmapsForNoPackagesInstalled: Service[IntentsRequest, PackagesResponse] =
      request => {
        val packagesNoFound = request.intents flatMap {
          intent =>
            if (Option(packageManager.resolveActivity(intent, 0)).isEmpty) intent.extractPackageName()
            else None
        }
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
