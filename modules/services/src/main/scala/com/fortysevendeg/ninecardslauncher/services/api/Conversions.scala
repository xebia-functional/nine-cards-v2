package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.api.model.{AuthData => ApiAuthData, AuthGoogle => ApiAuthGoogle, AuthGoogleDevice => ApiAuthGoogleDevice, GooglePlayAggregateRating => ApiGooglePlayAggregateRating, GooglePlayApp => ApiGooglePlayApp, GooglePlayAppDetails => ApiGooglePlayAppDetails, GooglePlayDetails => ApiGooglePlayDetails, GooglePlayImage => ApiGooglePlayImage, GooglePlayOffer => ApiGooglePlayOffer, GooglePlayPackage => ApiGooglePlayPackage, GooglePlayRecommendation, Installation => ApiInstallation, RecommendationRequest => ApiRecommendationRequest, ShareCollection => ApiShareCollection, SharedCollection, SharedCollectionPackage, User => ApiUser, UserConfig => ApiUserConfig, UserConfigCollection => ApiUserConfigCollection, UserConfigCollectionItem => ApiUserConfigCollectionItem, UserConfigDevice => ApiUserConfigDevice, UserConfigPlusProfile => ApiUserConfigPlusProfile, UserConfigProfileImage => ApiUserConfigProfileImage, UserConfigStatusInfo => ApiUserConfigStatusInfo, UserConfigTimeSlot => ApiUserConfigTimeSlot, UserConfigUserLocation => ApiUserConfigUserLocation}
import com.fortysevendeg.ninecardslauncher.services.api.models._
import play.api.libs.json._

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

  def toUser(user: ApiUser): User =
    User(
      id = user._id,
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

  def toAuthData(
    email: String,
    devices: Seq[GoogleDevice]
    ): ApiAuthData =
    ApiAuthData(
      google = Some(ApiAuthGoogle(
        email = email,
        devices = devices map fromGoogleDevice
      )),
      facebook = None,
      twitter = None,
      anonymous = None)

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

  def toGooglePlayPackageSeq(googlePlayPackages: Seq[ApiGooglePlayPackage]): Seq[GooglePlayPackage] =
    googlePlayPackages map toGooglePlayPackage

  def toGooglePlayPackage(googlePlayPackage: ApiGooglePlayPackage): GooglePlayPackage =
    GooglePlayPackage(
      app = toGooglePlayApp(googlePlayPackage.docV2))

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

  def toGooglePlayImage(googlePlayImage: ApiGooglePlayImage): GooglePlayImage =
    GooglePlayImage(
      imageType = googlePlayImage.imageType,
      imageUrl = googlePlayImage.imageUrl,
      creator = googlePlayImage.creator)

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

  def toConfigDevice(device: UserConfigDevice): ApiUserConfigDevice =
    ApiUserConfigDevice(
      deviceId = device.deviceId,
      deviceName = device.deviceName,
      collections = device.collections map fromUserConfigCollection)

  def fromUserConfigCollection(collection: UserConfigCollection): ApiUserConfigCollection =
    ApiUserConfigCollection(
      name = collection.name,
      originalSharedCollectionId = collection.originalSharedCollectionId,
      sharedCollectionId = collection.sharedCollectionId,
      sharedCollectionSubscribed = collection.sharedCollectionSubscribed,
      items = collection.items map fromUserConfigCollectionItem,
      collectionType = collection.collectionType,
      constrains = collection.constrains,
      wifi = collection.wifi,
      occurrence = collection.occurrence,
      icon = collection.icon,
      radius = collection.radius,
      lat = collection.lat,
      lng = collection.lng,
      alt = collection.alt,
      category = collection.category)

  def fromUserConfigCollectionItem(collectionItem: UserConfigCollectionItem): ApiUserConfigCollectionItem =
    ApiUserConfigCollectionItem(
      itemType = collectionItem.itemType,
      title = collectionItem.title,
      metadata = Json.parse("{\"name\": \"test\"}"), // TODO Create metadata for item
      categories = collectionItem.categories)

  def fromUserConfigUserLocation(apiUserLocation: UserConfigUserLocation): ApiUserConfigUserLocation =
    ApiUserConfigUserLocation(
      wifi = apiUserLocation.wifi,
      lat = apiUserLocation.lat,
      lng = apiUserLocation.lng,
      occurrence = apiUserLocation.occurrence map fromUserConfigTimeSlot)

  def fromUserConfigTimeSlot(apiTimeSlot: UserConfigTimeSlot): ApiUserConfigTimeSlot =
    ApiUserConfigTimeSlot(
      from = apiTimeSlot.from,
      to = apiTimeSlot.to,
      days = apiTimeSlot.days)

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

  def toSharedCollectionResponseSeq(sharedCollections: Seq[SharedCollection]): Seq[SharedCollectionResponse] =
    sharedCollections map toSharedCollectionResponse

  def toSharedCollectionResponse(sharedCollection: SharedCollection) =
    SharedCollectionResponse(
      id = sharedCollection._id,
      sharedCollectionId = sharedCollection.sharedCollectionId,
      publishedOn = sharedCollection.publishedOn,
      description = sharedCollection.description,
      screenshots = sharedCollection.screenshots map (_.uri),
      author = sharedCollection.author,
      tags = sharedCollection.tags,
      name = sharedCollection.name,
      shareLink = sharedCollection.shareLink,
      packages = sharedCollection.packages,
      resolvedPackages = toSharedCollectionPackageResponseSeq(sharedCollection.resolvedPackages),
      views = sharedCollection.views,
      category = sharedCollection.category,
      icon = sharedCollection.icon,
      community = sharedCollection.community)

  def toSharedCollectionPackageResponseSeq(packages: Seq[SharedCollectionPackage]): Seq[SharedCollectionPackageResponse] =
    packages map toSharedCollectionPackageResponse

  def toSharedCollectionPackageResponse(item: SharedCollectionPackage): SharedCollectionPackageResponse =
    SharedCollectionPackageResponse(
      packageName = item.packageName,
      title = item.title,
      description = item.description,
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
      shareLink = sharedCollection.shareLink,
      sharedCollectionId = sharedCollection.sharedCollectionId,
      icon = sharedCollection.icon,
      community = sharedCollection.community
    )
}
