/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.api.version2

object JsonImplicits {

  import play.api.libs.json._

  implicit val loginResponseReads                 = Json.reads[ApiLoginResponse]
  implicit val installationResponseReads          = Json.reads[InstallationResponse]
  implicit val collectionAppReads                 = Json.reads[CollectionApp]
  implicit val collectionReads                    = Json.reads[Collection]
  implicit val collectionsResponseReads           = Json.reads[CollectionsResponse]
  implicit val packagesStatsReads                 = Json.reads[PackagesStats]
  implicit val createCollectionResponseReads      = Json.reads[CreateCollectionResponse]
  implicit val updateCollectionResponseReads      = Json.reads[UpdateCollectionResponse]
  implicit val categorizedAppReads                = Json.reads[CategorizedApp]
  implicit val categorizedAppDetailReads          = Json.reads[CategorizedAppDetail]
  implicit val categorizeResponseReads            = Json.reads[CategorizeResponse]
  implicit val categorizeDetailResponseReads      = Json.reads[CategorizeDetailResponse]
  implicit val recommendationAppReads             = Json.reads[NotCategorizedApp]
  implicit val recommendationsResponseReads       = Json.reads[RecommendationsResponse]
  implicit val recommendationsByAppsResponseReads = Json.reads[RecommendationsByAppsResponse]
  implicit val subscriptionsResponseReads         = Json.reads[SubscriptionsResponse]
  implicit val rankAppsCategoryResponseReads      = Json.reads[RankAppsCategoryResponse]
  implicit val rankAppsResponseReads              = Json.reads[RankAppsResponse]
  implicit val rankAppsByMomentResponseReads      = Json.reads[RankAppsByMomentResponse]
  implicit val rankWidgetsResponseReads           = Json.reads[RankWidgetsResponse]
  implicit val rankWidgetsWithMomentResponse      = Json.reads[RankWidgetsWithMomentResponse]
  implicit val rankWidgetsByMomentResponse        = Json.reads[RankWidgetsByMomentResponse]
  implicit val searchResponseReads                = Json.reads[SearchResponse]

  implicit val loginRequestWrites                 = Json.writes[ApiLoginRequest]
  implicit val installationRequestWrites          = Json.writes[InstallationRequest]
  implicit val createCollectionRequestWrites      = Json.writes[CreateCollectionRequest]
  implicit val collectionUpdateInfoWrites         = Json.writes[CollectionUpdateInfo]
  implicit val updateCollectionRequestWrites      = Json.writes[UpdateCollectionRequest]
  implicit val categorizeRequestWrites            = Json.writes[CategorizeRequest]
  implicit val recommendationsRequestWrites       = Json.writes[RecommendationsRequest]
  implicit val recommendationsByAppsRequestWrites = Json.writes[RecommendationsByAppsRequest]
  implicit val rankAppsRequestWrites              = Json.writes[RankAppsRequest]
  implicit val rankAppsByMomentRequestWrites      = Json.writes[RankAppsByMomentRequest]
  implicit val rankWidgetsByMomentRequest         = Json.writes[RankWidgetsByMomentRequest]
  implicit val searchRequestWrites                = Json.writes[SearchRequest]

}
