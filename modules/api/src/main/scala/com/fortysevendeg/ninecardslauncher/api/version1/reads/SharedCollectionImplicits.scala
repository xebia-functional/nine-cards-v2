package com.fortysevendeg.ninecardslauncher.api.version1.reads

import com.fortysevendeg.ninecardslauncher.api.version1.model._
import UserConfigImplicits._
import play.api.libs.json._

object SharedCollectionImplicits {

  implicit val sharedCollectionPackageReads = Json.reads[SharedCollectionPackage]
  implicit val assetThumbResponseReads = Json.reads[AssetThumbResponse]
  implicit val assetResponseReads = Json.reads[AssetResponse]
  implicit val sharedCollectionReads = Json.reads[SharedCollection]
  implicit val sharedCollectionListReads = Json.reads[SharedCollectionList]

  implicit val sharedCollectionPackageWrites = Json.writes[SharedCollectionPackage]
  implicit val assetThumbResponseWrites = Json.writes[AssetThumbResponse]
  implicit val assetResponseWrites = Json.writes[AssetResponse]
  implicit val shareCollectionWrites = Json.writes[ShareCollection]
  implicit val sharedCollectionWrites = Json.writes[SharedCollection]

}
