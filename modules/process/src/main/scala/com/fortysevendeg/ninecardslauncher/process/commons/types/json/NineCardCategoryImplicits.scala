package com.fortysevendeg.ninecardslauncher.process.commons.types.json

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import play.api.libs.json._

object NineCardCategoryImplicits {

  implicit val nineCardCategoryReads = new Reads[NineCardCategory] {
    def reads(js: JsValue): JsResult[NineCardCategory] = {
      JsSuccess(NineCardCategory(js.as[String]))
    }
  }

  implicit val nineCardCategoryWrites = new Writes[NineCardCategory] {
    def writes(category: NineCardCategory): JsValue = {
      JsString(category.name)
    }
  }

}
