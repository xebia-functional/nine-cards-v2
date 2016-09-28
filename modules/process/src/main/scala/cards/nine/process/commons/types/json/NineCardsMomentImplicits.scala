package cards.nine.process.commons.types.json

import cards.nine.process.commons.types.NineCardsMoment
import play.api.libs.json._

object NineCardsMomentImplicits {

  implicit val nineCardMomentReads = new Reads[NineCardsMoment] {
    def reads(js: JsValue): JsResult[NineCardsMoment] = {
      JsSuccess(NineCardsMoment(js.as[String]))
    }
  }

  implicit val nineCardMomentWrites = new Writes[NineCardsMoment] {
    def writes(moment: NineCardsMoment): JsValue = {
      JsString(moment.name)
    }
  }

}
