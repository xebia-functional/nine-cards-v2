package cards.nine.commons.test.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.models.types.CardType
import cards.nine.models.{Card, CardData, NineCardsIntentConversions}

trait CardTestData extends NineCardsIntentConversions {

  def card(num: Int = 0) = Card(
    id = cardId + num,
    position = cardPosition + num,
    term = term,
    packageName = Option(cardPackageName + num),
    cardType = CardType(cardType),
    intent = jsonToNineCardIntent(cardIntent),
    imagePath = Option(cardImagePath),
    notification = Option(notification))

  val card: Card = card(0)
  val seqCard: Seq[Card] = Seq(card(0), card(1), card(2))

  def cardData(num: Int = 0) = CardData(
    position = cardPosition + num,
    term = term,
    packageName = Option(cardPackageName + num),
    cardType = CardType(cardType),
    intent = jsonToNineCardIntent(cardIntent),
    imagePath = Option(cardImagePath),
    notification = Option(notification))

  val cardData: CardData  = cardData(0)
  val seqCardData: Seq[CardData] = Seq(cardData(0), cardData(1), cardData(2))

}
