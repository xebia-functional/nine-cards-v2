package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory

import scala.concurrent.Future
import scalaz.\/
import scalaz.concurrent.Task

trait PersistenceServices {

  def addCacheCategory(request: AddCacheCategoryRequest): Task[NineCardsException \/ CacheCategory]

  def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse]

  def deleteCacheCategoryByPackage: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse]

  def fetchCacheCategoryByPackage: Service[FetchCacheCategoryByPackageRequest, FetchCacheCategoryByPackageResponse]

  def fetchCacheCategories: Task[NineCardsException \/ Seq[CacheCategory]]

  def findCacheCategoryById: Service[FindCacheCategoryByIdRequest, FindCacheCategoryByIdResponse]

  def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse]

  def addCard: Service[AddCardRequest, AddCardResponse]

  def deleteCard: Service[DeleteCardRequest, DeleteCardResponse]

  def fetchCardsByCollection: Service[FetchCardsByCollectionRequest, FetchCardsByCollectionResponse]

  def findCardById: Service[FindCardByIdRequest, FindCardByIdResponse]

  def updateCard: Service[UpdateCardRequest, UpdateCardResponse]

  def addCollection: Service[AddCollectionRequest, AddCollectionResponse]

  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse]

  def fetchCollections: Service[FetchCollectionsRequest, FetchCollectionsResponse]

  def fetchCollectionBySharedCollection: Service[FetchCollectionBySharedCollectionRequest, FetchCollectionBySharedCollectionResponse]

  def fetchCollectionByPosition: Service[FetchCollectionByPositionRequest, FetchCollectionByPositionResponse]

  def findCollectionById: Service[FindCollectionByIdRequest, FindCollectionByIdResponse]

  def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse]

  def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse]

  def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse]

  def fetchGeoInfoByConstrain: Service[FetchGeoInfoByConstrainRequest, FetchGeoInfoByConstrainResponse]

  def fetchGeoInfoItems: Service[FetchGeoInfoItemsRequest, FetchGeoInfoItemsResponse]

  def findGeoInfoById: Service[FindGeoInfoByIdRequest, FindGeoInfoByIdResponse]

  def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse]

  def getUser(implicit context: ContextSupport): Task[NineCardsException \/ User]

  def saveUser(user: User)(implicit context: ContextSupport): Future[Unit]

  def resetUser()(implicit context: ContextSupport): Future[Boolean]

  def getAndroidId(implicit context: ContextSupport): Task[NineCardsException \/ String]

  def getInstallation()(implicit context: ContextSupport): Future[Installation]

  def saveInstallation(installation: Installation)(implicit context: ContextSupport): Future[Boolean]

}
