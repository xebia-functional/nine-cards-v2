package cards.nine.models

import cards.nine.models.types.NineCardsMoment

case class Moment(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment],
  widgets: Option[Seq[WidgetData]])

case class MomentData(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment],
  widgets: Option[Seq[WidgetData]])

case class MomentTimeSlot(
  from: String,
  to: String,
  days: Seq[Int])

object Moment {

  implicit class MomentOps(moment: Moment) {

    def toData = MomentData(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType,
      widgets = moment.widgets)
  }
}