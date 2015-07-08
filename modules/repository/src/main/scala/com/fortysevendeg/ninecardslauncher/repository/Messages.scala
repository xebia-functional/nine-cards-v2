package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.model._

case class DeleteCacheCategoryRequest(cacheCategory: CacheCategory)

case class DeleteCacheCategoryResponse(deleted: Int)

case class DeleteCacheCategoryByPackageRequest(packageName: String)

case class DeleteCacheCategoryByPackageResponse(deleted: Int)

case class FindCacheCategoryByIdRequest(id: Int)

case class FindCacheCategoryByIdResponse(cacheCategory: Option[CacheCategory])

case class FetchCacheCategoryByPackageRequest(`package`: String)

case class FetchCacheCategoryByPackageResponse(cacheCategory: Option[CacheCategory])

case class UpdateCacheCategoryRequest(cacheCategory: CacheCategory)

case class UpdateCacheCategoryResponse(updated: Int)

case class AddCardRequest(collectionId: Int, data: CardData)

case class AddCardResponse(card: Card)

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(deleted: Int)

case class FindCardByIdRequest(id: Int)

case class FindCardByIdResponse(card: Option[Card])

case class FetchCardsByCollectionRequest(collectionId: Int)

case class FetchCardsByCollectionResponse(cards: Seq[Card])

case class UpdateCardRequest(card: Card)

case class UpdateCardResponse(updated: Int)

case class AddCollectionRequest(data: CollectionData)

case class AddCollectionResponse(collection: Collection)

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(deleted: Int)

case class FindCollectionByIdRequest(id: Int)

case class FindCollectionByIdResponse(collection: Option[Collection])

case class FetchCollectionByPositionRequest(position: Int)

case class FetchCollectionByPositionResponse(collection: Option[Collection])

case class FetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int)

case class FetchCollectionByOriginalSharedCollectionIdResponse(collection: Option[Collection])

case class FetchSortedCollectionsRequest()

case class FetchSortedCollectionsResponse(collections: Seq[Collection])

case class UpdateCollectionRequest(collection: Collection)

case class UpdateCollectionResponse(updated: Int)

case class AddGeoInfoRequest(data: GeoInfoData)

case class AddGeoInfoResponse(geoInfo: GeoInfo)

case class DeleteGeoInfoRequest(geoInfo: GeoInfo)

case class DeleteGeoInfoResponse(deleted: Int)

case class FetchGeoInfoItemsRequest()

case class FetchGeoInfoItemsResponse(geoInfoItems: Seq[GeoInfo])

case class FindGeoInfoByIdRequest(id: Int)

case class FindGeoInfoByIdResponse(geoInfo: Option[GeoInfo])

case class FetchGeoInfoByConstrainRequest(constrain: String)

case class FetchGeoInfoByConstrainResponse(geoInfo: Option[GeoInfo])

case class UpdateGeoInfoRequest(geoInfo: GeoInfo)

case class UpdateGeoInfoResponse(updated: Int)