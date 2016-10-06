package cards.nine.models

case class Card(
  id: Int,
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
  imagePath: Option[String],
  notification: Option[String] = None)

case class CardData(
  position: Int,
  term: String,
  packageName: Option[String],
  cardType: String,
  intent: String,
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
