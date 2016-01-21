package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.api.model.{SharedCollectionPackage, SharedCollection, GooglePlayRecommendation}
import com.fortysevendeg.ninecardslauncher.api.{model => apiModel}
import com.fortysevendeg.ninecardslauncher.services.api.models._
import play.api.libs.json._

trait Conversions {

  def toUser(
    email: String,
    device: GoogleDevice
    ): apiModel.User =
    apiModel.User(
      _id = None,
      email = None,
      sessionToken = None,
      username = None,
      password = None,
      authData = Some(apiModel.AuthData(
        google = Some(apiModel.AuthGoogle(
          email = email,
          devices = List(fromGoogleDevice(device))
        )),
        facebook = None,
        twitter = None,
        anonymous = None)))

  def fromGoogleDevice(device: GoogleDevice): apiModel.AuthGoogleDevice =
    apiModel.AuthGoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toUser(user: apiModel.User): User =
    User(
      id = user._id,
      sessionToken = user.sessionToken,
      email = user.email,
      devices = (for {
        data <- user.authData
        google <- data.google
      } yield toGoogleDeviceSeq(google.devices)) getOrElse Seq.empty)

  def toGoogleDeviceSeq(devices: Seq[apiModel.AuthGoogleDevice]): Seq[GoogleDevice] = devices map toGoogleDevice

