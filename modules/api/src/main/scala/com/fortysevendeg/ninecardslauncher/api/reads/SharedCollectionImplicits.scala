package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._

trait SharedCollectionImplicits {

  import play.api.libs.json._

  implicit val userConfigTimeSlotReads = Json.reads[UserConfigTimeSlot]
  implicit val sharedCollectionPackageReads = Json.reads[SharedCollectionPackage]
  implicit val assetThumbResponseReads = Json.reads[AssetThumbResponse]
  implicit val assetResponseReads = Json.reads[AssetResponse]
  implicit val sharedCollectionReads = Json.reads[SharedCollection]
  implicit val sharedCollectionListReads = Json.reads[SharedCollectionList]
  implicit val sharedCollectionSubscriptionReads = Json.reads[SharedCollectionSubscription]

  implicit val userConfigTimeSlotWrites = Json.writes[UserConfigTimeSlot]
  implicit val sharedCollectionPackageWrites = Json.writes[SharedCollectionPackage]
  implicit val assetThumbResponseWrites = Json.writes[AssetThumbResponse]
  implicit val assetResponseWrites = Json.writes[AssetResponse]
  implicit val sharedCollectionWrites = Json.writes[SharedCollection]

}
