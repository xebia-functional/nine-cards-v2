package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Writes, Reads}

import scala.concurrent.ExecutionContext

class ApiRecommendationService(serviceClient: ServiceClient) {

  private val PrefixRecommendation = "/collections"
  private val Prefix9CardsRecommendation = "/ninecards/collections"
  private val RecommendationsPath = "recommendations"
  private val AppsPath = "apps"
  private val SponsoredPath = "items/sponsored"

  def getRecommendedCollections(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[CollectionRecommendations]) =
    serviceClient.get[CollectionRecommendations](
      path = s"$PrefixRecommendation",
      headers = headers)

  def getRecommendedApps(
      recommendationRequest: RecommendationRequest,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlayRecommendation], writes: Writes[RecommendationRequest]) =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$PrefixRecommendation/$RecommendationsPath/$AppsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getRecommendedCollectionApps(
      recommendationRequest: RecommendationRequest,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlayRecommendation], writes: Writes[RecommendationRequest]) =
    serviceClient.post[RecommendationRequest, GooglePlayRecommendation](
      path = s"$PrefixRecommendation/$RecommendationsPath",
      headers = headers,
      body = recommendationRequest,
      reads = Some(reads))

  def getSponsoredCollections(headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[CollectionSponsored]) =
    serviceClient.get[CollectionSponsored](
      path = s"$Prefix9CardsRecommendation/$SponsoredPath",
      headers = headers,
      reads = Some(reads))

}
