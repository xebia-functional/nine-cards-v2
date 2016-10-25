package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.repository.model.{Moment, MomentData}

trait MomentPersistenceServicesData {

  def repoMomentData(num: Int = 0) = MomentData(
    collectionId = Option(momentCollectionId + num),
    timeslot = timeslotJson,
    wifi = Seq(wifiSeq(num)).mkString(","),
    headphone = headphone,
    momentType = Option(momentTypeSeq(num)))

  val repoMomentData: MomentData = repoMomentData(0)
  val seqRepoMomentData = Seq(repoMomentData(0), repoMomentData(1), repoMomentData(2))

  def repoMoment(num: Int = 0) = Moment(
    id = momentId + num,
    data = repoMomentData(num))

  val repoMoment: Moment = repoMoment(0)
  val seqRepoMoment = Seq(repoMoment(0), repoMoment(1), repoMoment(2))

}
