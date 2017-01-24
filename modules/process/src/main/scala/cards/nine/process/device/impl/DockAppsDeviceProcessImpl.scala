/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{AppDockType, DockType}
import cards.nine.models.{
  ApplicationData,
  DockAppData,
  NineCardsIntent,
  NineCardsIntentConversions
}
import cards.nine.process.device._
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions

trait DockAppsDeviceProcessImpl extends DeviceProcess with NineCardsIntentConversions {

  self: DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions =>

  def generateDockApps(size: Int)(implicit context: ContextSupport) =
    (for {
      allDefaultApps <- appsServices.getDefaultApps
      defaultApps    <- persistenceServices.fetchAppByPackages(allDefaultApps map (_.packageName))
      images = defaultApps map (app => (app.packageName, ""))
      apps   = matchAppsWithImages(allDefaultApps, images).take(size)
      dockAppsData = apps map (app =>
                                 DockAppData(
                                   app.name,
                                   AppDockType,
                                   app.intent,
                                   app.imagePath,
                                   app.position))
      dockApps <- persistenceServices.createOrUpdateDockApp(dockAppsData)
    } yield dockApps).resolve[DockAppException]

  def createOrUpdateDockApp(
      name: String,
      dockType: DockType,
      intent: NineCardsIntent,
      imagePath: String,
      position: Int) =
    (for {
      _ <- persistenceServices.createOrUpdateDockApp(
        Seq(DockAppData(name, dockType, intent, imagePath, position)))
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

  def deleteDockAppByPosition(position: Int) =
    (for {
      _ <- persistenceServices.deleteDockAppByPosition(position)
    } yield ()).resolve[DockAppException]

  private[this] def matchAppsWithImages(apps: Seq[ApplicationData], images: Seq[(String, String)])(
      implicit context: ContextSupport): Seq[DockAppData] = {
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
