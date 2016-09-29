package cards.nine.services.persistence.reads

import cards.nine.services.persistence.models.MomentTimeSlot

object MomentImplicits {

  import play.api.libs.json._

  implicit val momentTimeSlotReads = Json.reads[MomentTimeSlot]

  implicit val momentTimeSlotWrites = Json.writes[MomentTimeSlot]

}