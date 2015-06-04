package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User}
import macroid.ContextWrapper

import scala.concurrent.{ExecutionContext, Future}

trait PersistenceServices {

  def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse]

  def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse]

  def deleteCacheCategoryByPackage: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse]

  def fetchCacheCategoryByPackage: Service[FetchCacheCategoryByPackageRequest, FetchCacheCategoryByPackageResponse]

  def fetchCacheCategories: Service[FetchCacheCategoriesRequest, FetchCacheCategoriesResponse]

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

  def getUser()(implicit context: ContextWrapper): Future[User]

  def saveUser(user: User)(implicit context: ContextWrapper): Future[Unit]

  def resetUser()(implicit context: ContextWrapper): Future[Boolean]

  def getAndroidId()(implicit context: ContextWrapper): Future[String]

  def getInstallation()(implicit context: ContextWrapper): Future[Installation]

  def saveInstallation(installation: Installation)(implicit context: ContextWrapper): Future[Boolean]

}
