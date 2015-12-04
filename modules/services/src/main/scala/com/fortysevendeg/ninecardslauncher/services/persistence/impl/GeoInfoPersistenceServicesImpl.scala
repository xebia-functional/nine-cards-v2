package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

trait GeoInfoPersistenceServicesImpl {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addGeoInfo(request: AddGeoInfoRequest) =
    (for {
      geoInfo <- geoInfoRepository.addGeoInfo(toRepositoryGeoInfoData(request))
    } yield toGeoInfo(geoInfo)).resolve[PersistenceServiceException]

  def deleteAllGeoInfoItems() =
    (for {
      deleted <- geoInfoRepository.deleteGeoInfoItems()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteGeoInfo(request: DeleteGeoInfoRequest) =
    (for {
      deleted <- geoInfoRepository.deleteGeoInfo(toRepositoryGeoInfo(request.geoInfo))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest) =
    (for {
      maybeGeoInfo <- geoInfoRepository.fetchGeoInfoByConstrain(request.constrain)
    } yield maybeGeoInfo map toGeoInfo).resolve[PersistenceServiceException]

  def fetchGeoInfoItems =
    (for {
      geoInfoItems <- geoInfoRepository.fetchGeoInfoItems
    } yield geoInfoItems map toGeoInfo).resolve[PersistenceServiceException]

  def findGeoInfoById(request: FindGeoInfoByIdRequest) =
    (for {
      maybeGeoInfo <- geoInfoRepository.findGeoInfoById(request.id)
    } yield maybeGeoInfo map toGeoInfo).resolve[PersistenceServiceException]

  def updateGeoInfo(request: UpdateGeoInfoRequest) =
    (for {
      updated <- geoInfoRepository.updateGeoInfo(toRepositoryGeoInfo(request))
    } yield updated).resolve[PersistenceServiceException]

}
