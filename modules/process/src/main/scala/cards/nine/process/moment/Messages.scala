package cards.nine.process.moment

import cards.nine.process.commons.models.{FormedWidget, MomentTimeSlot}
import cards.nine.models.types.NineCardsMoment

case class MomentProcessConfig(
  namesMoments: Map[NineCardsMoment, String])

case class UpdateMomentRequest(
  id: Int,
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment])

case class SaveMomentRequest(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment],
  widgets: Option[Seq[FormedWidget]])