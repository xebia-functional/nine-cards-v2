package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.repository.model.{Card, CardData}

trait CardPersistenceServicesData {

  def repoCardData(num: Int = 0) =
    CardData(
      position = cardPosition + num,
      term = term,
      packageName = Option(cardPackageName + num),
      cardType = cardType,
      intent = intent,
      imagePath = Option(cardImagePath),
      notification = Option(notification))

  val repoCardData: CardData         = repoCardData(0)
  val seqRepoCardData: Seq[CardData] = Seq(repoCardData(0), repoCardData(1), repoCardData(2))

  def repoCard(num: Int = 0) = Card(id = cardId + num, data = repoCardData(num))

  val repoCard: Card         = repoCard(0)
  val seqRepoCard: Seq[Card] = Seq(repoCard(0), repoCard(1), repoCard(2))

}
