package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Misc, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, IterableApps}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServiceException
import com.fortysevendeg.ninecardslauncher.services.contacts.models.ContactCounter
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{DataCounter, App}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddAppRequest, ImplicitsPersistenceServiceExceptions, PersistenceServiceException}
import rapture.core.Answer

import scalaz.concurrent.Task

trait AppsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsImageExceptions
    with ImplicitsPersistenceServiceExceptions =>

  val apiUtils = new ApiUtils(persistenceServices)

  val emptyDataCounterService = Service {
    Task {
      CatchAll[PersistenceServiceException] {
        Seq.empty[DataCounter]
      }
    }
  }

  def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      apps <- persistenceServices.fetchApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield apps map toApp).resolve[AppException]

  def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      iter <- persistenceServices.fetchIterableApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]

  def getTermCountersForApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      counters <- orderBy match {
        case GetByName => persistenceServices.fetchAlphabeticalAppsCounter
        case GetByCategory => persistenceServices.fetchCategorizedAppsCounter
        case _ => emptyDataCounterService
      }
    } yield counters map toTermCounter).resolve[AppException]

  def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport)  =
    (for {
      iter <- persistenceServices.fetchIterableAppsByKeyword(keyword, toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]

  def saveInstalledApps(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      installedApps <- appsService.getInstalledApplications
      googlePlayPackagesResponse <- apiServices.googlePlayPackages(installedApps map (_.packageName))(requestConfig)
      appPaths <- createBitmapsFromAppPackage(toAppPackageSeq(installedApps))
      apps = installedApps map { app =>
        val path = appPaths.find { path =>
          path.packageName.equals(app.packageName) && path.className.equals(app.className)
        } map (_.path)
        val category = googlePlayPackagesResponse.packages find(_.app.docid == app.packageName) flatMap (_.app.details.appDetails.appCategory.headOption)
        toAddAppRequest(app, (category map (NineCardCategory(_))).getOrElse(Misc), path.getOrElse(""))
      }
      _ <- addApps(apps)
    } yield ()).resolve[AppException]

  def saveApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsService.getApplication(packageName)
      appCategory <- getAppCategory(packageName)
      appPackagePath <- imageServices.saveAppIcon(toAppPackage(app))
      _ <- persistenceServices.addApp(toAddAppRequest(app, appCategory, appPackagePath.path))
    } yield ()).resolve[AppException]

  def deleteApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.deleteAppByPackage(packageName)
    } yield ()).resolve[AppException]

  def updateApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsService.getApplication(packageName)
      Some(appPersistence) <- persistenceServices.findAppByPackage(packageName)
      appCategory <- getAppCategory(packageName)
      appPackagePath <- imageServices.saveAppIcon(toAppPackage(app))
      _ <- persistenceServices.updateApp(toUpdateAppRequest(appPersistence.id, app, appCategory, appPackagePath.path))
    } yield ()).resolve[AppException]

  def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      response <- apiServices.googlePlayPackages(packages)(requestConfig)
      _ <- createBitmapsFromAppWebSite(toAppWebSiteSeq(response.packages))
    } yield ()).resolve[CreateBitmapException]

  private[this] def getAppCategory(packageName: String)(implicit context: ContextSupport) =
    for {
      requestConfig <- apiUtils.getRequestConfig
      appCategory = apiServices.googlePlayPackage(packageName)(requestConfig).run.run match {
        case Answer(g) => (g.app.details.appDetails.appCategory map (NineCardCategory(_))).headOption.getOrElse(Misc)
        case _ => Misc
      }
    } yield appCategory

  private[this] def addApps(items: Seq[AddAppRequest]):
  ServiceDef2[Seq[App], PersistenceServiceException] = Service {
    val tasks = items map (persistenceServices.addApp(_).run)
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
