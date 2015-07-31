package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.{ServiceClientException, ServiceClient}
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Writes, Reads}

class ApiRecommendationService(serviceClient: ServiceClient) {

  private val PrefixRecommendation = "/collections"
  private val Prefix9CardsRecommendation = "/ninecards/collections"
  private val RecommendationsPath = "recommendations"
  private val AppsPath = "apps"
  private val SponsoredPath = "items/sponsored"

  def getRecommendedCollections(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[CollectionRecommendations]): ServiceDef2[ServiceClientResponse[CollectionRecommendations], HttpClientException with ServiceClientException] =
    serviceClient.get[CollectionRecommendations](
      path = s"$PrefixRecommendation",
      headers = headers)

  def getRecommendedApps(
    recommendationRequest: RecommendationRequest,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[GooglePlayRecommendation], 
    writes: Writes[RecommendationRequest]): ServiceDef2[ServiceClientResponse[GooglePlayRecommendation], HttpClientException with ServiceClientException] =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$PrefixRecommendation/$RecommendationsPath/$AppsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getRecommendedCollectionApps(
    recommendationRequest: RecommendationRequest,
    headers: Seq[(String, String)]
    )(implicit reads: Reads[GooglePlayRecommendation], 
    writes: Writes[RecommendationRequest]): ServiceDef2[ServiceClientResponse[GooglePlayRecommendation], HttpClientException with ServiceClientException] =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$PrefixRecommendation/$RecommendationsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getSponsoredCollections(
    headers: Seq[(String, String)]
    )(implicit reads: Reads[CollectionSponsored]): ServiceDef2[ServiceClientResponse[CollectionSponsored], HttpClientException with ServiceClientException] =
    serviceClient.get[CollectionSponsored](
      path = s"$Prefix9CardsRecommendation/$SponsoredPath",
      headers = headers,
      reads = Some(reads))

}
