package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppDockType
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServiceException}
import rapture.core.Answer

import scalaz.concurrent.Task

trait DockAppsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions=>

  def generateDockApps(size: Int)(implicit context: ContextSupport) =
    (for {
      allDefaultApps <- appsServices.getDefaultApps
      defaultApps <- getAppsImages(allDefaultApps map (_.packageName))
      images = defaultApps.flatten map (app => (app.packageName, app.imagePath))
      _ <- saveDockApps(matchAppsWithImages(allDefaultApps, images).take(size))
    } yield ()).resolve[DockAppException]

  def getDockApps =
    (for {
      apps <- persistenceServices.fetchDockApps
    } yield apps map toDockApp).resolve[DockAppException]

  def deleteAllDockApps() =
    (for {
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[DockAppException]

  private[this] def getAppsImages(packageNames: Seq[String]) = Service {
    val tasks = packageNames map (packageName =>
      persistenceServices.findAppByPackage(packageName).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(app) => app }))
  }

  private[this] def matchAppsWithImages(apps: Seq[Application], images: Seq[(String, String)])(implicit context: ContextSupport) : Seq[DockApp] = {
    apps.zipWithIndex.map {
      case (app, index) =>
        val image = images find (i => i._1 == app.packageName) map (_._2)
        toDockApp(app, index, image getOrElse "")
    }
  }

  private[this] def saveDockApps(apps: Seq[DockApp]) = Service {
    val tasks = apps map (app =>
      persistenceServices.createOrUpdateDockApp(toCreateOrUpdateDockAppRequest(app.name, AppDockType, app.intent, app.imagePath, app.position)).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(app) => app }))
  }

}
