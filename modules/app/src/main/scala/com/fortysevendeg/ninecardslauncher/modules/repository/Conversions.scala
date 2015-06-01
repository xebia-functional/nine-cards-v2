package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.services.api.models.{NineCardIntent, CacheCategory, Card, Collection}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection => RepositoryCollection, Card => RepositoryCard, CacheCategory => RepositoryCacheCategory, _}
import play.api.libs.json._

trait Conversions {

  def toCollectionSeq(collections: Seq[RepositoryCollection]) = collections map toCollection

  def toCollection(collection: RepositoryCollection) =
    Collection(
      id = collection.id,
      position = collection.data.position,
      name = collection.data.name,
      `type` = collection.data.`type`,
      icon = collection.data.icon,
      themedColorIndex = collection.data.themedColorIndex,
      appsCategory = collection.data.appsCategory,
      constrains = collection.data.constrains,
      originalSharedCollectionId = collection.data.originalSharedCollectionId,
      sharedCollectionId = collection.data.sharedCollectionId,
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed getOrElse false
    )

  def toCard(card: RepositoryCard) = {
    import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntentImplicits._
    val intent = Json.parse(card.data.intent).as[NineCardIntent]
    Card(
      id = card.id,
      position = card.data.position,
      micros = card.data.micros,
      term = card.data.term,
      packageName = card.data.packageName,
      `type` = card.data.`type`,
      intent = intent,
      imagePath = card.data.imagePath,
      starRating = card.data.starRating,
      numDownloads = card.data.numDownloads,
      notification = card.data.notification)
  }

  def toCacheCategorySeq(cache: Seq[RepositoryCacheCategory]) = cache map toCacheCategory

  def toCacheCategory(cacheCategory: RepositoryCacheCategory) =
    CacheCategory(
      id = cacheCategory.id,
      packageName = cacheCategory.data.packageName,
      category = cacheCategory.data.category,
      starRating = cacheCategory.data.starRating,
      numDownloads = cacheCategory.data.numDownloads,
      ratingsCount = cacheCategory.data.ratingsCount,
      commentCount = cacheCategory.data.commentCount)

  def toAddCacheCategoryRequest(request: InsertCacheCategoryRequest) =
    AddCacheCategoryRequest(
      data = CacheCategoryData(
        packageName = request.packageName,
        category = request.category,
        starRating = request.starRating,
        numDownloads = request.numDownloads,
        ratingsCount = request.ratingsCount,
        commentCount = request.commentCount
      )
    )

  def toAddGeoInfoRequest(request: InsertGeoInfoRequest) =
    AddGeoInfoRequest(
      data = GeoInfoData(
        constrain = request.constrain,
        occurrence = request.occurrence,
        wifi = request.wifi,
        latitude = request.latitude,
        longitude = request.longitude,
        system = request.system
      )
    )

  def toAddCollectionRequest(request: InsertCollectionRequest) =
    AddCollectionRequest(
      data = CollectionData(
        position = request.position,
        name = request.name,
        `type` = request.`type`,
        icon = request.icon,
        themedColorIndex = request.themedColorIndex,
        appsCategory = request.appsCategory,
        constrains = request.constrains,
        originalSharedCollectionId = request.originalSharedCollectionId,
        sharedCollectionId = request.sharedCollectionId,
        sharedCollectionSubscribed = request.sharedCollectionSubscribed
      )
    )

  def toAddCardRequest(collectionId: Int, request: CardItem) =
    AddCardRequest(
      collectionId = collectionId,
      data = CardData(
        position = request.position,
        term = request.term,
        `type` = request.`type`,
        micros = request.micros,
        packageName = request.packageName,
        intent = request.intent,
        imagePath = request.imagePath,
        starRating = request.starRating,
        numDownloads = request.numDownloads,
        notification = request.notification
      )
    )
}
