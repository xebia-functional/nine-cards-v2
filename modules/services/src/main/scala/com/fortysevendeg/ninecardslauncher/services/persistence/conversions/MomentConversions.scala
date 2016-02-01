package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Moment => RepositoryMoment, MomentData => RepositoryMomentData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{MomentTimeSlot, Moment}
import com.fortysevendeg.ninecardslauncher.services.persistence.reads.MomentImplicits
import play.api.libs.json.Json

trait MomentConversions {

  import MomentImplicits._

  def toMomentSeq(moment: Seq[RepositoryMoment]): Seq[Moment] = moment map toMoment

  def toMoment(moment: RepositoryMoment): Moment =
    Moment(
      id = moment.id,
      collectionId = moment.data.collectionId,
      timeslot = Json.parse(moment.data.timeslot).as[Seq[MomentTimeSlot]],
      wifi = moment.data.wifi.split(","),
      headphone = moment.data.headphone)

  def toRepositoryMoment(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = moment.collectionId,
        timeslot = Json.toJson(moment.timeslot).toString,
        wifi = moment.wifi.mkString(","),
        headphone = moment.headphone))

  def toRepositoryMoment(request: UpdateMomentRequest): RepositoryMoment =
    RepositoryMoment(
      id = request.id,
      data = RepositoryMomentData(
        collectionId = request.collectionId,
        timeslot = Json.toJson(request.timeslot).toString,
        wifi = request.wifi.mkString(","),
        headphone = request.headphone))

  def toRepositoryMomentData(request: AddMomentRequest): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = request.collectionId,
      timeslot = Json.toJson(request.timeslot).toString,
      wifi = request.wifi.mkString(","),
      headphone = request.headphone)
}
