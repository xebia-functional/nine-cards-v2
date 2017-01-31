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

package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.{DockApp, DockAppData}
import cards.nine.repository.provider.DockAppEntity
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait DockAppPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def createOrUpdateDockApp(dockAppDataSeq: Seq[DockAppData]) =
    (for {
      fetchedDockApps <- dockAppRepository.fetchDockApps(where =
        s"${DockAppEntity.position} IN (${dockAppDataSeq.map(_.position).mkString("\"", ",", "\"")})")
      items = dockAppDataSeq map { dockAppData =>
        fetchedDockApps.find(_.data.position == dockAppData.position) map { dockApp =>
          (dockAppData, Some(dockApp.id))
        } getOrElse {
          (dockAppData, None)
        }
      }
      (toAdd, toUpdate) = items.partition(_._2.isEmpty)
      addedDockapps <- dockAppRepository.addDockApps(
        toAdd.map(req => toRepositoryDockAppData(req._1)))
      toUpdateDockApps = toUpdate.flatMap(req =>
        req._2 map (id => toRepositoryDockApp(id, req._1)))
      _ <- dockAppRepository.updateDockApps(toUpdateDockApps)
    } yield (addedDockapps ++ toUpdateDockApps) map toDockApp).resolve[PersistenceServiceException]

  def deleteAllDockApps() =
    (for {
      deleted <- dockAppRepository.deleteDockApps()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteDockAppByPosition(position: Int) =
    (for {
      _ <- dockAppRepository.deleteDockApps(where = s"${DockAppEntity.position} = $position")
    } yield ()).resolve[PersistenceServiceException]

  def deleteDockApp(dockApp: DockApp) =
    (for {
      deleted <- dockAppRepository.deleteDockApp(toRepositoryDockApp(dockApp))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchDockApps =
    (for {
      dockAppItems <- dockAppRepository.fetchDockApps()
    } yield dockAppItems map toDockApp).resolve[PersistenceServiceException]

  def findDockAppById(dockAppId: Int) =
    (for {
      maybeDockApp <- dockAppRepository.findDockAppById(dockAppId)
    } yield maybeDockApp map toDockApp).resolve[PersistenceServiceException]

}
