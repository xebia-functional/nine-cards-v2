package com.fortysevendeg.ninecardslauncher.process.device.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppDockType
import com.fortysevendeg.ninecardslauncher.process.device._

trait DockAppsDeviceProcessImpl {

  self: DeviceConversions
    with DeviceProcessDependencies
    with ImplicitsDeviceException =>

  def saveDockApp(packageName: String, intent: NineCardIntent, imagePath: String, position: Int) =
    (for {
      _ <- persistenceServices.createOrUpdateDockApp(toCreateOrUpdateDockAppRequest(packageName, AppDockType, intent, imagePath, position))
    } yield ()).resolve[DockAppException]

  def getDockApps =
    (for {
      apps <- persistenceServices.fetchDockApps
    } yield apps map toDockApp).resolve[DockAppException]

  def deleteAllDockApps =
    (for {
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[DockAppException]

}
