package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsServices, GetInstalledAppsRequest, GetInstalledAppsResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.rest.client.ServiceClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DeviceProcessImpl(
    appsService: AppsServices,
    serviceClient: ServiceClient,
    apiServices: ApiServices,
    persistenceServices: PersistenceServices)
    extends DeviceProcess
    with DeviceConversions {

  override def getApps(request: GetAppsRequest)(implicit context: ContextSupport): Future[GetAppsResponse] =
    for {
      GetInstalledAppsResponse(apps) <- appsService.getInstalledApps(GetInstalledAppsRequest())
    } yield GetAppsResponse(toAppItemSeq(apps))

  override def getAppsByCategory(request: GetAppsByCategoryRequest)(implicit context: ContextSupport): Future[GetAppsByCategoryResponse] =
    getCategorizedApps(GetCategorizedAppsRequest()) map {
      response =>
        val apps = response.apps.filter(_.category == Some(request.category))
        GetAppsByCategoryResponse(apps)
    }

  override def getCategorizedApps(request: GetCategorizedAppsRequest)(implicit context: ContextSupport): Future[GetCategorizedAppsResponse] =
    for {
      FetchCacheCategoriesResponse(cacheCategory) <- persistenceServices.fetchCacheCategories(
        FetchCacheCategoriesRequest())
      GetAppsResponse(apps) <- getApps(GetAppsRequest())
    } yield {
      val categorizedApps = apps map {
        app =>
          app.copy(category = cacheCategory.find(_.packageName == app.packageName).map(_.category))
      }
      GetCategorizedAppsResponse(categorizedApps)
    }

  override def categorizeApps(request: CategorizeAppsRequest)(implicit context: ContextSupport): Future[CategorizeAppsResponse] =
    (for {
      GetCategorizedAppsResponse(apps) <- getCategorizedApps(GetCategorizedAppsRequest())
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      (androidId, token) <- getTokenAndAndroidId
      GooglePlaySimplePackagesResponse(_, packages) <- apiServices.googlePlaySimplePackages(GooglePlaySimplePackagesRequest(androidId, token, packagesWithoutCategory))
      _ <- insertRepositories(packages)
    } yield CategorizeAppsResponse()).recover {
      case _ => throw CategorizeAppsException()
    }

  private[this] def insertRepositories(packages: GooglePlaySimplePackages): Future[Seq[AddCacheCategoryResponse]] =
    Future.sequence(packages.items map {
      app =>
        persistenceServices.addCacheCategory(AddCacheCategoryRequest(
          packageName = app.packageName,
          category = app.appCategory,
          starRating = app.starRating,
          numDownloads = app.numDownloads,
          ratingsCount = app.ratingCount,
          commentCount = app.commentCount
        ))
    })

  private def getTokenAndAndroidId()(implicit context: ContextSupport): Future[(String, String)] =
    for {
      token <- getSessionToken
      androidId <- persistenceServices.getAndroidId
    } yield (androidId, token)

  private def getSessionToken(implicit context: ContextSupport): Future[String] =
    persistenceServices.getUser map (_.sessionToken getOrElse (throw new RuntimeException("User not found")))

}

