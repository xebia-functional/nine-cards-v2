package com.fortysevendeg.ninecardslauncher.api.model

case class RecommendationRequest(
    collectionId: Option[String],
    categories: Seq[String],
    adPresenceRatio: Double,
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int,
    adsRequest: Option[AdsRequest])

case class AdsRequest(
    userAgentHeader: String,
    sessionId: String,
    ipAddress: String,
    siteId: String,
    placementId: String,
    totalCampaignsRequested: String,
    adTypeId: String,
    categoryId: String,
    androidId: String,
    aIdSHA1: String,
    aIdMD5: String,
    idfa: String,
    macAddress: String,
    campaignId: String)

case class GooglePlayRecommendation(
    count: Int,
    items: Seq[GooglePlayRecommendationItems])

case class GooglePlayRecommendationItems(
    appId: String,
    app: GooglePlayApp,
    ad: Option[AppiaAd])

case class AppiaAd(
    clickProxyURL: String,
    impressionTrackingURL: String,
    bidRate: Double,
    rating: Double)

case class CollectionRecommendations(items: CollectionRecommendation)

case class CollectionRecommendation(
    _id: String,
    name: String,
    description: String,
    packages: Seq[String])

case class CollectionSponsored(items: Seq[UserConfigCollectionItem])