  def toGoogleDevice(device: apiModel.AuthGoogleDevice): GoogleDevice =
    GoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toAuthData(
    email: String,
    devices: Seq[GoogleDevice]
    ): apiModel.AuthData =
    apiModel.AuthData(
      google = Some(apiModel.AuthGoogle(
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
    ): apiModel.Installation =
    apiModel.Installation(
      _id = id,
      deviceType = deviceType map (_.paramValue),
      deviceToken = deviceToken,
      userId = userId)

  def toInstallation(installation: apiModel.Installation): Installation =
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

  def toGooglePlayPackageSeq(googlePlayPackages: Seq[apiModel.GooglePlayPackage]): Seq[GooglePlayPackage] =
    googlePlayPackages map toGooglePlayPackage

  def toGooglePlayPackage(googlePlayPackage: apiModel.GooglePlayPackage): GooglePlayPackage =
    GooglePlayPackage(
      app = toGooglePlayApp(googlePlayPackage.docV2))

  def toGooglePlayApp(googlePlayApp: apiModel.GooglePlayApp): GooglePlayApp =
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

  def getIcon(images: Seq[apiModel.GooglePlayImage]): Option[String] =
    images.find(_.imageType == iconImageType) map (_.imageUrl)

  def getBackground(images: Seq[apiModel.GooglePlayImage]): Option[String] =
    images.find(_.imageType == iconBackgroundType) map (_.imageUrl)

  def getScreenShoots(images: Seq[apiModel.GooglePlayImage]): Seq[String] =
    images.filter(_.imageType == iconScreenShootType) map (_.imageUrl)

  def getVideo(images: Seq[apiModel.GooglePlayImage]): Option[String] =
    images.find(_.imageType == iconVideoType) map (_.imageUrl)

  def toGooglePlayImage(googlePlayImage: apiModel.GooglePlayImage): GooglePlayImage =
    GooglePlayImage(
      imageType = googlePlayImage.imageType,
      imageUrl = googlePlayImage.imageUrl,
      creator = googlePlayImage.creator)

  def toGooglePlayOffer(googlePlayOffer: apiModel.GooglePlayOffer): GooglePlayOffer =
    GooglePlayOffer(
      formattedAmount = googlePlayOffer.formattedAmount,
      micros = googlePlayOffer.micros)

  def toGooglePlayAggregateRating(googlePlayAggregateRating: apiModel.GooglePlayAggregateRating): GooglePlayAggregateRating =
    GooglePlayAggregateRating(
      ratingsCount = googlePlayAggregateRating.ratingsCount,
      commentCount = googlePlayAggregateRating.commentCount,
      oneStarRatings = googlePlayAggregateRating.oneStarRatings,
      twoStarRatings = googlePlayAggregateRating.twoStarRatings,
      threeStarRatings = googlePlayAggregateRating.threeStarRatings,
      fourStarRatings = googlePlayAggregateRating.fourStarRatings,
      fiveStarRatings = googlePlayAggregateRating.fiveStarRatings,
      starRating = googlePlayAggregateRating.starRating)

  def toGooglePlayDetails(googlePlayDetails: apiModel.GooglePlayDetails): GooglePlayDetails =
    GooglePlayDetails(appDetails = toGooglePlayAppDetails(googlePlayDetails.appDetails))

  def toGooglePlayAppDetails(googlePlayAppDetails: apiModel.GooglePlayAppDetails): GooglePlayAppDetails =
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

  def toGooglePlaySimplePackages(googlePlaySimplePackages: apiModel.GooglePlaySimplePackages): GooglePlaySimplePackages =
    GooglePlaySimplePackages(
      errors = googlePlaySimplePackages.errors,
      items = googlePlaySimplePackages.items map toGooglePlaySimplePackage
    )

  def toGooglePlaySimplePackage(googlePlaySimplePackage: apiModel.GooglePlaySimplePackage): GooglePlaySimplePackage =
    GooglePlaySimplePackage(
      packageName = googlePlaySimplePackage.packageName,
      appType = googlePlaySimplePackage.appType,
      appCategory = googlePlaySimplePackage.appCategory,
      numDownloads = googlePlaySimplePackage.numDownloads,
      starRating = googlePlaySimplePackage.starRating,
      ratingCount = googlePlaySimplePackage.ratingCount,
      commentCount = googlePlaySimplePackage.commentCount
    )

  def toUserConfig(apiUserConfig: apiModel.UserConfig): UserConfig =
    UserConfig(
      _id = apiUserConfig._id,
      email = apiUserConfig.email,
      plusProfile = toUserConfigPlusProfile(apiUserConfig.plusProfile),
      devices = apiUserConfig.devices map toUserConfigDevice,
      geoInfo = toUserConfigGeoInfo(apiUserConfig.geoInfo),
      status = toUserConfigStatusInfo(apiUserConfig.status))

  def toUserConfigPlusProfile(apiPlusProfile: apiModel.UserConfigPlusProfile): UserConfigPlusProfile =
    UserConfigPlusProfile(
      displayName = apiPlusProfile.displayName,
      profileImage = toUserConfigProfileImage(apiPlusProfile.profileImage))

  def toUserConfigProfileImage(apiProfileImage: apiModel.UserConfigProfileImage): UserConfigProfileImage =
    UserConfigProfileImage(
      imageType = apiProfileImage.imageType,
      imageUrl = apiProfileImage.imageUrl)

  def toUserConfigDevice(apiDevice: apiModel.UserConfigDevice): UserConfigDevice =
    UserConfigDevice(
      deviceId = apiDevice.deviceId,
      deviceName = apiDevice.deviceName,
      collections = apiDevice.collections map toUserConfigCollection)

  def toUserConfigCollection(apiCollection: apiModel.UserConfigCollection): UserConfigCollection =
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

  def toUserConfigCollectionItem(apiCollectionItem: apiModel.UserConfigCollectionItem): UserConfigCollectionItem =
    UserConfigCollectionItem(
      itemType = apiCollectionItem.itemType,
      title = apiCollectionItem.title,
      metadata = apiCollectionItem.metadata,
      categories = apiCollectionItem.categories)

  def toUserConfigGeoInfo(apiGeoInfo: apiModel.UserConfigGeoInfo): UserConfigGeoInfo =
    UserConfigGeoInfo(
      homeMorning = apiGeoInfo.homeMorning map toUserConfigUserLocation,
      homeNight = apiGeoInfo.homeNight map toUserConfigUserLocation,
      work = apiGeoInfo.work map toUserConfigUserLocation,
      current = apiGeoInfo.current map toUserConfigUserLocation)

  def toUserConfigUserLocation(apiUserLocation: apiModel.UserConfigUserLocation): UserConfigUserLocation =
    UserConfigUserLocation(
      wifi = apiUserLocation.wifi,
      lat = apiUserLocation.lat,
      lng = apiUserLocation.lng,
      occurrence = apiUserLocation.occurrence map toUserConfigTimeSlot)

  def toUserConfigTimeSlot(apiTimeSlot: apiModel.UserConfigTimeSlot): UserConfigTimeSlot =
    UserConfigTimeSlot(
      from = apiTimeSlot.from,
      to = apiTimeSlot.to,
      days = apiTimeSlot.days)

  def toUserConfigStatusInfo(apiStatusInfo: apiModel.UserConfigStatusInfo): UserConfigStatusInfo =
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

  def toConfigDevice(device: UserConfigDevice): apiModel.UserConfigDevice =
    apiModel.UserConfigDevice(
      deviceId = device.deviceId,
      deviceName = device.deviceName,
      collections = device.collections map fromUserConfigCollection)

  def fromUserConfigCollection(collection: UserConfigCollection): apiModel.UserConfigCollection =
    apiModel.UserConfigCollection(
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

  def fromUserConfigCollectionItem(collectionItem: UserConfigCollectionItem): apiModel.UserConfigCollectionItem =
    apiModel.UserConfigCollectionItem(
      itemType = collectionItem.itemType,
      title = collectionItem.title,
      metadata = Json.parse("{\"name\": \"test\"}"), // TODO Create metadata for item
      categories = collectionItem.categories)

  def toUserConfigGeoInfo(apiGeoInfo: UserConfigGeoInfo): apiModel.UserConfigGeoInfo =
    apiModel.UserConfigGeoInfo(
      homeMorning = apiGeoInfo.homeMorning map fromUserConfigUserLocation,
      homeNight = apiGeoInfo.homeNight map fromUserConfigUserLocation,
      work = apiGeoInfo.work map fromUserConfigUserLocation,
      current = apiGeoInfo.current map fromUserConfigUserLocation)

  def fromUserConfigUserLocation(apiUserLocation: UserConfigUserLocation): apiModel.UserConfigUserLocation =
    apiModel.UserConfigUserLocation(
      wifi = apiUserLocation.wifi,
      lat = apiUserLocation.lat,
      lng = apiUserLocation.lng,
      occurrence = apiUserLocation.occurrence map fromUserConfigTimeSlot)

  def fromUserConfigTimeSlot(apiTimeSlot: UserConfigTimeSlot): apiModel.UserConfigTimeSlot =
    apiModel.UserConfigTimeSlot(
      from = apiTimeSlot.from,
      to = apiTimeSlot.to,
      days = apiTimeSlot.days)

  def toRecommendationRequest(
    categories: Seq[String],
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int): apiModel.RecommendationRequest =
    apiModel.RecommendationRequest(
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
    community: Boolean): apiModel.ShareCollection =
    apiModel.ShareCollection(
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
      icon = sharedCollection.icon,
      community = sharedCollection.community
    )
}
