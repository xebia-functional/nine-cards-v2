package com.fortysevendeg.ninecardslauncher.modules.repository.card

import com.fortysevendeg.ninecardslauncher.models.{Card, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.repository.{AddCardRequest => RepositoryAddCardRequest}
import com.fortysevendeg.ninecardslauncher.repository.{DeleteCardRequest => RepositoryDeleteCardRequest}
import com.fortysevendeg.ninecardslauncher.repository.{FetchCardsByCollectionRequest => RepositoryFetchCardsByCollectionRequest}
import com.fortysevendeg.ninecardslauncher.repository.{FindCardByIdRequest => RepositoryFindCardByIdRequest}
import com.fortysevendeg.ninecardslauncher.repository.{UpdateCardRequest => RepositoryUpdateCardRequest}
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard}
import com.fortysevendeg.ninecardslauncher.repository.model.{CardData => RepositoryCardData}
import play.api.libs.json._

trait Conversions {

  def toCard(card: RepositoryCard) = {
    import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
    val intent = Json.parse(card.data.intent).as[NineCardIntent]
    Card(
      id = card.id,
      position = card.data.position,
      micros = card.data.micros,
      term = card.data.term,
      packageName = card.data.packageName,
      `type` = card.data.`type`,
      intent = intent,
      imagePath = card.data.imagePath,
      starRating = card.data.starRating,
      numDownloads = card.data.numDownloads,
      notification = card.data.notification)
  }

  def toRepositoryCard(card: Card) = {
    import com.fortysevendeg.ninecardslauncher.models.NineCardIntentImplicits._
    RepositoryCard(
      id = card.id,
      data = RepositoryCardData(
        position = card.position,
        micros = card.micros,
        term = card.term,
        packageName = card.packageName,
        `type` = card.`type`,
        intent = Json.toJson(card.intent).toString(),
        imagePath = card.imagePath,
        starRating = card.starRating,
        numDownloads = card.numDownloads,
        notification = card.notification
      )
    )
  }
  
  def toRepositoryAddCardRequest(request: AddCardRequest) =
    RepositoryAddCardRequest(
      collectionId = request.collectionId,
      data = RepositoryCardData(
        position = request.cardItem.position,
        term = request.cardItem.term,
        `type` = request.cardItem.`type`,
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
    RepositoryDeleteCardRequest(card = toRepositoryCard(request.card))

  def toRepositoryFetchCardsByCollectionRequest(request: FetchCardsByCollectionRequest) =
    RepositoryFetchCardsByCollectionRequest(collectionId = request.collectionId)

  def toRepositoryFindCardByIdRequest(request: FindCardByIdRequest) =
    RepositoryFindCardByIdRequest(id = request.id)

  def toRepositoryUpdateCardRequest(request: UpdateCardRequest) =
    RepositoryUpdateCardRequest(
      RepositoryCard(
        id = request.id,
        data = RepositoryCardData(
          position = request.position,
          micros = request.micros,
          term = request.term,
          packageName = request.packageName,
          `type` = request.`type`,
          intent = request.intent,
          imagePath = request.imagePath,
          starRating = request.starRating,
          numDownloads = request.numDownloads,
          notification = request.notification
        )
      )
    )
}
