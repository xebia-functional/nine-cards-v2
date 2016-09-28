package com.fortysevendeg.ninecardslauncher.services.persistence.reads

import com.fortysevendeg.ninecardslauncher.services.persistence.models.MomentTimeSlot

object MomentImplicits {

  import play.api.libs.json._

  implicit val momentTimeSlotReads = Json.reads[MomentTimeSlot]

  implicit val momentTimeSlotWrites = Json.writes[MomentTimeSlot]

}