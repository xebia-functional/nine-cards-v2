package com.fortysevendeg.ninecardslauncher.api.version1.integration

import com.fortysevendeg.ninecardslauncher.api.version1.model.{AdsRequest, RecommendationRequest, ShareCollection}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

trait ApiServiceHelper {

  val fakeBaseUrl = "http://localhost:9999"

  val serviceOkHttpClient = new ServiceClient(new OkHttpClient, fakeBaseUrl)

  def createShareCollection(
    sharedCollectionId: Option[String] = None,
    description: String = "",
    author: String = "",
    name: String = "",
    packages: Seq[String] = Seq.empty,
    category: String = "",
    icon: String = "",
    community: Boolean = false): ShareCollection =
    ShareCollection(
      sharedCollectionId = sharedCollectionId,
      description = description,
      author = author,
      name = name,
      packages = packages,
      category = category,
      icon = icon,
      community = community)

  def createAdsRequest(
    userAgentHeader: String = "",
    sessionId: String = "",
    ipAddress: String = "",
    siteId: String = "",
    placementId: String = "",
    totalCampaignsRequested: String = "",
    adTypeId: String = "",
    categoryId: String = "",
    androidId: String = "",
    aIdSHA1: String = "",
    aIdMD5: String = "",
    idfa: String = "",
    macAddress: String = "",
    campaignId: String = ""): AdsRequest =
    AdsRequest(
      userAgentHeader = userAgentHeader,
      sessionId = sessionId,
      ipAddress = ipAddress,
      siteId = siteId,
      placementId = placementId,
      totalCampaignsRequested = totalCampaignsRequested,
      adTypeId = adTypeId,
      categoryId = categoryId,
      androidId = androidId,
      aIdSHA1: String,
      aIdMD5: String,
      idfa = idfa,
      macAddress = macAddress,
      campaignId = campaignId)


  def createRecommendationRequest(
    collectionId: Option[String] = None,
    categories: Seq[String] = Seq.empty,
    adPresenceRatio: Double = 0.0,
    likePackages: Seq[String] = Seq.empty,
    excludePackages: Seq[String] = Seq.empty,
    limit: Int = 10,
    adsRequest: Option[AdsRequest] = Some(createAdsRequest())): RecommendationRequest =
    RecommendationRequest(
      collectionId = collectionId,
      categories = categories,
      adPresenceRatio = adPresenceRatio,
      likePackages = likePackages,
      excludePackages = excludePackages,
      limit = limit,
      adsRequest = adsRequest)

}

object ApiServiceHelper extends ApiServiceHelper