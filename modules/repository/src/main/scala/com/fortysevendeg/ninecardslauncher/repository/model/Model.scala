package com.fortysevendeg.ninecardslauncher.repository.model

case class CacheCategory(
  id: Int,
  data: CacheCategoryData)

case class CacheCategoryData(
  packageName: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int)

case class Collection(
  id: Int,
  data: CollectionData)

case class CollectionData(
  position: Int,
  name: String,
  `type`: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[String] = None,
  constrains: Option[String] = None,
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Boolean)

case class Card(
  id: Int,
  data: CardData)

case class CardData(
  position: Int,
  micros: Int = 0,
  term: String,
  packageName: Option[String],
  `type`: String,
  intent: String,
  imagePath: String,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  notification: Option[String] = None)

case class GeoInfo(
  id: Int,
  data: GeoInfoData)

case class GeoInfoData(
  constrain: String,
  occurrence: String,
  wifi: String,
  latitude: Double,
  longitude: Double,
  system: Boolean)
