package cards.nine.commons.test.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models.types.CardType
import cards.nine.models.{NineCardsIntentConversions, Card, CardData}

trait CardTestData extends NineCardsIntentConversions {

  def createSeqCardData(
    num: Int = 5,
    collectionId: Int = collectionId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): Seq[CardData] = List.tabulate(num)(
    item => CardData(
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = CardType(cardType),
      intent = jsonToNineCardIntent(intent),
      imagePath = Option(imagePath),
      notification = Option(notification)))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): Seq[Card] = List.tabulate(num)(
    item => Card(
      id = id + item,
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = CardType(cardType),
      intent = jsonToNineCardIntent(intent),
      imagePath = Option(imagePath),
      notification = Option(notification)))

  val seqCard: Seq[Card] = createSeqCard()
  val card: Card = seqCard(0)
  val seqCardData: Seq[CardData] = createSeqCardData()
  val cardData: CardData = seqCardData(0)

}
