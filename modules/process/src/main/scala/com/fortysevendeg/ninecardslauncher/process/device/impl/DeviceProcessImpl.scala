package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class DeviceProcessImpl(
  appsService: AppsServices,
  apiServices: ApiServices,
  persistenceServices: PersistenceServices,
  imageServices: ImageServices
  )
  extends DeviceProcess
  with DeviceConversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getCategorizedApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppCategorized]] =
    for {
      cacheCategories <- persistenceServices.fetchCacheCategories ▹ eitherT
      apps <- getApps ▹ eitherT
    } yield {
      apps map (app => copyCacheCategory(app, cacheCategories.find(_.packageName == app.packageName)))
    }

  override def categorizeApps(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      apps <- getCategorizedApps ▹ eitherT
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      response <- apiServices.googlePlaySimplePackages(packagesWithoutCategory)(requestConfig) ▹ eitherT
      _ <- addCacheCategories(toAddCacheCategoryRequestSeq(response.apps.items)) ▹ eitherT
    } yield ()

  override def createBitmapsForNoPackagesInstalled(packages: Seq[String])(implicit context: ContextSupport): Task[NineCardsException \/ Unit] =
    for {
      requestConfig <- apiUtils.getRequestConfig ▹ eitherT
      response <- apiServices.googlePlayPackages(packages)(requestConfig) ▹ eitherT
      _ <- createBitmapsFromAppWebSite(toAppWebSiteSeq(response.packages)) ▹ eitherT
    } yield ()

  private[this] def getApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppCategorized]] =
    for {
      applications <- appsService.getInstalledApps ▹ eitherT
      paths <- createBitmapsFromAppPackage(toAppPackageSeq(applications)) ▹ eitherT
    } yield {
      applications map {
        app =>
          val path = paths.find {
            path =>
              path.packageName.equals(app.packageName) && path.className.equals(app.className)
          } map (_.path)
          AppCategorized(
            name = app.name,
            packageName = app.packageName,
            className = app.className,
            imagePath = path)
      }
    }

  private[this] def addCacheCategories(items: Seq[AddCacheCategoryRequest]): Task[NineCardsException \/ Seq[CacheCategory]] = {
    val tasks = items map persistenceServices.addCacheCategory
    Task.gatherUnordered(tasks) map (_.collect { case \/-(category) => category }.right[NineCardsException])
  }

  private[this] def createBitmapsFromAppPackage(apps: Seq[AppPackage])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppPackagePath]] = {
    val tasks = apps map imageServices.saveAppIcon
    Task.gatherUnordered(tasks) map (_.collect { case \/-(app) => app }.right[NineCardsException])
  }

  private[this] def createBitmapsFromAppWebSite(apps: Seq[AppWebsite])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[AppWebsitePath]] = {
    val tasks = apps map imageServices.saveAppIcon
    Task.gatherUnordered(tasks) map (_.collect { case \/-(app) => app }.right[NineCardsException])
  }

}
