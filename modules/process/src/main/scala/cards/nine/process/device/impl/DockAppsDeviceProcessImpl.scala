package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{AppDockType, DockType}
import cards.nine.models.{ApplicationData, DockAppData, NineCardsIntent}
import cards.nine.process.device._
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions

trait DockAppsDeviceProcessImpl extends DeviceProcess {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions=>

  def generateDockApps(size: Int)(implicit context: ContextSupport) =
    (for {
      allDefaultApps <- appsServices.getDefaultApps
      defaultApps <- persistenceServices.fetchAppByPackages(allDefaultApps map (_.packageName))
      images = defaultApps map (app => (app.packageName, ""))
      apps = matchAppsWithImages(allDefaultApps, images).take(size)
      dockAppsData = apps map (app => DockAppData(app.name, AppDockType, app.intent, app.imagePath, app.position))
      dockApps <- persistenceServices.createOrUpdateDockApp(dockAppsData)
    } yield dockApps).resolve[DockAppException]

  def createOrUpdateDockApp(name: String, dockType: DockType, intent: NineCardsIntent, imagePath: String, position: Int) =
    (for {
      _ <- persistenceServices.createOrUpdateDockApp(Seq(DockAppData(name, dockType, intent, imagePath, position)))
    } yield ()).resolve[DockAppException]

  def saveDockApps(items: Seq[DockAppData]) =
    (for {
      dockApps <- persistenceServices.createOrUpdateDockApp(items)
    } yield dockApps).resolve[DockAppException]

  def getDockApps =
    (for {
      apps <- persistenceServices.fetchDockApps
    } yield apps).resolve[DockAppException]

  def deleteAllDockApps() =
    (for {
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[DockAppException]

  private[this] def matchAppsWithImages(apps: Seq[ApplicationData], images: Seq[(String, String)])(implicit context: ContextSupport) : Seq[DockAppData] = {
    apps.zipWithIndex.map {
      case (app, index) =>
        val image = images find (i => i._1 == app.packageName) map (_._2)
        DockAppData(
          name = app.packageName,
          dockType = AppDockType,
          intent = toNineCardIntent(app),
          imagePath = image getOrElse "",
          position = index)
    }
  }

}
