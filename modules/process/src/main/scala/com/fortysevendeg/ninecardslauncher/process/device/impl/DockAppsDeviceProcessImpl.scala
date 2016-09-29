package com.fortysevendeg.ninecardslauncher.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppDockType, DockType}
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import cards.nine.services.apps.models.Application
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions
import cards.nine.commons.services.TaskService._

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
      requests = apps map (app => toCreateOrUpdateDockAppRequest(app.name, AppDockType, app.intent, app.imagePath, app.position))
      dockApps <- persistenceServices.createOrUpdateDockApp(requests)
    } yield dockApps map toDockApp).resolve[DockAppException]

  def createOrUpdateDockApp(name: String, dockType: DockType, intent: NineCardIntent, imagePath: String, position: Int) =
    (for {
      _ <- persistenceServices.createOrUpdateDockApp(Seq(toCreateOrUpdateDockAppRequest(name, dockType, intent, imagePath, position)))
    } yield ()).resolve[DockAppException]

  def saveDockApps(items: Seq[SaveDockAppRequest]) =
    (for {
      dockApps <- persistenceServices.createOrUpdateDockApp(items map toCreateOrUpdateDockAppRequest)
    } yield dockApps map toDockApp).resolve[DockAppException]

  def getDockApps =
    (for {
      apps <- persistenceServices.fetchDockApps
    } yield apps map toDockApp).resolve[DockAppException]

  def deleteAllDockApps() =
    (for {
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[DockAppException]

  private[this] def matchAppsWithImages(apps: Seq[Application], images: Seq[(String, String)])(implicit context: ContextSupport) : Seq[DockApp] = {
    apps.zipWithIndex.map {
      case (app, index) =>
        val image = images find (i => i._1 == app.packageName) map (_._2)
        toDockApp(app, index, image getOrElse "")
    }
  }

}
