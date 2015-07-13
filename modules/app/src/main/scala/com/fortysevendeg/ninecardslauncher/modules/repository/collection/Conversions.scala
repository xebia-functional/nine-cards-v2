package com.fortysevendeg.ninecardslauncher.modules.repository.collection

import com.fortysevendeg.ninecardslauncher.models.Collection
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection => RepositoryCollection, CollectionData => RepositoryCollectionData}
import com.fortysevendeg.ninecardslauncher.{repository => repo}

trait Conversions {

  def toCollectionSeq(collections: Seq[RepositoryCollection]) = collections map toCollection

  def toCollection(collection: RepositoryCollection) =
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
    RepositoryCollection(
      id = collection.id,
      data = RepositoryCollectionData(
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
      data = RepositoryCollectionData(
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
      RepositoryCollection(
        id = request.id,
        data = RepositoryCollectionData(
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
