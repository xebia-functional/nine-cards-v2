package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.api.version1.model.{GooglePlayRecommendation, SharedCollection, AuthData => ApiAuthData, AuthGoogle => ApiAuthGoogle, AuthGoogleDevice => ApiAuthGoogleDevice, GooglePlayAggregateRating => ApiGooglePlayAggregateRating, GooglePlayApp => ApiGooglePlayApp, GooglePlayAppDetails => ApiGooglePlayAppDetails, GooglePlayDetails => ApiGooglePlayDetails, GooglePlayImage => ApiGooglePlayImage, GooglePlayOffer => ApiGooglePlayOffer, Installation => ApiInstallation, RecommendationRequest => ApiRecommendationRequest, ShareCollection => ApiShareCollection, User => ApiUser, UserConfig => ApiUserConfig, UserConfigCollection => ApiUserConfigCollection, UserConfigCollectionItem => ApiUserConfigCollectionItem, UserConfigDevice => ApiUserConfigDevice, UserConfigPlusProfile => ApiUserConfigPlusProfile, UserConfigProfileImage => ApiUserConfigProfileImage, UserConfigStatusInfo => ApiUserConfigStatusInfo, UserConfigTimeSlot => ApiUserConfigTimeSlot, UserConfigUserLocation => ApiUserConfigUserLocation}
import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import org.joda.time.format.DateTimeFormat

import scala.util.{Success, Try}

trait Conversions {

  def toUser(
    email: String,
    device: GoogleDevice
    ): ApiUser =
    ApiUser(
      _id = None,
      email = None,
      sessionToken = None,
      username = None,
      password = None,
      authData = Some(ApiAuthData(
        google = Some(ApiAuthGoogle(
          email = email,
          devices = List(fromGoogleDevice(device))
        )),
        facebook = None,
        twitter = None,
        anonymous = None)))

  def fromGoogleDevice(device: GoogleDevice): ApiAuthGoogleDevice =
    ApiAuthGoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toLoginResponseV1(statusCode: Int, user: ApiUser): LoginResponseV1 =
    LoginResponseV1(
      statusCode,
      userId = user._id,
      sessionToken = user.sessionToken,
      email = user.email,
      devices = (for {
        data <- user.authData
        google <- data.google
      } yield toGoogleDeviceSeq(google.devices)) getOrElse Seq.empty)

  def toGoogleDeviceSeq(devices: Seq[ApiAuthGoogleDevice]): Seq[GoogleDevice] = devices map toGoogleDevice

  def toGoogleDevice(device: ApiAuthGoogleDevice): GoogleDevice =
    GoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toInstallation(
    id: Option[String],
    deviceType: Option[DeviceType],
    deviceToken: Option[String],
    userId: Option[String]
    ): ApiInstallation =
    ApiInstallation(
      _id = id,
      deviceType = deviceType map (_.paramValue),
      deviceToken = deviceToken,
      userId = userId)

  def toInstallation(installation: ApiInstallation): Installation =
    Installation(
      id = installation._id,
      deviceType = installation.deviceType flatMap parseDeviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId)

  def parseDeviceType(deviceType: String): Option[DeviceType] =
    deviceType match {
      case AndroidDevice.paramValue => Some(AndroidDevice)
      case _ => None
    }

  def toGooglePlayApp(googlePlayApp: ApiGooglePlayApp): GooglePlayApp =
    GooglePlayApp(
      docid = googlePlayApp.docid,
      title = googlePlayApp.title,
      creator = googlePlayApp.creator,
      descriptionHtml = googlePlayApp.descriptionHtml,
      icon = getIcon(googlePlayApp.image),
      background = getBackground(googlePlayApp.image),
      screenshots = getScreenShoots(googlePlayApp.image),
      video = getVideo(googlePlayApp.image),
      details = toGooglePlayDetails(googlePlayApp.details),
      offer = googlePlayApp.offer map toGooglePlayOffer,
      aggregateRating = toGooglePlayAggregateRating(googlePlayApp.aggregateRating))

  def toCategorizedPackage(packageName: String, categorizeResponse: version2.CategorizeResponse): CategorizedPackage =
    CategorizedPackage(packageName, categorizeResponse.items.find(_.packageName == packageName).map(_.category))

  def toCategorizedPackages(categorizeResponse: version2.CategorizeResponse): Seq[CategorizedPackage] =
    categorizeResponse.items.map(app => CategorizedPackage(app.packageName, Some(app.category)))

  val iconImageType = 4

  val iconBackgroundType = 2

  val iconScreenShootType = 1

