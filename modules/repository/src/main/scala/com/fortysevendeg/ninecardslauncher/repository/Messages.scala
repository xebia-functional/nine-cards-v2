package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.provider.{CollectionEntity, CollectionEntityData}

case class AddCollectionRequest(data: CollectionEntityData)

case class AddCollectionResponse(success: Boolean, collectionEntity: Option[CollectionEntity])

case class DeleteCollectionRequest(entity: CollectionEntity)

case class DeleteCollectionResponse(success: Boolean)

case class GetCollectionByIdRequest(id: Int)

case class GetCollectionByIdResponse(result: Option[CollectionEntity])

case class GetCollectionByPositionRequest(position: Int)

case class GetCollectionByPositionResponse(result: Seq[CollectionEntity])

case class GetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int)

case class GetCollectionByOriginalSharedCollectionIdResponse(result: Seq[CollectionEntity])

case class UpdateCollectionRequest(entity: CollectionEntity)

case class UpdateCollectionResponse(success: Boolean)