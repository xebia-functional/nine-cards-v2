package com.fortysevendeg.ninecardslauncher.modules.repository

import com.fortysevendeg.ninecardslauncher.repository.model.{Collection => RepositoryCollection}
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard}

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
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed,
      cards = collection.data.cards map toCard
    )

  def toCard(card: RepositoryCard) =
    Card(
      id = card.id,
      position = card.data.position,
      micros = card.data.micros,
      term = card.data.term,
      packageName = card.data.packageName,
      `type` = card.data.`type`,
      intent = card.data.intent,
      imagePath = card.data.imagePath,
      starRating = card.data.starRating,
      numDownloads = card.data.numDownloads,
      notification = card.data.notification)

}
