package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.services.persistence.models._

case class AddAppRequest(
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  colorPrimary: String,
  dateInstalled: Double,
  dateUpdate: Double,
  version: String,
  installedFromGooglePlay: Boolean)

case class UpdateAppRequest(
  id: Int,
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  colorPrimary: String,
  dateInstalled: Double,
  dateUpdate: Double,
  version: String,
  installedFromGooglePlay: Boolean)

case class AddCacheCategoryRequest(
  packageName: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int)

case class DeleteCacheCategoryRequest(cacheCategory: CacheCategory)

case class DeleteCacheCategoryResponse(deleted: Int)

case class DeleteCacheCategoryByPackageRequest(packageName: String)

case class DeleteCacheCategoryByPackageResponse(deleted: Int)

case class FindCacheCategoryByIdRequest(id: Int)

case class FindCacheCategoryByIdResponse(category: Option[CacheCategory])

case class FetchCacheCategoryByPackageRequest(packageName: String)

case class FetchCacheCategoryByPackageResponse(category: Option[CacheCategory])

case class UpdateCacheCategoryRequest(
  id: Int, packageName: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int)

case class UpdateCacheCategoryResponse(updated: Int)

case class AddCardRequest(
  collectionId: Option[Int] = None,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class AddCardResponse(card: Card)

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(deleted: Int)

case class FindCardByIdRequest(id: Int)

case class FindCardByIdResponse(card: Option[Card])

case class FetchCardsByCollectionRequest(collectionId: Int)

case class FetchCardsByCollectionResponse(cards: Seq[Card])

case class UpdateCardRequest(
  id: Int,
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class UpdateCardResponse(updated: Int)

case class CardItem(
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class AddCollectionRequest(
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[AddCardRequest])

case class AddCollectionResponse(success: Boolean)

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(deleted: Int)

case class FetchCollectionsResponse(collections: Seq[Collection])

case class FetchCollectionByPositionRequest(position: Int)

case class FetchCollectionByPositionResponse(collection: Option[Collection])

case class FetchCollectionBySharedCollectionRequest(sharedCollectionId: String)

case class FetchCollectionBySharedCollectionResponse(collection: Option[Collection])

case class FindCollectionByIdRequest(id: Int)

case class FindCollectionByIdResponse(collection: Option[Collection])

case class UpdateCollectionRequest(
  id: Int,
  position: Int,
  name: String,
  collectionType: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean],
  cards: Seq[Card])

case class UpdateCollectionResponse(updated: Int)

case class AddGeoInfoRequest(
  constrain: String,
  occurrence: String,
  wifi: String,
  latitude: Double,
  longitude: Double,
  system: Boolean)

case class AddGeoInfoResponse(geoInfo: GeoInfo)

case class DeleteGeoInfoRequest(geoInfo: GeoInfo)

case class DeleteGeoInfoResponse(deleted: Int)

case class FetchGeoInfoItemsResponse(geoInfoItems: Seq[GeoInfo])

case class FindGeoInfoByIdRequest(id: Int)

case class FindGeoInfoByIdResponse(geoInfo: Option[GeoInfo])

case class FetchGeoInfoByConstrainRequest(constrain: String)

case class FetchGeoInfoByConstrainResponse(geoInfo: Option[GeoInfo])

case class UpdateGeoInfoRequest(
  id: Int,
  constrain: String,
  occurrence: String,
  wifi: String,
  latitude: Double,
  longitude: Double,
  system: Boolean)

case class UpdateGeoInfoResponse(updated: Int)