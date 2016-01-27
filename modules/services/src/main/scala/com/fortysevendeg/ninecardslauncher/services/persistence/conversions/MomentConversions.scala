package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Moment => RepositoryMoment, MomentData => RepositoryMomentData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment

trait MomentConversions {

  def toMomentSeq(moment: Seq[RepositoryMoment]): Seq[Moment] = moment map toMoment

  def toMoment(moment: RepositoryMoment): Moment =
    Moment(
      id = moment.id,
      collectionId = moment.data.collectionId,
      timeslot = moment.data.timeslot,
      wifi = moment.data.wifi,
      headphone = moment.data.headphone)

  def toRepositoryMoment(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = moment.collectionId,
        timeslot = moment.timeslot,
        wifi = moment.wifi,
        headphone = moment.headphone))

  def toRepositoryMoment(request: UpdateMomentRequest): RepositoryMoment =
    RepositoryMoment(
      id = request.id,
      data = RepositoryMomentData(
        collectionId = request.collectionId,
        timeslot = request.timeslot,
        wifi = request.wifi,
        headphone = request.headphone))

  def toRepositoryMomentData(request: AddMomentRequest): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = request.collectionId,
      timeslot = request.timeslot,
      wifi = request.wifi,
      headphone = request.headphone)
}
