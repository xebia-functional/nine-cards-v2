package cards.nine.services.persistence.conversions

import cards.nine.commons._
import cards.nine.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData, CardsWithCollectionId}
import cards.nine.services.persistence._
import cards.nine.services.persistence.models.Card

trait CardConversions {

  def toCardsWithCollectionId(request: AddCardWithCollectionIdRequest): CardsWithCollectionId =
    CardsWithCollectionId(
      collectionId = request.collectionId,
      data = request.cards map toRepositoryCardData)

  def toCard(card: RepositoryCard): Card = {
    Card(
      id = card.id,
      position = card.data.position,
      term = card.data.term,
      packageName = card.data.packageName,
      cardType = card.data.cardType,
      intent = card.data.intent,
      imagePath = card.data.imagePath,
      notification = card.data.notification)
  }

  def toRepositoryCard(request: UpdateCardRequest): RepositoryCard =
    RepositoryCard(
      id = request.id,
      data = RepositoryCardData(
        position = request.position,
        term = request.term,
        packageName = request.packageName,
        cardType = request.cardType,
        intent = request.intent,
        imagePath = request.imagePath,
        notification = request.notification
      )
    )

  def toRepositoryCardData(request: AddCardRequest): RepositoryCardData =
    RepositoryCardData(
      position = request.position,
      term = request.term,
      packageName = request.packageName,
      cardType = request.cardType,
      intent = request.intent,
      imagePath = request.imagePath,
      notification = request.notification)
}
