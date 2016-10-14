package cards.nine.models.reads

import cards.nine.models.MomentTimeSlot

object MomentImplicits {

  import play.api.libs.json._

  implicit val momentTimeSlotReads = Json.reads[MomentTimeSlot]

  implicit val momentTimeSlotWrites = Json.writes[MomentTimeSlot]

}