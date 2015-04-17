package com.fortysevendeg.ninecardslauncher.modules.api

import com.fortysevendeg.ninecardslauncher.api.{model => apiModel}

trait Conversions {

  def fromLoginRequest(login: LoginRequest): apiModel.User =
    apiModel.User(
      id = login.id,
      email = Some(login.email),
      sessionToken = login.sessionToken,
      username = None,
      password = None,
      authData = Some(apiModel.AuthData(
        google = Some(apiModel.AuthGoogle(
          email = login.email,
          devices = login.devices map fromGoogleDevice
        )),
        facebook = None,
        twitter = None,
        anonymous = None
      )))

  def fromGoogleDevice(device: GoogleDevice): apiModel.AuthGoogleDevice =
    apiModel.AuthGoogleDevice(
      name = device.name,
      deviceId = device.devideId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toUser(user: apiModel.User): User =
    User(
      id = user.id,
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
      devideId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def fromLinkGoogleAccountRequest(googleRequest: LinkGoogleAccountRequest): apiModel.AuthData =
    apiModel.AuthData(
      google = Some(apiModel.AuthGoogle(
        email = googleRequest.email,
        devices = googleRequest.devices map fromGoogleDevice
      )),
      facebook = None,
      twitter = None,
      anonymous = None)

  def fromInstallationRequest(installation: InstallationRequest): apiModel.Installation =
    apiModel.Installation(
      id = installation.id,
      deviceType = installation.deviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId,
      facebookId = None)

  def toInstallation(installation: apiModel.Installation): Installation =
    Installation(
      id = installation.id,
      deviceType = installation.deviceType,
      deviceToken = installation.deviceToken,
      userId = installation.userId
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
      imageUrl = apiProfileImage.imageUrl,
      secureUrl = apiProfileImage.secureUrl)

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
      metadata = toNineCardIntent(apiCollectionItem.metadata),
      categories = apiCollectionItem.categories)

  def toNineCardIntent(apiIntent: apiModel.NineCardIntent): NineCardIntent =
    NineCardIntent(
      action = apiIntent.action,
      className = apiIntent.className,
      packageName = apiIntent.packageName,
      dataExtra = apiIntent.dataExtra,
      intentExtras = apiIntent.intentExtras,
      categories = apiIntent.categories)

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

  def fromUserConfigDevice(device: UserConfigDevice): apiModel.UserConfigDevice =
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
      metadata = fromNineCardIntent(collectionItem.metadata),
      categories = collectionItem.categories)

  def fromNineCardIntent(apiIntent: NineCardIntent): apiModel.NineCardIntent =
    apiModel.NineCardIntent(
      action = apiIntent.action,
      className = apiIntent.className,
      packageName = apiIntent.packageName,
      dataExtra = apiIntent.dataExtra,
      intentExtras = apiIntent.intentExtras,
      categories = apiIntent.categories)

  def fromUserConfigGeoInfo(apiGeoInfo: UserConfigGeoInfo): apiModel.UserConfigGeoInfo =
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

}
