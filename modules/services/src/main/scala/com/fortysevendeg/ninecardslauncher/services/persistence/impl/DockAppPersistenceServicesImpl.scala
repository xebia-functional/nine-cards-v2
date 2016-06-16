package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableDockApps

trait DockAppPersistenceServicesImpl {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def createOrUpdateDockApp(requests: Seq[CreateOrUpdateDockAppRequest]) =
    (for {
      dockApps <- dockAppRepository.fetchDockApps(where = s"${DockAppEntity.position} IN (${requests.map(_.position).mkString("\"", ",", "\"")})")
      items = requests map { request =>
        dockApps.find(_.data.position == request.position) map { dockApp =>
          (request, Some(dockApp.id))
        } getOrElse {
          (request, None)
        }
      }
      toAdd = items.filter(_._2.isEmpty)
      toUpdate = items.filter(_._2.isDefined)
      _ <- dockAppRepository.addDockApps(toAdd.map(req => toRepositoryDockAppData(req._1)))
      _ <- dockAppRepository.updateDockApps(toUpdate.flatMap(req => req._2 map (id => toRepositoryDockApp(id, req._1))))
    } yield ()).resolve[PersistenceServiceException]

  def deleteAllDockApps() =
    (for {
      deleted <- dockAppRepository.deleteDockApps()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteDockApp(request: DeleteDockAppRequest) =
    (for {
      deleted <- dockAppRepository.deleteDockApp(toRepositoryDockApp(request.dockApp))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchDockApps =
    (for {
      dockAppItems <- dockAppRepository.fetchDockApps()
    } yield dockAppItems map toDockApp).resolve[PersistenceServiceException]

  def fetchIterableDockApps =
    (for {
      iter <- dockAppRepository.fetchIterableDockApps()
    } yield new IterableDockApps(iter)).resolve[PersistenceServiceException]

  def findDockAppById(request: FindDockAppByIdRequest) =
    (for {
      maybeDockApp <- dockAppRepository.findDockAppById(request.id)
    } yield maybeDockApp map toDockApp).resolve[PersistenceServiceException]
  
}
