package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

trait PersistenceServices {

  def getApp(packageName: String): ServiceDef2[Option[App], PersistenceServiceException]

  def addApp(request: AddAppRequest): ServiceDef2[App, PersistenceServiceException]

  def deleteApp(app: App): ServiceDef2[Int, PersistenceServiceException]

  def deleteAppByPackage(packageName: String): ServiceDef2[Int, PersistenceServiceException]

  def updateApp(request: UpdateAppRequest): ServiceDef2[Int, PersistenceServiceException]

  def addCacheCategory(request: AddCacheCategoryRequest): ServiceDef2[CacheCategory, PersistenceServiceException]

  def deleteCacheCategory(request: DeleteCacheCategoryRequest): ServiceDef2[Int, PersistenceServiceException]

  def deleteCacheCategoryByPackage(request: DeleteCacheCategoryByPackageRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchCacheCategoryByPackage(request: FetchCacheCategoryByPackageRequest): ServiceDef2[Option[CacheCategory], PersistenceServiceException]

  def fetchCacheCategories: ServiceDef2[Seq[CacheCategory], PersistenceServiceException]

  def findCacheCategoryById(request: FindCacheCategoryByIdRequest): ServiceDef2[Option[CacheCategory], PersistenceServiceException]

  def updateCacheCategory(request: UpdateCacheCategoryRequest): ServiceDef2[Int, PersistenceServiceException]

  def addCard(request: AddCardRequest): ServiceDef2[Card, PersistenceServiceException]

  def deleteCard(request: DeleteCardRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchCardsByCollection(request: FetchCardsByCollectionRequest): ServiceDef2[Seq[Card], PersistenceServiceException]

  def findCardById(request: FindCardByIdRequest): ServiceDef2[Option[Card], PersistenceServiceException]

  def updateCard(request: UpdateCardRequest): ServiceDef2[Int, PersistenceServiceException]

  def addCollection(request: AddCollectionRequest): ServiceDef2[Collection, PersistenceServiceException]

  def deleteCollection(request: DeleteCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchCollections: ServiceDef2[Seq[Collection], PersistenceServiceException]

  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def findCollectionById(request: FindCollectionByIdRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def updateCollection(request: UpdateCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  def addGeoInfo(request: AddGeoInfoRequest): ServiceDef2[GeoInfo, PersistenceServiceException]

  def deleteGeoInfo(request: DeleteGeoInfoRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest): ServiceDef2[Option[GeoInfo], PersistenceServiceException]

  def fetchGeoInfoItems: ServiceDef2[Seq[GeoInfo], PersistenceServiceException]

  def findGeoInfoById(request: FindGeoInfoByIdRequest): ServiceDef2[Option[GeoInfo], PersistenceServiceException]

  def updateGeoInfo(request: UpdateGeoInfoRequest): ServiceDef2[Int, PersistenceServiceException]

  def getUser(implicit context: ContextSupport): ServiceDef2[User, PersistenceServiceException]

  def saveUser(user: User)(implicit context: ContextSupport): ServiceDef2[Unit, PersistenceServiceException]

  def resetUser(implicit context: ContextSupport): ServiceDef2[Boolean, PersistenceServiceException]

  def getAndroidId(implicit context: ContextSupport): ServiceDef2[String, AndroidIdNotFoundException]

  def getInstallation(implicit context: ContextSupport): ServiceDef2[Installation, InstallationNotFoundException]

  def existsInstallation(implicit context: ContextSupport): ServiceDef2[Boolean, PersistenceServiceException]

  def saveInstallation(installation: Installation)(implicit context: ContextSupport): ServiceDef2[Unit, PersistenceServiceException]

}
