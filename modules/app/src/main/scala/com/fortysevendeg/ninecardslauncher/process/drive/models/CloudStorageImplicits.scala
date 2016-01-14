package com.fortysevendeg.ninecardslauncher.process.drive.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.json.{CollectionTypeImplicits, NineCardCategoryImplicits}

object CloudStorageImplicits {

  import play.api.libs.json._
  import NineCardCategoryImplicits._
  import CollectionTypeImplicits._

  implicit val cloudStorageCollectionItemReads = Json.reads[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionReads = Json.reads[CloudStorageCollection]
  implicit val cloudStorageDeviceReads = Json.reads[CloudStorageDevice]

  implicit val cloudStorageCollectionItemWrites = Json.writes[CloudStorageCollectionItem]
  implicit val cloudStorageCollectionWrites = Json.writes[CloudStorageCollection]
  implicit val cloudStorageDeviceWrites = Json.writes[CloudStorageDevice]

}