  val iconVideoType = 3

  def getIcon(images: Seq[ApiGooglePlayImage]): Option[String] =
    images.find(_.imageType == iconImageType) map (_.imageUrl)

  def getBackground(images: Seq[ApiGooglePlayImage]): Option[String] =
    images.find(_.imageType == iconBackgroundType) map (_.imageUrl)

  def getScreenShoots(images: Seq[ApiGooglePlayImage]): Seq[String] =
    images.filter(_.imageType == iconScreenShootType) map (_.imageUrl)

  def getVideo(images: Seq[ApiGooglePlayImage]): Option[String] =
    images.find(_.imageType == iconVideoType) map (_.imageUrl)

  def toGooglePlayOffer(googlePlayOffer: ApiGooglePlayOffer): GooglePlayOffer =
    GooglePlayOffer(
      formattedAmount = googlePlayOffer.formattedAmount,
      micros = googlePlayOffer.micros)

  def toGooglePlayAggregateRating(googlePlayAggregateRating: ApiGooglePlayAggregateRating): GooglePlayAggregateRating =
    GooglePlayAggregateRating(
      ratingsCount = googlePlayAggregateRating.ratingsCount,
      commentCount = googlePlayAggregateRating.commentCount,
      oneStarRatings = googlePlayAggregateRating.oneStarRatings,
      twoStarRatings = googlePlayAggregateRating.twoStarRatings,
      threeStarRatings = googlePlayAggregateRating.threeStarRatings,
      fourStarRatings = googlePlayAggregateRating.fourStarRatings,
      fiveStarRatings = googlePlayAggregateRating.fiveStarRatings,
      starRating = googlePlayAggregateRating.starRating)

  def toGooglePlayDetails(googlePlayDetails: ApiGooglePlayDetails): GooglePlayDetails =
    GooglePlayDetails(appDetails = toGooglePlayAppDetails(googlePlayDetails.appDetails))

  def toGooglePlayAppDetails(googlePlayAppDetails: ApiGooglePlayAppDetails): GooglePlayAppDetails =
    GooglePlayAppDetails(
      appCategory = googlePlayAppDetails.appCategory,
      numDownloads = googlePlayAppDetails.numDownloads,
      developerEmail = googlePlayAppDetails.developerEmail,
      developerName = googlePlayAppDetails.developerName,
      developerWebsite = googlePlayAppDetails.developerWebsite,
      versionCode = googlePlayAppDetails.versionCode,
      versionString = googlePlayAppDetails.versionString,
      appType = googlePlayAppDetails.appType,
      permission = googlePlayAppDetails.permission)

  def toUserConfig(apiUserConfig: ApiUserConfig): UserConfig =
    UserConfig(
      _id = apiUserConfig._id,
      email = apiUserConfig.email,
      plusProfile = toUserConfigPlusProfile(apiUserConfig.plusProfile),
      devices = apiUserConfig.devices map toUserConfigDevice,
      status = toUserConfigStatusInfo(apiUserConfig.status))

  def toUserConfigPlusProfile(apiPlusProfile: ApiUserConfigPlusProfile): UserConfigPlusProfile =
    UserConfigPlusProfile(
      displayName = apiPlusProfile.displayName,
      profileImage = toUserConfigProfileImage(apiPlusProfile.profileImage))

  def toUserConfigProfileImage(apiProfileImage: ApiUserConfigProfileImage): UserConfigProfileImage =
    UserConfigProfileImage(
      imageType = apiProfileImage.imageType,
      imageUrl = apiProfileImage.imageUrl)

  def toUserConfigDevice(apiDevice: ApiUserConfigDevice): UserConfigDevice =
    UserConfigDevice(
      deviceId = apiDevice.deviceId,
      deviceName = apiDevice.deviceName,
      collections = apiDevice.collections map toUserConfigCollection)

  def toUserConfigCollection(apiCollection: ApiUserConfigCollection): UserConfigCollection =
    UserConfigCollection(
      name = apiCollection.name,
      originalSharedCollectionId = apiCollection.originalSharedCollectionId,
      sharedCollectionId = apiCollection.sharedCollectionId,
      sharedCollectionSubscribed = apiCollection.sharedCollectionSubscribed,
      items = apiCollection.items map toUserConfigCollectionItem,
      collectionType = apiCollection.collectionType,
      constrains = apiCollection.constrains,
      wifi = apiCollection.wifi,
      occurrence = apiCollection.occurrence,
      icon = apiCollection.icon,
      radius = apiCollection.radius,
      lat = apiCollection.lat,
      lng = apiCollection.lng,
      alt = apiCollection.alt,
      category = apiCollection.category)

