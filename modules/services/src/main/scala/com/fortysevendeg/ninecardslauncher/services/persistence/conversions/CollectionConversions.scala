package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepoCard, Collection => RepoCollection, CollectionData => RepoCollectionData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection

trait CollectionConversions extends CardConversions {

  def toCollectionSeq(collections: Seq[RepoCollection]) = collections map toCollection

  def toCollection(collection: RepoCollection) =
    Collection(
      id = collection.id,
      position = collection.data.position,
      name = collection.data.name,
      collectionType = collection.data.collectionType,
      icon = collection.data.icon,
      themedColorIndex = collection.data.themedColorIndex,
      appsCategory = collection.data.appsCategory,
      constrains = collection.data.constrains,
      originalSharedCollectionId = collection.data.originalSharedCollectionId,
      sharedCollectionId = collection.data.sharedCollectionId,
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed getOrElse false
    )

  def toCollection(collection: RepoCollection, cards: Seq[RepoCard]) =
    Collection(
      id = collection.id,
      position = collection.data.position,
      name = collection.data.name,
      collectionType = collection.data.collectionType,
      icon = collection.data.icon,
      themedColorIndex = collection.data.themedColorIndex,
      appsCategory = collection.data.appsCategory,
      constrains = collection.data.constrains,
      originalSharedCollectionId = collection.data.originalSharedCollectionId,
      sharedCollectionId = collection.data.sharedCollectionId,
      sharedCollectionSubscribed = collection.data.sharedCollectionSubscribed getOrElse false,
      cards = cards map toCard
    )

  def toRepositoryCollection(collection: Collection) =
    RepoCollection(
      id = collection.id,
      data = RepoCollectionData(
        position = collection.position,
        name = collection.name,
        collectionType = collection.collectionType,
        icon = collection.icon,
        themedColorIndex = collection.themedColorIndex,
        appsCategory = collection.appsCategory,
        constrains = collection.constrains,
        originalSharedCollectionId = collection.originalSharedCollectionId,
        sharedCollectionId = collection.sharedCollectionId,
        sharedCollectionSubscribed = Option(collection.sharedCollectionSubscribed)
      )
    )

  def toRepositoryCollection(request: UpdateCollectionRequest) =
    RepoCollection(
      id = request.id,
      data = RepoCollectionData(
        position = request.position,
        name = request.name,
        collectionType = request.collectionType,
        icon = request.icon,
        themedColorIndex = request.themedColorIndex,
        appsCategory = request.appsCategory,
        constrains = request.constrains,
        originalSharedCollectionId = request.originalSharedCollectionId,
        sharedCollectionId = request.sharedCollectionId,
        sharedCollectionSubscribed = request.sharedCollectionSubscribed
      )
    )

  def toRepositoryCollectionData(request: AddCollectionRequest) =
    RepoCollectionData(
      position = request.position,
      name = request.name,
      collectionType = request.collectionType,
      icon = request.icon,
      themedColorIndex = request.themedColorIndex,
      appsCategory = request.appsCategory,
      constrains = request.constrains,
      originalSharedCollectionId = request.originalSharedCollectionId,
      sharedCollectionId = request.sharedCollectionId,
      sharedCollectionSubscribed = request.sharedCollectionSubscribed
    )
}
