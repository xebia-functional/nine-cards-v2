package cards.nine.models.types.json

import cards.nine.models.types.CollectionType
import play.api.libs.json._

object CollectionTypeImplicits {

  implicit val collectionTypeReads = new Reads[CollectionType] {
    def reads(js: JsValue): JsResult[CollectionType] =
      JsSuccess(CollectionType(js.as[String]))
  }

  implicit val collectionTypeWrites = new Writes[CollectionType] {
    def writes(collectionType: CollectionType): JsValue =
      JsString(collectionType.name)
  }

}
