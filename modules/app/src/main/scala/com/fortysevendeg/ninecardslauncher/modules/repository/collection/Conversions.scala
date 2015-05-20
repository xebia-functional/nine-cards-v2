package com.fortysevendeg.ninecardslauncher.modules.repository.collection

import com.fortysevendeg.ninecardslauncher.models.Collection
import com.fortysevendeg.ninecardslauncher.repository.{AddCollectionRequest => RepositoryAddCollectionRequest, DeleteCollectionRequest => RepositoryDeleteCollectionRequest, FetchCollectionByOriginalSharedCollectionIdRequest => RepositoryFetchCollectionByOriginalSharedCollectionRequest, FetchCollectionByPositionRequest => RepositoryFetchCollectionByPositionRequest, FindCollectionByIdRequest => RepositoryFindCollectionByIdRequest, UpdateCollectionRequest => RepositoryUpdateCollectionRequest, DeleteCacheCategoryRequest}
import com.fortysevendeg.ninecardslauncher.repository.model.{Collection => RepositoryCollection}
import com.fortysevendeg.ninecardslauncher.repository.model.{CollectionData => RepositoryCollectionData}

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

  def toRepositoryCollection(collection: Collection) =
    RepositoryCollection(
      id = collection.id,
      data = RepositoryCollectionData(
        position = collection.position,
        name = collection.name,
        `type` = collection.`type`,
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
    RepositoryAddCollectionRequest(
      data = RepositoryCollectionData(
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

  def toRepositoryDeleteCollectionRequest(request: DeleteCollectionRequest) =
    RepositoryDeleteCollectionRequest(
      collection = toRepositoryCollection(request.collection)
    )

  def toRepositoryFetchCollectionByOriginalSharedCollectionRequest(request: FetchCollectionByOriginalSharedCollectionRequest) =
    RepositoryFetchCollectionByOriginalSharedCollectionRequest(sharedCollectionId = request.sharedCollectionId)

  def toRepositoryFetchCollectionByPositionRequest(request: FetchCollectionByPositionRequest) =
    RepositoryFetchCollectionByPositionRequest(position = request.position)

  def toRepositoryFindCollectionByIdRequest(request: FindCollectionByIdRequest) =
    RepositoryFindCollectionByIdRequest(id = request.id)

  def toRepositoryUpdateCollectionRequest(request: UpdateCollectionRequest) =
    RepositoryUpdateCollectionRequest(
      RepositoryCollection(
        id = request.id,
        data = RepositoryCollectionData(
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
    )
}
