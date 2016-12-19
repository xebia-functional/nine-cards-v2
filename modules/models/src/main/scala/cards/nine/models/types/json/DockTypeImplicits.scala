package cards.nine.models.types.json

import cards.nine.models.types.DockType
import play.api.libs.json._

object DockTypeImplicits {

  implicit val dockTypeReads = new Reads[DockType] {
    def reads(js: JsValue): JsResult[DockType] =
      JsSuccess(DockType(js.as[String]))
  }

  implicit val dockTypeWrites = new Writes[DockType] {
    def writes(dockType: DockType): JsValue =
      JsString(dockType.name)
  }

}
