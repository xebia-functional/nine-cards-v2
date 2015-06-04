package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepoCard, CardData => RepoCardData}
import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.services.api.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
import com.fortysevendeg.ninecardslauncher.{repository => repo}
import play.api.libs.json.Json

trait CardConversions {

  def toCard(card: RepoCard) = {
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
    RepoCard(
      id = card.id,
      data = RepoCardData(
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
      data = RepoCardData(
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
      RepoCard(
        id = request.id,
        data = RepoCardData(
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
