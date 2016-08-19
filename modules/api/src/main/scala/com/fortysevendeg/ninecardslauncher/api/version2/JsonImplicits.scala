package com.fortysevendeg.ninecardslauncher.api.version2

object JsonImplicits {

  import play.api.libs.json._

  implicit val loginResponseReads = Json.reads[LoginResponse]
  implicit val installationResponseReads = Json.reads[InstallationResponse]
  implicit val collectionAppReads = Json.reads[CollectionApp]
  implicit val collectionReads = Json.reads[Collection]
  implicit val collectionsResponseReads = Json.reads[CollectionsResponse]
  implicit val packagesStatsReads = Json.reads[PackagesStats]
  implicit val createCollectionResponseReads = Json.reads[CreateCollectionResponse]
  implicit val updateCollectionResponseReads = Json.reads[UpdateCollectionResponse]
  implicit val categorizedAppReads = Json.reads[CategorizedApp]
  implicit val categorizeResponseReads = Json.reads[CategorizeResponse]
  implicit val recommendationAppReads = Json.reads[RecommendationApp]
  implicit val recommendationsResponseReads = Json.reads[RecommendationsResponse]

  implicit val loginRequestWrites = Json.writes[LoginRequest]
  implicit val installationRequestWrites = Json.writes[InstallationRequest]
  implicit val createCollectionRequestWrites = Json.writes[CreateCollectionRequest]
  implicit val collectionUpdateInfoWrites = Json.writes[CollectionUpdateInfo]
  implicit val updateCollectionRequestWrites = Json.writes[UpdateCollectionRequest]
  implicit val categorizeRequestWrites = Json.writes[CategorizeRequest]

}
