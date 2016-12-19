package cards.nine.models.types.json

import cards.nine.models.types.NineCardsCategory
import play.api.libs.json._

object NineCardCategoryImplicits {

  implicit val nineCardCategoryReads = new Reads[NineCardsCategory] {
    def reads(js: JsValue): JsResult[NineCardsCategory] =
      JsSuccess(NineCardsCategory(js.as[String]))
  }

  implicit val nineCardCategoryWrites = new Writes[NineCardsCategory] {
    def writes(category: NineCardsCategory): JsValue =
      JsString(category.name)
  }

}
