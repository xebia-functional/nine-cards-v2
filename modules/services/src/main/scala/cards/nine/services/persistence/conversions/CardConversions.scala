package cards.nine.services.persistence.conversions

import cards.nine.models.types.CardType
import cards.nine.models.{Card, CardData}
import cards.nine.repository.model.{
  Card => RepositoryCard,
  CardData => RepositoryCardData,
  CardsWithCollectionId
}
import cards.nine.models.NineCardsIntentConversions

trait CardConversions extends NineCardsIntentConversions {

  def toCardsWithCollectionId(cardsByCollectionId: (Int, Seq[CardData])): CardsWithCollectionId = {
    val (collectionId, cards) = cardsByCollectionId
    CardsWithCollectionId(collectionId = collectionId, data = cards map toRepositoryCardData)
  }

  def toCard(card: RepositoryCard): Card = {
    Card(
      id = card.id,
      position = card.data.position,
      term = card.data.term,
      packageName = card.data.packageName,
      cardType = CardType(card.data.cardType),
      intent = jsonToNineCardIntent(card.data.intent),
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
        cardType = card.cardType.name,
        intent = nineCardIntentToJson(card.intent),
        imagePath = card.imagePath,
        notification = card.notification
      )
    )

  def toRepositoryCardData(card: CardData): RepositoryCardData =
    RepositoryCardData(
      position = card.position,
      term = card.term,
      packageName = card.packageName,
      cardType = card.cardType.name,
      intent = nineCardIntentToJson(card.intent),
      imagePath = card.imagePath,
      notification = card.notification)
}
