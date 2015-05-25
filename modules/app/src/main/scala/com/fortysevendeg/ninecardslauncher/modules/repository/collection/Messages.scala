package com.fortysevendeg.ninecardslauncher.modules.repository.collection

import com.fortysevendeg.ninecardslauncher.models.Collection
import com.fortysevendeg.ninecardslauncher.modules.repository.card.CardItem

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
    cards: Seq[CardItem])

case class AddCollectionResponse(
    success: Boolean)

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(deleted: Int)

case class FetchCollectionsRequest()

case class FetchCollectionsResponse(collections: Seq[Collection])

case class FetchCollectionByPositionRequest(position: Int)

case class FetchCollectionByPositionResponse(collection: Option[Collection])

case class FetchCollectionBySharedCollectionRequest(sharedCollectionId: Int)

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
    cards: Seq[CardItem])

case class UpdateCollectionResponse(updated: Int)