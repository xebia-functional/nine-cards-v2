package com.fortysevendeg.ninecardslauncher.repository

import com.fortysevendeg.ninecardslauncher.repository.model.{Card, Collection}

case class AddCardRequest(collectionId: Int, data: Card)

case class AddCardResponse(card: Option[Card])

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(success: Boolean)

case class GetCardByIdRequest(id: Int)

case class GetCardByIdResponse(result: Option[Card])

case class GetCardByCollectionRequest(collectionId: Int)

case class GetCardByCollectionResponse(result: Seq[Card])

case class UpdateCardRequest(card: Card)

case class UpdateCardResponse(success: Boolean)

case class AddCollectionRequest(data: Collection)

case class AddCollectionResponse(collection: Option[Collection])

case class DeleteCollectionRequest(collection: Collection)

case class DeleteCollectionResponse(success: Boolean)

case class GetCollectionByIdRequest(id: Int)

case class GetCollectionByIdResponse(result: Option[Collection])

case class GetCollectionByPositionRequest(position: Int)

case class GetCollectionByPositionResponse(result: Option[Collection])

case class GetCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId: Int)

case class GetCollectionByOriginalSharedCollectionIdResponse(result: Option[Collection])

case class UpdateCollectionRequest(collection: Collection)

case class UpdateCollectionResponse(success: Boolean)