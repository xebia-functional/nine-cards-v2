package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._

object GooglePlayImplicits {

  import play.api.libs.json._

  implicit val aggregateRatingReads = Json.reads[GooglePlayAggregateRating]
  implicit val offerReads = Json.reads[GooglePlayOffer]
  implicit val detailAppReads = Json.reads[GooglePlayAppDetails]
  implicit val detailReads = Json.reads[GooglePlayDetails]
  implicit val imageReads = Json.reads[GooglePlayImage]
  implicit val appReads = Json.reads[GooglePlayApp]
  implicit val packageReads = Json.reads[GooglePlayPackage]
  implicit val packagesReads = Json.reads[GooglePlayPackages]

  implicit val requestPackagesWrites = Json.writes[PackagesRequest]

}
