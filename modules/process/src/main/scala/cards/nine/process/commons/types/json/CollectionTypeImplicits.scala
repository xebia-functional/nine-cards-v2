package cards.nine.process.commons.types.json

import cards.nine.process.commons.types.CollectionType
import play.api.libs.json._

object CollectionTypeImplicits {

  implicit val collectionTypeReads = new Reads[CollectionType] {
    def reads(js: JsValue): JsResult[CollectionType] = {
      JsSuccess(CollectionType(js.as[String]))
    }
  }

  implicit val collectionTypeWrites = new Writes[CollectionType] {
    def writes(collectionType: CollectionType): JsValue = {
      JsString(collectionType.name)
    }
  }

}
