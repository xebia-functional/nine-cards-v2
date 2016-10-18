package cards.nine.services.persistence.conversions

import cards.nine.models.reads.MomentImplicits
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.{Moment, MomentData, MomentTimeSlot}
import cards.nine.repository.model.{Moment => RepositoryMoment, MomentData => RepositoryMomentData}
import play.api.libs.json.Json

trait MomentConversions {

  import MomentImplicits._

  def toMoment(moment: RepositoryMoment): Moment =
    Moment(
      id = moment.id,
      collectionId = moment.data.collectionId,
      timeslot = Json.parse(moment.data.timeslot).as[Seq[MomentTimeSlot]],
      wifi = if (moment.data.wifi.isEmpty) List.empty else moment.data.wifi.split(",").toList,
      headphone = moment.data.headphone,
      momentType = moment.data.momentType.map(NineCardsMoment(_)),
      widgets = None)

  def toRepositoryMoment(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = moment.collectionId,
        timeslot = Json.toJson(moment.timeslot).toString,
        wifi = moment.wifi.mkString(","),
        headphone = moment.headphone,
        momentType = moment.momentType map (_.name)))

  def toRepositoryMomentWithoutCollection(moment: Moment): RepositoryMoment =
    RepositoryMoment(
      id = moment.id,
      data = RepositoryMomentData(
        collectionId = None,
        timeslot = Json.toJson(moment.timeslot).toString,
        wifi = moment.wifi.mkString(","),
        headphone = moment.headphone,
        momentType = moment.momentType map (_.name)))

  def toRepositoryMomentData(moment: MomentData): RepositoryMomentData =
    RepositoryMomentData(
      collectionId = moment.collectionId,
      timeslot = Json.toJson(moment.timeslot).toString,
      wifi = moment.wifi.mkString(","),
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))
}
