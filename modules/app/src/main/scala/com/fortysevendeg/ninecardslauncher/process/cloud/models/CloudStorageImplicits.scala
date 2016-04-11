package com.fortysevendeg.ninecardslauncher.process.cloud.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.json.{CollectionTypeImplicits, NineCardCategoryImplicits}

object CloudStorageImplicits {

  import play.api.libs.json._
  import NineCardCategoryImplicits._
  import CollectionTypeImplicits._

  implicit val cloudStorageMomentTimeSlotReads = Json.reads[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentReads = Json.reads[CloudStorageMoment]
  implicit val cloudStorageCollectionItemReads = Json.reads[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionReads = Json.reads[CloudStorageCollection]
  implicit val cloudStorageDeviceReads = Json.reads[CloudStorageDevice]

  implicit val cloudStorageMomentTimeSlotWrites = Json.writes[CloudStorageMomentTimeSlot]
  implicit val cloudStorageMomentWrites = Json.writes[CloudStorageMoment]
  implicit val cloudStorageCollectionItemWrites = Json.writes[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionWrites = Json.writes[CloudStorageCollection]
  implicit val cloudStorageDeviceWrites = Json.writes[CloudStorageDevice]

}
