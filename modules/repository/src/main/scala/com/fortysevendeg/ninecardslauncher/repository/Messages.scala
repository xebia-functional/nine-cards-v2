package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.model._


case class AddCacheCategoryRequest(data: CacheCategoryData)

case class AddCacheCategoryResponse(cacheCategory: Option[CacheCategory])

case class DeleteCacheCategoryRequest(cacheCategory: CacheCategory)

case class DeleteCacheCategoryResponse(success: Boolean)

case class DeleteCacheCategoryByPackageRequest(`package`: String)

case class DeleteCacheCategoryByPackageResponse(success: Boolean)

case class GetAllCacheCategoriesRequest()

case class GetAllCacheCategoriesResponse(cacheCategories: Seq[CacheCategory])

case class GetCacheCategoryByIdRequest(id: Int)

case class GetCacheCategoryByIdResponse(result: Option[CacheCategory])

case class GetCacheCategoryByPackageRequest(`package`: String)

case class GetCacheCategoryByPackageResponse(result: Option[CacheCategory])

case class UpdateCacheCategoryRequest(cacheCategory: CacheCategory)

case class UpdateCacheCategoryResponse(success: Boolean)

case class AddCardRequest(collectionId: Int, data: CardData)

case class AddCardResponse(card: Option[Card])

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(success: Boolean)

case class GetCardByIdRequest(id: Int)

case class GetCardByIdResponse(result: Option[Card])

case class GetAllCardsByCollectionRequest(collectionId: Int)

case class GetAllCardsByCollectionResponse(result: Seq[Card])

case class UpdateCardRequest(card: Card)

case class UpdateCardResponse(success: Boolean)

case class AddCollectionRequest(data: CollectionData)

case class AddCollectionResponse(collection: Option[Collection])

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(success: Boolean)

case class GetCollectionByIdRequest(id: Int)

case class GetCollectionByIdResponse(result: Option[Collection])

case class GetCollectionByPositionRequest(position: Int)

case class GetCollectionByPositionResponse(result: Option[Collection])

case class GetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int)

case class GetCollectionByOriginalSharedCollectionIdResponse(result: Option[Collection])

case class GetSortedCollectionsRequest()

case class GetSortedCollectionsResponse(collections: Seq[Collection])

case class UpdateCollectionRequest(collection: Collection)

case class UpdateCollectionResponse(success: Boolean)

case class AddGeoInfoRequest(data: GeoInfoData)

case class AddGeoInfoResponse(geoInfo: Option[GeoInfo])

case class DeleteGeoInfoRequest(geoInfo: GeoInfo)

case class DeleteGeoInfoResponse(success: Boolean)

case class GetAllGeoInfoItemsRequest()

case class GetAllGeoInfoItemsResponse(geoInfoItems: Seq[GeoInfo])

case class GetGeoInfoByIdRequest(id: Int)

case class GetGeoInfoByIdResponse(result: Option[GeoInfo])

case class GetGeoInfoByConstrainRequest(constrain: String)

case class GetGeoInfoByConstrainResponse(result: Option[GeoInfo])

case class UpdateGeoInfoRequest(geoInfo: GeoInfo)

case class UpdateGeoInfoResponse(success: Boolean)