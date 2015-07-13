package com.fortysevendeg.ninecardslauncher.api.integration

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient

trait ApiServiceHelper {

  val fakeBaseUrl = "http://localhost:9999"

  val serviceOkHttpClient = new ServiceClient(new OkHttpClient, fakeBaseUrl)

  def createUserConfigDevice(
    deviceId: String = "",
    deviceName: String = "",
    collections: Seq[UserConfigCollection] = Seq.empty) =
    UserConfigDevice(
      deviceId = deviceId,
      deviceName = deviceName,
      collections = collections)

  def createUserConfigGeoInfo(
    homeMorning: Option[UserConfigUserLocation] = None,
    homeNight: Option[UserConfigUserLocation] = None,
    work: Option[UserConfigUserLocation] = None,
    current: Option[UserConfigUserLocation] = None) =
    UserConfigGeoInfo(
      homeMorning = homeMorning,
      homeNight = homeNight,
      work = work,
      current = current)

  def createSharedCollection(
    _id: String = "",
    sharedCollectionId: String = "",
    publishedOn: Long = 0,
    description: String = "",
    screenshots: Seq[AssetResponse] = Seq.empty,
    author: String = "",
    tags: Seq[String] = Seq.empty,
    name: String = "",
    shareLink: String = "",
    packages: Seq[String] = Seq.empty,
    resolvedPackages: Seq[SharedCollectionPackage] = Seq.empty,
    occurrence: Seq[UserConfigTimeSlot] = Seq.empty,
    lat: Double = 0.0,
    lng: Double = 0.0,
    alt: Double = 0.0,
    views: Int = 1,
    category: String = "",
    icon: String = "",
    community: Boolean = false): SharedCollection =
    SharedCollection(_id = _id,
      sharedCollectionId = sharedCollectionId,
      publishedOn = publishedOn,
      description = description,
      screenshots = screenshots,
      author = author,
      tags = tags,
      name = name,
      shareLink = shareLink,
      packages = packages,
      resolvedPackages = resolvedPackages,
      occurrence = occurrence,
      lat = lat,
      lng = lng,
      alt = alt,
      views = views,
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
    collectionId: String = "",
    categories: Seq[String] = Seq.empty,
    adPresenceRatio: Double = 0.0,
    likePackages: Seq[String] = Seq.empty,
    excludePackages: Seq[String] = Seq.empty,
    limit: Int = 10,
    adsRequest: AdsRequest = createAdsRequest()): RecommendationRequest =
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