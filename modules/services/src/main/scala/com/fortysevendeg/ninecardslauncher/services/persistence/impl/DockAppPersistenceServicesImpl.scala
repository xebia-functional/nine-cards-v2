package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

trait DockAppPersistenceServicesImpl {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addDockApp(request: AddDockAppRequest) =
    (for {
      dockApp <- dockAppRepository.addDockApp(toRepositoryDockAppData(request))
    } yield toDockApp(dockApp)).resolve[PersistenceServiceException]

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
      dockAppItems <- dockAppRepository.fetchDockApps
    } yield dockAppItems map toDockApp).resolve[PersistenceServiceException]

  def findDockAppById(request: FindDockAppByIdRequest) =
    (for {
      maybeDockApp <- dockAppRepository.findDockAppById(request.id)
    } yield maybeDockApp map toDockApp).resolve[PersistenceServiceException]

  def updateDockApp(request: UpdateDockAppRequest) =
    (for {
      updated <- dockAppRepository.updateDockApp(toRepositoryDockApp(request))
    } yield updated).resolve[PersistenceServiceException]
  
}
