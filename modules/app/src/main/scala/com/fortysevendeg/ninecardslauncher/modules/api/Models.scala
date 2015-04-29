package com.fortysevendeg.ninecardslauncher.modules.api

case class User(
    id: Option[String],
    sessionToken: Option[String],
    email: Option[String],
    devices: Seq[GoogleDevice])

case class GoogleDevice(
    name: String,
    devideId: String,
    secretToken: String,
    permissions: Seq[String])

case class GooglePlayApp(
    docid: String,
    title: String,
    creator: String,
    descriptionHtml: Option[String],
    image: Seq[GooglePlayImage],
    details: GooglePlayDetails,
    offer: Seq[GooglePlayOffer],
    aggregateRating: GooglePlayAggregateRating)

case class GooglePlayImage(
    imageType: Int,
    imageUrl: String,
    creator: Option[String])

case class GooglePlayDetails(
    appDetails: GooglePlayAppDetails)

case class GooglePlayAppDetails(
    appCategory: Seq[String],
    numDownloads: String,
    developerEmail: Option[String],
    developerName: Option[String],
    developerWebsite: Option[String],
    versionCode: Int,
    versionString: Option[String],
    appType: Option[String],
    permission: Seq[String])

case class GooglePlayOffer(
    formattedAmount: String,
    micros: Long)

case class GooglePlayAggregateRating(
    ratingsCount: Int,
    commentCount: Option[Int],
    oneStarRatings: Int,
    twoStarRatings: Int,
    threeStarRatings: Int,
    fourStarRatings: Int,
    fiveStarRatings: Int,
    starRating: Double)

case class GooglePlaySimplePackages(
    errors: Seq[String],
    items: Seq[GooglePlaySimplePackage])

case class GooglePlaySimplePackage(
    packageName: String,
    appType: String,
    appCategory: String,
    numDownloads: String,
    starRating: Double,
    ratingCount: Int,
    commentCount: Int)

case class UserConfig(
    _id: String,
    email: String,
    plusProfile: UserConfigPlusProfile,
    devices: Seq[UserConfigDevice],
    geoInfo: UserConfigGeoInfo,
    status: UserConfigStatusInfo)

case class UserConfigPlusProfile(
    displayName: String,
    profileImage: UserConfigProfileImage)

case class UserConfigDevice(
    deviceId: String,
    deviceName: String,
    collections: Seq[UserConfigCollection])

case class UserConfigGeoInfo(
    homeMorning: Option[UserConfigUserLocation],
    homeNight: Option[UserConfigUserLocation],
    work: Option[UserConfigUserLocation],
    current: Option[UserConfigUserLocation])

case class UserConfigStatusInfo(
    products: Seq[String],
    friendsReferred: Int,
    themesShared: Int,
    collectionsShared: Int,
    customCollections: Int,
    earlyAdopter: Boolean,
    communityMember: Boolean,
    joinedThrough: Option[String],
    tester: Boolean)

case class UserConfigProfileImage(
    imageType: Int,
    imageUrl: String,
    secureUrl: String)

case class UserConfigCollection(
    name: String,
    originalSharedCollectionId: Option[String],
    sharedCollectionId: Option[String],
    sharedCollectionSubscribed: Option[String],
    items: Seq[UserConfigCollectionItem],
    collectionType: String,
    constrains: Seq[String],
    wifi: Seq[String],
    occurrence: Seq[String],
    icon: String,
    radius: Int,
    lat: Double,
    lng: Double,
    alt: Double,
    category: Option[String])

case class UserConfigCollectionItem(
    itemType: String,
    title: String,
    metadata: NineCardIntent,
    categories: Option[Seq[String]])

case class NineCardIntent(
    action: String,
    className: Option[String],
    packageName: Option[String],
    dataExtra: Option[String],
    intentExtras: Map[String, String],
    categories: Option[Seq[String]])

case class UserConfigUserLocation(
    wifi: String,
    lat: Double,
    lng: Double,
    occurrence: Seq[UserConfigTimeSlot])

case class UserConfigTimeSlot(
    from: String,
    to: String,
    days: Seq[Int])