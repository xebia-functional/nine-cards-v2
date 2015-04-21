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
  implicit val simplePackageReads = Json.reads[GooglePlaySimplePackage]
  implicit val packagesReads = Json.reads[GooglePlayPackages]
  implicit val simplePackagesReads = Json.reads[GooglePlaySimplePackages]
  implicit val searchMetadataReads = Json.reads[GooglePlaySearchMetadata]
  implicit val searchDocsReads = Json.reads[GooglePlaySearchDoc]
  implicit val searchRelatedReads = Json.reads[GooglePlayRelatedSearch]
  implicit val searchReads = Json.reads[GooglePlaySearch]

  implicit val requestPackagesWrites = Json.writes[PackagesRequest]

}
