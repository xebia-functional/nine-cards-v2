package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Collection => RepoCollection, CollectionData => RepoCollectionData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import com.fortysevendeg.ninecardslauncher.{repository => repo}

trait CollectionConversions {

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

  def toRepositoryAddCollectionRequest(request: AddCollectionRequest) =
    repo.AddCollectionRequest(
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

  def toRepositoryDeleteCollectionRequest(request: DeleteCollectionRequest) =
    repo.DeleteCollectionRequest(
      collection = toRepositoryCollection(request.collection)
    )

  def toRepositoryFetchCollectionBySharedCollectionRequest(request: FetchCollectionBySharedCollectionRequest) =
    repo.FetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = request.sharedCollectionId)

  def toRepositoryFetchCollectionByPositionRequest(request: FetchCollectionByPositionRequest) =
    repo.FetchCollectionByPositionRequest(position = request.position)

  def toRepositoryFindCollectionByIdRequest(request: FindCollectionByIdRequest) =
    repo.FindCollectionByIdRequest(id = request.id)

  def toRepositoryUpdateCollectionRequest(request: UpdateCollectionRequest) =
    repo.UpdateCollectionRequest(
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
    )

}
