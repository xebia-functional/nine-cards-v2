package com.fortysevendeg.ninecardslauncher.services.persistence

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

}
