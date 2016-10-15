package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.repository.model.{Moment, MomentData}

trait MomentPersistenceServicesData {

  def momentData(num: Int = 0) = MomentData(
    collectionId = Option(momentCollectionId + num),
    timeslot = timeslotJson,
    wifi = Seq(wifiSeq(num)).mkString(","),
    headphone = headphone,
    momentType = Option(momentTypeSeq(num)))

  val repoMomentData: MomentData = momentData(0)
  val seqRepoMomentData = Seq(momentData(0), momentData(1), momentData(2))

  def moment(num: Int = 0) = Moment(
    id = momentId,
    data = momentData(num))

  val repoMoment: Moment = moment(0)
  val seqRepoMoment = Seq(moment(0), moment(1), moment(2))

}
