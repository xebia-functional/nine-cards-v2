package com.fortysevendeg.ninecardslauncher.process.drive.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CollectionType, NineCardCategory}

object CloudStorageImplicits {

  import play.api.libs.json._

  implicit val nineCardCategoryReads = new Reads[NineCardCategory] {
    def reads(js: JsValue): JsResult[NineCardCategory] = {
      JsSuccess(NineCardCategory(js.as[String]))
    }
  }

  implicit val collectionTypeReads = new Reads[CollectionType] {
    def reads(js: JsValue): JsResult[CollectionType] = {
      JsSuccess(CollectionType(js.as[String]))
    }
  }

  implicit val cloudStorageCollectionItemReads = Json.reads[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionReads = Json.reads[CloudStorageCollection]
  implicit val cloudStorageDeviceReads = Json.reads[CloudStorageDevice]

  implicit val nineCardCategoryWrites = new Writes[NineCardCategory] {
    def writes(category: NineCardCategory): JsValue = {
      JsString(category.name)
    }
  }

  implicit val collectionTypeWrites = new Writes[CollectionType] {
    def writes(collectionType: CollectionType): JsValue = {
      JsString(collectionType.name)
    }
  }

  implicit val cloudStorageCollectionItemWrites = Json.writes[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionWrites = Json.writes[CloudStorageCollection]
  implicit val cloudStorageDeviceWrites = Json.writes[CloudStorageDevice]

}
