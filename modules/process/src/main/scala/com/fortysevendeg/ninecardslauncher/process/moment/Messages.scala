package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.process.commons.models.MomentTimeSlot
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment

case class MomentProcessConfig(
  namesMoments: Map[NineCardsMoment, String])

case class SaveMomentRequest(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment])