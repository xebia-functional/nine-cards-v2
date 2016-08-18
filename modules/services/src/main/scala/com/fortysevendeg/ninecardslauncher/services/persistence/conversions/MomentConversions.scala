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
      wifi = if (moment.data.wifi.isEmpty) List.empty else moment.data.wifi.split(",").toList,
      headphone = moment.data.headphone,
      momentType = moment.data.momentType)

  def toRepositoryMoment(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = moment.collectionId,
        timeslot = Json.toJson(moment.timeslot).toString,
        wifi = moment.wifi.mkString(","),
        headphone = moment.headphone,
        momentType = moment.momentType))

  def toRepositoryMomentWithoutCollection(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = None,
        timeslot = Json.toJson(moment.timeslot).toString,
        wifi = moment.wifi.mkString(","),
        headphone = moment.headphone,
        momentType = moment.momentType))

  def toRepositoryMoment(request: UpdateMomentRequest): RepositoryMoment =
    RepositoryMoment(
      id = request.id,
      data = RepositoryMomentData(
        collectionId = request.collectionId,
        timeslot = Json.toJson(request.timeslot).toString,
        wifi = request.wifi.mkString(","),
        headphone = request.headphone,
        momentType = request.momentType))

  def toRepositoryMomentData(request: AddMomentRequest): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = request.collectionId,
      timeslot = Json.toJson(request.timeslot).toString,
      wifi = request.wifi.mkString(","),
      headphone = request.headphone,
      momentType = request.momentType)
}
