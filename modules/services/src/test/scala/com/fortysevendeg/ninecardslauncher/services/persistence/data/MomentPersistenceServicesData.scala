package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.repository.model.{Moment => RepositoryMoment, MomentData => RepositoryMomentData}

trait MomentPersistenceServicesData {

  def createRepoMomentData(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: String = timeslotJson,
    wifiString: String = wifiString,
    headphone: Boolean = headphone,
    momentType: String = momentTypeStr): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifiString,
      headphone = headphone,
      momentType = Option(momentType))

  def createSeqRepoMoment(
    num: Int = 5,
    id: Int = momentId,
    data: RepositoryMomentData = createRepoMomentData()): Seq[RepositoryMoment] =
    List.tabulate(num)(item => RepositoryMoment(id = id + item, data = data))

  val repoMomentData: RepositoryMomentData = createRepoMomentData()
  val seqRepoMoment: Seq[RepositoryMoment] = createSeqRepoMoment(data = repoMomentData)
  val repoMoment: RepositoryMoment = seqRepoMoment(0)

}
