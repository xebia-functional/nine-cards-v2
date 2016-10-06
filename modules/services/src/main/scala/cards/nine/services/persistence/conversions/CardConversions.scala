package cards.nine.services.persistence.conversions

import cards.nine.models.{CardData, Card}
import cards.nine.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData, CardsWithCollectionId}
import cards.nine.services.persistence._

trait CardConversions {

  def toCardsWithCollectionId(cardsByCollectionId: (Int, Seq[CardData])): CardsWithCollectionId = {
    val (collectionId, cards) = cardsByCollectionId
    CardsWithCollectionId(
      collectionId = collectionId,
      data = cards map toRepositoryCardData)
  }
  
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

  def toRepositoryCard(card: Card): RepositoryCard =
    RepositoryCard(
      id = card.id,
      data = RepositoryCardData(
        position = card.position,
        term = card.term,
        packageName = card.packageName,
        cardType = card.cardType,
        intent = card.intent,
        imagePath = card.imagePath,
        notification = card.notification
      )
    )

  def toRepositoryCardData(card: CardData): RepositoryCardData =
    RepositoryCardData(
      position = card.position,
      term = card.term,
      packageName = card.packageName,
      cardType = card.cardType,
      intent = card.intent,
      imagePath = card.imagePath,
      notification = card.notification)
}
