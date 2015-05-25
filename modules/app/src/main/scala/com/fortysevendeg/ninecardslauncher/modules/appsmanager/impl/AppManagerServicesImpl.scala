package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import android.content.pm.{ResolveInfo, PackageManager}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.models._
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.{ImageServices, StoreImageAppRequest, StoreImageAppResponse}
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AppManagerServicesImpl(
    packageManager: PackageManager,
    apiServices: ApiServices,
    imageServices: ImageServices,
    repositoryServices: RepositoryServices)
  extends AppManagerServices {

  override def getApps: Service[GetAppsRequest, GetAppsResponse] =
    request =>
      Future {
        val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        import scala.collection.JavaConverters._
        val l: Seq[ResolveInfo] = packageManager.queryIntentActivities(mainIntent, 0).asScala
        val apps = Seq(l: _*)

        val appItems: Seq[AppItem] = apps map {
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
        GooglePlayPackagesResponse(_, packages) <-  apiServices.googlePlayPackages(GooglePlayPackagesRequest(packagesNoFound))
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
          val apps = response.apps.filter(_.category.contains(request.category))
          GetAppsByCategoryResponse(apps)
      }

  override def categorizeApps: Service[CategorizeAppsRequest, CategorizeAppsResponse] =
    request =>
      (for {
        GetCategorizedAppsResponse(apps) <- getCategorizedApps(GetCategorizedAppsRequest())
        packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
        GooglePlaySimplePackagesResponse(_, packages) <- apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(packagesWithoutCategory))
        _ <- insertRepositories(packages)
      } yield CategorizeAppsResponse()).recover {
        case _ => throw CategorizeAppsException()
      }

  private def insertRepositories(packages: GooglePlaySimplePackages): Future[Seq[InsertCacheCategoryResponse]] =
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


}
