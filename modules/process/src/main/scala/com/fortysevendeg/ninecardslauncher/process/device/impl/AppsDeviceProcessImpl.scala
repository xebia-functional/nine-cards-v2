package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Misc, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps
import com.fortysevendeg.ninecardslauncher.process.device.utils.KnownCategoriesUtil
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.GooglePlayPackagesResponse
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, OrderByName}
import rapture.core.Answer

trait AppsDeviceProcessImpl extends KnownCategoriesUtil {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsImageExceptions
    with ImplicitsPersistenceServiceExceptions =>

  val apiUtils = new ApiUtils(persistenceServices)

  def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      apps <- persistenceServices.fetchApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield apps map toApp).resolve[AppException]

  def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      iter <- persistenceServices.fetchIterableApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]

  def getIterableAppsByCategory(category: String)(implicit context: ContextSupport) =
    (for {
      iter <- persistenceServices.fetchIterableAppsByCategory(category, OrderByName, ascending = true)
    } yield new IterableApps(iter)).resolve[AppException]

  def getTermCountersForApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      counters <- orderBy match {
        case GetByName => persistenceServices.fetchAlphabeticalAppsCounter
        case GetByCategory => persistenceServices.fetchCategorizedAppsCounter
        case _ => persistenceServices.fetchInstallationDateAppsCounter
      }
    } yield counters map toTermCounter).resolve[AppException]

  def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport)  =
    (for {
      iter <- persistenceServices.fetchIterableAppsByKeyword(keyword, toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]

  def saveInstalledApps(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      installedApps <- appsServices.getInstalledApplications
      googlePlayPackagesResponse <- apiServices.googlePlayPackages(installedApps map (_.packageName))(requestConfig)
        .resolveTo(GooglePlayPackagesResponse(200, Seq.empty))
      apps = installedApps map { app =>
        val knownCategory = findCategory(app.packageName)
        val category = knownCategory getOrElse {
          val categoryName = googlePlayPackagesResponse.packages find(_.app.docid == app.packageName) flatMap (_.app.details.appDetails.appCategory.headOption)
          categoryName map (NineCardCategory(_)) getOrElse Misc
        }
        toAddAppRequest(app, category)
      }
      _ <- persistenceServices.addApps(apps)
    } yield ()).resolve[AppException]

  def saveApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      appCategory <- getAppCategory(packageName)
      _ <- persistenceServices.addApp(toAddAppRequest(app, appCategory))
    } yield ()).resolve[AppException]

  def deleteApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.deleteAppByPackage(packageName)
    } yield ()).resolve[AppException]

  def updateApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      Some(appPersistence) <- persistenceServices.findAppByPackage(packageName)
      appCategory <- getAppCategory(packageName)
      _ <- persistenceServices.updateApp(toUpdateAppRequest(appPersistence.id, app, appCategory))
    } yield ()).resolve[AppException]

  private[this] def getAppCategory(packageName: String)(implicit context: ContextSupport) =
    for {
      requestConfig <- apiUtils.getRequestConfig
      appCategory = apiServices.googlePlayPackage(packageName)(requestConfig).run.run match {
        case Answer(g) => (g.app.details.appDetails.appCategory map (NineCardCategory(_))).headOption.getOrElse(Misc)
        case _ => Misc
      }
    } yield appCategory

}
