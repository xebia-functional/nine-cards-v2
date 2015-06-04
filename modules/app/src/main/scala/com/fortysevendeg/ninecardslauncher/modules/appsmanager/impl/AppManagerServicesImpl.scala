package com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl

import android.content.Intent
import android.content.pm.{PackageManager, ResolveInfo}
import com.fortysevendeg.ninecardslauncher.models._
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.appsmanager._
import com.fortysevendeg.ninecardslauncher.modules.image.ImageServices
import com.fortysevendeg.ninecardslauncher.modules.repository._
import com.fortysevendeg.ninecardslauncher.ui.commons.NineCardsIntent._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AppManagerServicesImpl(
    packageManager: PackageManager,
    apiServices: ApiServices,
    imageServices: ImageServices,
    repositoryServices: RepositoryServices)
  extends AppManagerServices {

  override def getApps()(implicit executionContext: ExecutionContext): Future[Seq[AppItem]] =
    for {
      activities <- queryActivities()
      appItems <- Future.sequence(activities map createAppItem)
    } yield appItems

  private def queryActivities()(implicit executionContext: ExecutionContext): Future[Seq[ResolveInfo]] =
    Future {
      val mainIntent: Intent = new Intent(Intent.ACTION_MAIN, null)
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

      import scala.collection.JavaConverters._
      val apps: Seq[ResolveInfo] = packageManager.queryIntentActivities(mainIntent, 0).asScala
      apps
    }

  private def createAppItem(resolveInfo: ResolveInfo)(implicit executionContext: ExecutionContext): Future[AppItem] = {
    val name = resolveInfo.loadLabel(packageManager).toString
    val intent = new NineCardIntent(NineCardIntentExtras(
      package_name = Some(resolveInfo.activityInfo.applicationInfo.packageName),
      class_name = Some(resolveInfo.activityInfo.name)))
    intent.setAction(OpenApp)
    import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
    imageServices.createAppBitmap(name, resolveInfo) map { imagePath =>
      AppItem(
        name = name,
        packageName = resolveInfo.activityInfo.applicationInfo.packageName,
        imagePath = imagePath,
        intent = Json.toJson(intent).toString())
    }
  }

  override def createBitmapsForNoPackagesInstalled(intents: Seq[NineCardIntent])(implicit executionContext: ExecutionContext): Future[Seq[String]] = {
    val packagesNoFound = intents flatMap {
      intent =>
        if (Option(packageManager.resolveActivity(intent, 0)).isEmpty) intent.extractPackageName()
        else None
    }
    for {
      GooglePlayPackagesResponse(_, packages) <-  apiServices.googlePlayPackages(GooglePlayPackagesRequest(packagesNoFound))
      storeImageResponses <- storeImages(packages)
    } yield storeImageResponses
  }

  private def storeImages(packages: Seq[GooglePlayPackage]): Future[Seq[String]] =
    Future.sequence(packages map {
      p =>
        (p.app.docid, p.app.getIcon)
    } flatMap {
      case (packageName, maybeIcon) => maybeIcon map {
        icon =>
          imageServices.storeImageApp(packageName, icon)
      }
    })

  override def getCategorizedApps()(implicit executionContext: ExecutionContext): Future[Seq[AppItem]] =
    for {
      GetCacheCategoryResponse(cacheCategory) <- repositoryServices.getCacheCategory(GetCacheCategoryRequest())
      apps <- getApps()
    } yield {
      val categorizedApps = apps map {
        app =>
          app.copy(category = cacheCategory.find(_.packageName == app.packageName).map(_.category))
      }
      categorizedApps
    }

  override def getAppsByCategory(category: String)(implicit executionContext: ExecutionContext): Future[Seq[AppItem]] =
    getCategorizedApps() map (_.filter(_.category.contains(category)))

  override def categorizeApps()(implicit executionContext: ExecutionContext): Future[Unit] =
    (for {
      apps <- getCategorizedApps()
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      GooglePlaySimplePackagesResponse(_, packages) <- apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(packagesWithoutCategory))
      _ <- insertRepositories(packages)
    } yield ()).recover {
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
