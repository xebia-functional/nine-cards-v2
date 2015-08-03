package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiRecommendationService(serviceClient: ServiceClient) {

  private[this] val prefixRecommendation = "/collections"
  private[this] val prefix9CardsRecommendation = "/ninecards/collections"
  private[this] val recommendationsPath = "recommendations"
  private[this] val appsPath = "apps"
  private[this] val sponsoredPath = "items/sponsored"

  def getRecommendedCollections(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[CollectionRecommendations]): ServiceDef2[ServiceClientResponse[CollectionRecommendations], HttpClientException with ServiceClientException] =
    serviceClient.get[CollectionRecommendations](
      path = s"$prefixRecommendation",
      headers = headers)

  def getRecommendedApps(
    recommendationRequest: RecommendationRequest,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[GooglePlayRecommendation], 
    writes: Writes[RecommendationRequest]): ServiceDef2[ServiceClientResponse[GooglePlayRecommendation], HttpClientException with ServiceClientException] =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$prefixRecommendation/$recommendationsPath/$appsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getRecommendedCollectionApps(
    recommendationRequest: RecommendationRequest,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[GooglePlayRecommendation], 
    writes: Writes[RecommendationRequest]): ServiceDef2[ServiceClientResponse[GooglePlayRecommendation], HttpClientException with ServiceClientException] =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$prefixRecommendation/$recommendationsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getSponsoredCollections(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[CollectionSponsored]): ServiceDef2[ServiceClientResponse[CollectionSponsored], HttpClientException with ServiceClientException] =
    serviceClient.get[CollectionSponsored](
      path = s"$prefix9CardsRecommendation/$sponsoredPath",
      headers = headers,
      reads = Some(reads))

}
