package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.model._


case class AddCacheCategoryRequest(data: CacheCategoryData)

case class AddCacheCategoryResponse(cacheCategory: Option[CacheCategory])

case class DeleteCacheCategoryRequest(cacheCategory: CacheCategory)

case class DeleteCacheCategoryResponse(deleted: Int)

case class DeleteCacheCategoryByPackageRequest(`package`: String)

case class DeleteCacheCategoryByPackageResponse(deleted: Int)

case class GetAllCacheCategoriesRequest()

case class GetAllCacheCategoriesResponse(cacheCategories: Seq[CacheCategory])

case class GetCacheCategoryByIdRequest(id: Int)

case class GetCacheCategoryByIdResponse(result: Option[CacheCategory])

case class GetCacheCategoryByPackageRequest(`package`: String)

case class GetCacheCategoryByPackageResponse(result: Option[CacheCategory])

case class UpdateCacheCategoryRequest(cacheCategory: CacheCategory)

case class UpdateCacheCategoryResponse(updated: Int)

case class AddCardRequest(collectionId: Int, data: CardData)

case class AddCardResponse(card: Option[Card])

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(deleted: Int)

case class GetCardByIdRequest(id: Int)

case class GetCardByIdResponse(result: Option[Card])

case class GetAllCardsByCollectionRequest(collectionId: Int)

case class GetAllCardsByCollectionResponse(result: Seq[Card])

case class UpdateCardRequest(card: Card)

case class UpdateCardResponse(updated: Int)

case class AddCollectionRequest(data: CollectionData)

case class AddCollectionResponse(collection: Option[Collection])

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(deleted: Int)

case class GetCollectionByIdRequest(id: Int)

case class GetCollectionByIdResponse(result: Option[Collection])

case class GetCollectionByPositionRequest(position: Int)

case class GetCollectionByPositionResponse(result: Option[Collection])

case class GetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int)

case class GetCollectionByOriginalSharedCollectionIdResponse(result: Option[Collection])

case class GetSortedCollectionsRequest()

case class GetSortedCollectionsResponse(collections: Seq[Collection])

case class UpdateCollectionRequest(collection: Collection)

case class UpdateCollectionResponse(updated: Int)

case class AddGeoInfoRequest(data: GeoInfoData)

case class AddGeoInfoResponse(geoInfo: Option[GeoInfo])

case class DeleteGeoInfoRequest(geoInfo: GeoInfo)

case class DeleteGeoInfoResponse(deleted: Int)

case class GetAllGeoInfoItemsRequest()

case class GetAllGeoInfoItemsResponse(geoInfoItems: Seq[GeoInfo])

case class GetGeoInfoByIdRequest(id: Int)

case class GetGeoInfoByIdResponse(result: Option[GeoInfo])

case class GetGeoInfoByConstrainRequest(constrain: String)

case class GetGeoInfoByConstrainResponse(result: Option[GeoInfo])

case class UpdateGeoInfoRequest(geoInfo: GeoInfo)

case class UpdateGeoInfoResponse(updated: Int)