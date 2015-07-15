package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppItem
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.image.{AppWebsite, ImageServices}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.servies.persistence.models.CacheCategory
import com.fortysevendeg.rest.client.ServiceClient

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class DeviceProcessImpl(
  appsService: AppsServices,
  serviceClient: ServiceClient,
  apiServices: ApiServices,
  persistenceServices: PersistenceServices,
  imageServices: ImageServices
  )
  extends DeviceProcess
  with DeviceConversions {

  private val apiUtils = new ApiUtils(persistenceServices)

  override def getApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    appsService.getInstalledApps ▹ eitherT map toAppItemSeq

  override def getAppsByCategory(category: String)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    getCategorizedApps ▹ eitherT map (_.filter(_.category.contains(category)))


  override def getCategorizedApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppItem]] =
    for {
      cacheCategories <- persistenceServices.fetchCacheCategories ▹ eitherT
      apps <- getApps ▹ eitherT
    } yield {
      apps map (app => app.copy(category = cacheCategories.find(_.packageName == app.packageName).map(_.category)))
    }

  override def categorizeApps(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      apps <- getCategorizedApps ▹ eitherT
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      response <- apiServices.googlePlaySimplePackages(packagesWithoutCategory)(requestConfig) ▹ eitherT
      _ <- insertRepositories(response.apps) ▹ eitherT
    } yield ()

  override def createBitmapsForNoPackagesInstalled(packages: Seq[String])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[String]] =
    for {
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      response <- apiServices.googlePlayPackages(packages)(requestConfig) ▹ eitherT
      packages <- createBitmaps(response) ▹ eitherT
    } yield (packages)

  private[this] def insertRepositories(packages: GooglePlaySimplePackages): Task[NineCardsException \/ Seq[CacheCategory]] = {
    val tasks = packages.items map {
      app =>
        persistenceServices.addCacheCategory(AddCacheCategoryRequest(
          packageName = app.packageName,
          category = app.appCategory,
          starRating = app.starRating,
          numDownloads = app.numDownloads,
          ratingsCount = app.ratingCount,
          commentCount = app.commentCount
        ))
    }
    Task.gatherUnordered(tasks) map (_.collect { case \/-(category) => category }.right[NineCardsException])
  }

  private[this] def createBitmaps(response: GooglePlayPackagesResponse)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[String]] = {
    val tasks = response.packages map {
      case googlePlayPackage if googlePlayPackage.app.getIcon.isDefined =>
        imageServices.saveAppIcon(AppWebsite(
          packageName = googlePlayPackage.app.docid,
          url = googlePlayPackage.app.getIcon getOrElse "",
          name = googlePlayPackage.app.title
        ))
    }
    Task.gatherUnordered(tasks) map (_.collect { case \/-(packageName) => packageName }.right[NineCardsException])
  }

}

