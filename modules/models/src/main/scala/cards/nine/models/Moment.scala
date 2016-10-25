package cards.nine.models

import cards.nine.models.types._

case class Moment(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: NineCardsMoment,
  widgets: Option[Seq[WidgetData]] = None)

case class MomentData(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: NineCardsMoment,
  widgets: Option[Seq[WidgetData]] = None)

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

  implicit class MomentTimeSlotOps(moment: NineCardsMoment) {

    def toMomentTimeSlot: Seq[MomentTimeSlot] =
      moment match {
        case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case MusicMoment => Seq.empty
        case CarMoment => Seq.empty
        case SportsMoment => Seq.empty
        case OutAndAboutMoment => Seq.empty
        case _ => Seq.empty
      }
  }
}