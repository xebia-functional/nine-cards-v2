package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._

object RecommendationImplicits {

  import play.api.libs.json._

  import GooglePlayImplicits._
  import UserConfigImplicits._

  implicit val appiaAdReads = Json.reads[AppiaAd]
  implicit val playRecommendationItemsReads = Json.reads[GooglePlayRecommendationItems]
  implicit val playRecommendationReads = Json.reads[GooglePlayRecommendation]
  implicit val collectionRecommendationReads = Json.reads[CollectionRecommendation]
  implicit val collectionRecommendationsReads = Json.reads[CollectionRecommendations]
  implicit val collectionSponsoredReads = Json.reads[CollectionSponsored]

  implicit val adsRequestWrites = Json.writes[AdsRequest]
  implicit val recommendationRequestWrites = Json.writes[RecommendationRequest]

}