  def toUserConfigCollectionItem(apiCollectionItem: ApiUserConfigCollectionItem): UserConfigCollectionItem =
    UserConfigCollectionItem(
      itemType = apiCollectionItem.itemType,
      title = apiCollectionItem.title,
      metadata = apiCollectionItem.metadata,
      categories = apiCollectionItem.categories)

  def toUserConfigUserLocation(apiUserLocation: ApiUserConfigUserLocation): UserConfigUserLocation =
    UserConfigUserLocation(
      wifi = apiUserLocation.wifi,
      lat = apiUserLocation.lat,
      lng = apiUserLocation.lng,
      occurrence = apiUserLocation.occurrence map toUserConfigTimeSlot)

  def toUserConfigTimeSlot(apiTimeSlot: ApiUserConfigTimeSlot): UserConfigTimeSlot =
    UserConfigTimeSlot(
      from = apiTimeSlot.from,
      to = apiTimeSlot.to,
      days = apiTimeSlot.days)

  def toUserConfigStatusInfo(apiStatusInfo: ApiUserConfigStatusInfo): UserConfigStatusInfo =
    UserConfigStatusInfo(
      products = apiStatusInfo.products,
      friendsReferred = apiStatusInfo.friendsReferred,
      themesShared = apiStatusInfo.themesShared,
      collectionsShared = apiStatusInfo.collectionsShared,
      customCollections = apiStatusInfo.customCollections,
      earlyAdopter = apiStatusInfo.earlyAdopter,
      communityMember = apiStatusInfo.communityMember,
      joinedThrough = apiStatusInfo.joinedThrough,
      tester = apiStatusInfo.tester)

  def toRecommendationRequest(
    categories: Seq[String],
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int): ApiRecommendationRequest =
    ApiRecommendationRequest(
      collectionId = None,
      categories = categories,
      adPresenceRatio = 0.0,
      likePackages = likePackages,
      excludePackages = excludePackages,
      limit = limit,
      adsRequest = None)

  def toPlayAppSeq(recommendation: GooglePlayRecommendation): Seq[GooglePlayApp] =
    recommendation.items map (item => toGooglePlayApp(item.app))

  def toSharedCollectionResponseSeq(collections: Seq[version2.Collection]): Seq[SharedCollectionResponse] =
    collections map toSharedCollectionResponse

  def formatPublishedDate(date: String): Long = {

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    def cleanString: String = date.replaceAll("\"", "") match {
      case s if s.matches(".+\\.\\d{3}000") => s.substring(0, s.length - 3)
      case s => s
    }

    Try(formatter.withZoneUTC().parseDateTime(cleanString)) match {
      case Success(d) => d.getMillis
      case _ => 0
    }
  }

  def toSharedCollectionResponse(collection: version2.Collection) =
    SharedCollectionResponse(
      id = collection.publicIdentifier,
      sharedCollectionId = collection.publicIdentifier,
      publishedOn = formatPublishedDate(collection.publishedOn),
      description = collection.description getOrElse "",
      author = collection.author,
      name = collection.name,
      packages = collection.packages,
      resolvedPackages = toSharedCollectionPackageResponseSeq(collection.appsInfo),
      views = collection.views getOrElse 0,
      category = collection.category,
      icon = collection.icon,
      community = collection.community)

  def toSharedCollectionPackageResponseSeq(packages: Seq[version2.CollectionApp]): Seq[SharedCollectionPackageResponse] =
    packages map toSharedCollectionPackageResponse

  def toSharedCollectionPackageResponse(item: version2.CollectionApp): SharedCollectionPackageResponse =
    SharedCollectionPackageResponse(
      packageName = item.packageName,
      title = item.title,
      icon = item.icon,
      stars = item.stars,
      downloads = item.downloads,
      free = item.free)

  def toShareCollection(
    description: String,
    author: String,
    name: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean): ApiShareCollection =
    ApiShareCollection(
      sharedCollectionId = None,
      description = description,
      author = author,
      name = name,
      packages = packages,
      category = category,
      icon = icon,
      community = community
    )

  def toCreateSharedCollection(sharedCollection: SharedCollection): CreateSharedCollection =
    CreateSharedCollection(
      name = sharedCollection.name,
      description = sharedCollection.description,
      author = sharedCollection.author,
      packages = sharedCollection.packages,
      category = sharedCollection.category,
      sharedCollectionId = sharedCollection.sharedCollectionId,
      icon = sharedCollection.icon,
      community = sharedCollection.community
    )
}
