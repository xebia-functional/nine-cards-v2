package cards.nine.models

import cards.nine.models.types.CardType

case class Card(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardsIntent,
  imagePath: Option[String],
  notification: Option[String] = None) extends Serializable

case class CardData(
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardsIntent,
  imagePath: Option[String],
  notification: Option[String] = None)

object Card {

  implicit class CardOps(card: Card) {

    def toData = CardData(
      position = card.position,
      term = card.term,
      packageName = card.packageName,
      cardType = card.cardType,
      intent = card.intent,
      imagePath = card.imagePath,
      notification = card.notification)
  }
}
//
//case class PrivateCard(
//  term: String,
//  packageName: Option[String],
//  cardType: CardType,
//  intent: NineCardIntent,
//  imagePath: Option[String])