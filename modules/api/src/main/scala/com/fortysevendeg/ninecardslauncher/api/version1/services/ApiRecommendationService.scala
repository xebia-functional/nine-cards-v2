package com.fortysevendeg.ninecardslauncher.api.version1.services

import com.fortysevendeg.ninecardslauncher.api.version1.model._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiRecommendationService(serviceClient: ServiceClient) {

  private[this] val prefixRecommendation = "/collections"
  private[this] val recommendationsPath = "recommendations"
  private[this] val appsPath = "apps"

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

}
