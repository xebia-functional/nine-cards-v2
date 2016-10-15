package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.repository.model.{Card, CardData}

trait CardPersistenceServicesData {

  def cardData(num: Int = 0) = CardData(
    position = cardPosition + num,
    term = term,
    packageName = Option(cardPackageName + num),
    cardType = cardType,
    intent = cardIntent,
    imagePath = Option(cardImagePath),
    notification = Option(notification))

  val repoCardData: CardData = cardData(0)
  val seqRepoCardData: Seq[CardData] = Seq(cardData(0), cardData(1), cardData(2))

  def card(num: Int = 0) = Card(
    id = cardId + num,
    data = cardData(num))

  val repoCard: Card = card(0)
  val seqRepoCard: Seq[Card] = Seq(card(0), card(1), card(2))

}
