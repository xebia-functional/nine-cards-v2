package com.fortysevendeg.ninecardslauncher.di

import com.fortysevendeg.ninecardslauncher.api.services._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

trait ApiModule {

  def createServiceClient(baseUrl: String): ServiceClient =
    new ServiceClient(new OkHttpClient, baseUrl)

  def createApiUserService(serviceClient: ServiceClient) =
    new ApiUserService(serviceClient)

  def createApiUserConfigService(serviceClient: ServiceClient) =
    new ApiUserConfigService(serviceClient)

  def createApiSharedCollectionsService(serviceClient: ServiceClient) =
    new ApiSharedCollectionsService(serviceClient)

  def createApiRecommendationService(serviceClient: ServiceClient) =
    new ApiRecommendationService(serviceClient)

  def createApiGooglePlayService(serviceClient: ServiceClient) =
    new ApiGooglePlayService(serviceClient)

}
