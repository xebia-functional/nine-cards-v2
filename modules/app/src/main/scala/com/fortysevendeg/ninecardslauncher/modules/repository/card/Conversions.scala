package com.fortysevendeg.ninecardslauncher.modules.repository.card

import com.fortysevendeg.ninecardslauncher.models.{Card, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.{repository => repo}
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard}
import com.fortysevendeg.ninecardslauncher.repository.model.{CardData => RepositoryCardData}
import play.api.libs.json._

trait Conversions {

  def toCard(card: RepositoryCard) = {
    val intent = Json.parse(card.data.intent).as[NineCardIntent]
    Card(
      id = card.id,
      position = card.data.position,
      micros = card.data.micros,
      term = card.data.term,
      packageName = card.data.packageName,
      cardType = card.data.cardType,
      intent = intent,
      imagePath = card.data.imagePath,
      starRating = card.data.starRating,
      numDownloads = card.data.numDownloads,
      notification = card.data.notification)
  }

  def toRepositoryCard(card: Card) =
    RepositoryCard(
      id = card.id,
      data = RepositoryCardData(
        position = card.position,
        micros = card.micros,
        term = card.term,
        packageName = card.packageName,
        cardType = card.cardType,
        intent = Json.toJson(card.intent).toString(),
        imagePath = card.imagePath,
        starRating = card.starRating,
        numDownloads = card.numDownloads,
        notification = card.notification
      )
    )
  
  def toRepositoryAddCardRequest(request: AddCardRequest) =
    repo.AddCardRequest(
      collectionId = request.collectionId,
      data = RepositoryCardData(
        position = request.cardItem.position,
        term = request.cardItem.term,
        cardType = request.cardItem.cardType,
        micros = request.cardItem.micros,
        packageName = request.cardItem.packageName,
        intent = request.cardItem.intent,
        imagePath = request.cardItem.imagePath,
        starRating = request.cardItem.starRating,
        numDownloads = request.cardItem.numDownloads,
        notification = request.cardItem.notification
      )
    )

  def toRepositoryDeleteCardRequest(request: DeleteCardRequest) =
    repo.DeleteCardRequest(card = toRepositoryCard(request.card))

  def toRepositoryFetchCardsByCollectionRequest(request: FetchCardsByCollectionRequest) =
    repo.FetchCardsByCollectionRequest(collectionId = request.collectionId)

  def toRepositoryFindCardByIdRequest(request: FindCardByIdRequest) =
    repo.FindCardByIdRequest(id = request.id)

  def toRepositoryUpdateCardRequest(request: UpdateCardRequest) =
    repo.UpdateCardRequest(
      RepositoryCard(
        id = request.id,
        data = RepositoryCardData(
          position = request.position,
          micros = request.micros,
          term = request.term,
          packageName = request.packageName,
          cardType = request.cardType,
          intent = request.intent,
          imagePath = request.imagePath,
          starRating = request.starRating,
          numDownloads = request.numDownloads,
          notification = request.notification
        )
      )
    )
}
