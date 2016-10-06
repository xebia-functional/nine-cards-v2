package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.models.{DockApp, DockAppData}
import cards.nine.repository.provider.DockAppEntity
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions
import cards.nine.services.persistence.models.IterableDockApps

trait DockAppPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def createOrUpdateDockApp(dockApps: Seq[DockAppData]) =
    (for {
      fetchedDockApps <- dockAppRepository.fetchDockApps(where = s"${DockAppEntity.position} IN (${dockApps.map(_.position).mkString("\"", ",", "\"")})")
      items = dockApps map { dockApp =>
        fetchedDockApps.find(_.data.position == dockApp.position) map { dockApp =>
          (dockApp, Some(dockApp.id))
        } getOrElse {
          (dockApp, None)
        }
      }
      (toAdd, toUpdate) = items.partition(_._2.isEmpty)
      addedDockapps <- dockAppRepository.addDockApps(toAdd.map(req => toRepositoryDockAppData(req._1)))
      toUpdateDockApps = toUpdate.flatMap(req => req._2 map (id => toRepositoryDockApp(id, req._1)))
      _ <- dockAppRepository.updateDockApps(toUpdateDockApps)
    } yield (addedDockapps ++ toUpdateDockApps) map toDockApp).resolve[PersistenceServiceException]

  def deleteAllDockApps() =
    (for {
      deleted <- dockAppRepository.deleteDockApps()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteDockApp(dockApp: DockApp) =
    (for {
      deleted <- dockAppRepository.deleteDockApp(toRepositoryDockApp(dockApp))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchDockApps =
    (for {
      dockAppItems <- dockAppRepository.fetchDockApps()
    } yield dockAppItems map toDockApp).resolve[PersistenceServiceException]

  def fetchIterableDockApps =
    (for {
      iter <- dockAppRepository.fetchIterableDockApps()
    } yield new IterableDockApps(iter)).resolve[PersistenceServiceException]

  def findDockAppById(dockAppId: Int) =
    (for {
      maybeDockApp <- dockAppRepository.findDockAppById(dockAppId)
    } yield maybeDockApp map toDockApp).resolve[PersistenceServiceException]

}
