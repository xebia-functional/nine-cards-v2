package com.fortysevendeg.ninecardslauncher.modules.repository.card

import com.fortysevendeg.ninecardslauncher.models.Card

case class AddCardRequest(
    collectionId: Int,
    cardItem: CardItem)

case class AddCardResponse(card: Card)

case class DeleteCardRequest(card: Card)

case class DeleteCardResponse(deleted: Int)

case class FindCardByIdRequest(id: Int)

case class FindCardByIdResponse(card: Option[Card])

case class FetchCardsByCollectionRequest(collectionId: Int)

case class FetchCardsByCollectionResponse(cards: Seq[Card])

case class UpdateCardRequest(
    id: Int,
    position: Int,
    micros: Int = 0,
    term: String,
    packageName: Option[String],
    cardType: String,
    intent: String,
    imagePath: String,
    starRating: Option[Double] = None,
    numDownloads: Option[String] = None,
    notification: Option[String] = None
    )

case class UpdateCardResponse(updated: Int)

case class CardItem(
    position: Int,
    micros: Int = 0,
    term: String,
    packageName: Option[String],
    cardType: String,
    intent: String,
    imagePath: String,
    starRating: Option[Double] = None,
    numDownloads: Option[String] = None,
    notification: Option[String] = None)
