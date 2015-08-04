package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device.DeviceExceptions.{AppCategorizationException, _}
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceExceptions.PersistenceServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import rapture.core.Answer

import scalaz.concurrent.Task

class DeviceProcessImpl(
  appsService: AppsServices,
  apiServices: ApiServices,
  persistenceServices: PersistenceServices,
  imageServices: ImageServices)
  extends DeviceProcess
  with ImplicitsImageExceptions
  with ImplicitsPersistenceExceptions
  with DeviceConversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getCategorizedApps(implicit context: ContextSupport) = {
    (for {
      cacheCategories <- persistenceServices.fetchCacheCategories
      apps <- getApps
    } yield {
        apps map (app => copyCacheCategory(app, cacheCategories.find(_.packageName == app.packageName)))
      }).resolve[AppCategorizationException]
  }

  override def categorizeApps(implicit context: ContextSupport) =
    (for {
      apps <- getCategorizedApps
      packagesWithoutCategory = apps.filter(_.category.isEmpty) map (_.packageName)
      requestConfig <- apiUtils.getRequestConfig
      response <- apiServices.googlePlaySimplePackages(packagesWithoutCategory)(requestConfig)
      _ <- addCacheCategories(toAddCacheCategoryRequestSeq(response.apps.items))
    } yield ()).resolve[AppCategorizationException]

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      response <- apiServices.googlePlayPackages(packages)(requestConfig)
      _ <- createBitmapsFromAppWebSite(toAppWebSiteSeq(response.packages))
    } yield ()).resolve[CreateBitmapException]

  private[this] def getApps(implicit context: ContextSupport):
  ServiceDef2[Seq[AppCategorized], AppsInstalledException with BitmapTransformationException] =
    for {
      applications <- appsService.getInstalledApps
      paths <- createBitmapsFromAppPackage(toAppPackageSeq(applications))
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

  private[this] def addCacheCategories(items: Seq[AddCacheCategoryRequest]):
  ServiceDef2[Seq[CacheCategory], PersistenceServiceException] = Service {
    val tasks = items map (persistenceServices.addCacheCategory(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(app) => app }))
  }

  private[this] def createBitmapsFromAppPackage(apps: Seq[AppPackage])(implicit context: ContextSupport):
  ServiceDef2[Seq[AppPackagePath], BitmapTransformationException] = Service {
    val tasks = apps map (imageServices.saveAppIcon(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[BitmapTransformationException](list.collect { case Answer(app) => app }))
  }

  private[this] def createBitmapsFromAppWebSite(apps: Seq[AppWebsite])(implicit context: ContextSupport):
  ServiceDef2[Seq[AppWebsitePath], BitmapTransformationException] = Service {
    val tasks = apps map imageServices.saveAppIcon map (_.run)
    Task.gatherUnordered(tasks) map (list => CatchAll[BitmapTransformationException](list.collect { case Answer(app) => app }))
  }

}
