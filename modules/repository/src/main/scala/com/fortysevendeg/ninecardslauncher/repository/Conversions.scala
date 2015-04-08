package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.provider.{CacheCategoryEntity, GeoInfoEntity, CardEntity, CollectionEntity}
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, GeoInfo, Card, Collection}

object Conversions {

  def toCacheCategory(cacheCategory: CacheCategoryEntity) = CacheCategory(
    id = cacheCategory.id,
    packageName = cacheCategory.data.packageName,
    category = cacheCategory.data.category,
    starRating = cacheCategory.data.starRating,
    numDownloads = cacheCategory.data.numDownloads,
    ratingsCount = cacheCategory.data.ratingsCount,
    commentCount = cacheCategory.data.commentCount)

  def toCard(cardEntity: CardEntity) = Card(
    id = cardEntity.id,
    position = cardEntity.data.position,
    micros = cardEntity.data.micros,
    term = cardEntity.data.term,
    packageName = Option[String](cardEntity.data.packageName),
    `type` = cardEntity.data.`type`,
    intent = cardEntity.data.intent,
    imagePath = cardEntity.data.imagePath,
    starRating = Option[Double](cardEntity.data.starRating),
    numDownloads = Option[String](cardEntity.data.numDownloads),
    notification = Option[String](cardEntity.data.notification))

  def toCollection(collectionEntity: CollectionEntity) = Collection(
    id = collectionEntity.id,
    position = collectionEntity.data.position,
    name = collectionEntity.data.name,
    `type` = collectionEntity.data.`type`,
    icon = collectionEntity.data.icon,
    themedColorIndex = collectionEntity.data.themedColorIndex,
    appsCategory = Option[String](collectionEntity.data.appsCategory),
    constrains = Option[String](collectionEntity.data.constrains),
    originalSharedCollectionId = Option[String](collectionEntity.data.originalSharedCollectionId),
    sharedCollectionId = Option[String](collectionEntity.data.sharedCollectionId),
    sharedCollectionSubscribed = collectionEntity.data.sharedCollectionSubscribed,
    cards = Seq.empty[Card])

  def toGeoInfo(geoInfoEntity: GeoInfoEntity) = GeoInfo(
    id = geoInfoEntity.id,
    constrain = geoInfoEntity.data.constrain,
    occurrence = geoInfoEntity.data.occurrence,
    wifi = geoInfoEntity.data.wifi,
    latitude = geoInfoEntity.data.latitude,
    longitude = geoInfoEntity.data.longitude,
    system = geoInfoEntity.data.system)
}
