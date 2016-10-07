package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{OrderByName, Misc, NineCardCategory}
import cards.nine.process.device._
import cards.nine.process.device.models.IterableApps
import cards.nine.process.device.utils.KnownCategoriesUtil
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.GooglePlayPackagesResponse
import cards.nine.services.image._
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions

trait AppsDeviceProcessImpl
  extends DeviceProcess
  with KnownCategoriesUtil {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsImageExceptions
    with ImplicitsPersistenceServiceExceptions =>

  val apiUtils = new ApiUtils(persistenceServices)

  def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      apps <- persistenceServices.fetchApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield apps map (_.toData)).resolve[AppException]

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
        .resolveLeftTo(GooglePlayPackagesResponse(200, Seq.empty))
      apps = installedApps map { app =>
        val knownCategory = findCategory(app.packageName)
        val category = knownCategory getOrElse {
          val categoryName = googlePlayPackagesResponse.packages find(_.packageName == app.packageName) flatMap (_.category)
          categoryName map (NineCardCategory(_)) getOrElse Misc
        }
        (app, category)
      }
      _ <- persistenceServices.addApps(apps)
    } yield ()).resolve[AppException]

  def saveApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      application <- appsServices.getApplication(packageName)
      appCategory <- getAppCategory(packageName)
      applicationAdded <- persistenceServices.addApp((application, appCategory))
    } yield applicationAdded.toData).resolve[AppException]

  def deleteApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.deleteAppByPackage(packageName)
    } yield ()).resolve[AppException]

  def updateApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      appPersistence <- persistenceServices.findAppByPackage(packageName).resolveOption()
      appCategory <- getAppCategory(packageName)
      _ <- persistenceServices.updateApp((appPersistence.id, app, appCategory))
    } yield ()).resolve[AppException]

  private[this] def getAppCategory(packageName: String)(implicit context: ContextSupport) =
    for {
      requestConfig <- apiUtils.getRequestConfig
      appCategory <- apiServices.googlePlayPackage(packageName)(requestConfig)
        .map(_.app.category)
        .resolveLeftTo(None)
    } yield {
      appCategory map (NineCardCategory(_)) getOrElse Misc
    }

}
