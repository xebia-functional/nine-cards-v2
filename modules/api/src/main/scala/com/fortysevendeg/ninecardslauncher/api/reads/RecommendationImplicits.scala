package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._
import GooglePlayImplicits._
import UserConfigImplicits._
import play.api.libs.json._

object RecommendationImplicits {

  implicit val appiaAdReads = Json.reads[AppiaAd]
  implicit val playRecommendationItemsReads = Json.reads[GooglePlayRecommendationItems]
  implicit val playRecommendationReads = Json.reads[GooglePlayRecommendation]

  implicit val adsRequestWrites = Json.writes[AdsRequest]
  implicit val recommendationRequestWrites = Json.writes[RecommendationRequest]

}
