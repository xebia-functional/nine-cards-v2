package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData}

trait CardPersistenceServicesData {

  def createSeqRepoCard(
    num: Int = 5,
    id: Int = cardId,
    data: RepositoryCardData = createRepoCardData()): Seq[RepositoryCard] =
    List.tabulate(num)(item => RepositoryCard(id = id + item, data = data))

  def createRepoCardData(
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: String = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification): RepositoryCardData =
    RepositoryCardData(
      position = position,
      term = term,
      packageName = Option(packageName),
      cardType = cardType,
      intent = intent,
      imagePath = Option(imagePath),
      notification = Option(notification))

  val repoCardData: RepositoryCardData = createRepoCardData()
  val seqRepoCard: Seq[RepositoryCard] = createSeqRepoCard(data = repoCardData)
  val repoCard: RepositoryCard = seqRepoCard(0)

}
