package cards.nine.services.api.impl

import cards.nine.api.version1.{User, _}
import cards.nine.api.version2._
import cards.nine.commons.test.data.ApiV1Values._
import cards.nine.commons.test.data.ApiValues._
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.commons.test.data.UserV1Values._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.{NineCardsIntentConversions, PackagesByCategory}
import play.api.libs.json.Json

trait ApiServicesImplData extends NineCardsIntentConversions {

  def authGoogleDevice(num: Int = 0) = AuthGoogleDevice(
    name = userDeviceName + num,
    deviceId = userDeviceId + num,
    secretToken = marketToken,
    permissions = permissions)

  val authGoogleDevice: AuthGoogleDevice = authGoogleDevice(0)
  val seqAuthGoogleDevice: Seq[AuthGoogleDevice] = Seq(authGoogleDevice(0), authGoogleDevice(1), authGoogleDevice(2))

  def authGoogle(num: Int = 0) = AuthGoogle(
    email = email,
    devices = seqAuthGoogleDevice)

  val authGoogle: AuthGoogle = authGoogle(0)

  def authFacebook(num: Int = 0) = AuthFacebook (
    id = authFacebookId,
    accessToken = authFacebookAccessToken,
    expirationDate = authFacebookExpirationDate)

  val authFacebook: AuthFacebook = authFacebook(0)

  def authTwitter(num: Int = 0) = AuthTwitter (
    id = authTwitterId,
    screenName = authTwitterScreenName,
    consumerKey = authTwitterConsumerKey,
    consumerSecret = authTwitterConsumerSecret,
    authToken = authTwitterAuthToken,
    authTokenSecret = authTwitterAuthTokenSecret,
    key = authTwitterKey,
    secretKey = authTwitterSecretKey)

  val authTwitter: AuthTwitter = authTwitter(0)

  def authAnonymous(num: Int = 0) = AuthAnonymous (
    id = authAnonymousId)

  val authAnonymous: AuthAnonymous = authAnonymous(0)

  def authData(num: Int = 0) = AuthData(
    google = Some(authGoogle),
    facebook = Option(authFacebook),
    twitter = Option(authTwitter),
    anonymous = Option(authAnonymous))

  val authData: AuthData = authData(0)

  def apiUserV1(num: Int = 0) = User(
    _id = Option(userId.toString),
    sessionToken = Option(sessionToken),
    email = Option(email),
    username = Option(userV1Name),
    password = Option(userV1Password),
    authData = Option(authData))

  val apiUserV1: User = apiUserV1(0)

  val loginV1User = User(
    _id = None,
    email = None,
    sessionToken = None,
    username = None,
    password = None,
    authData = Option(authData.copy(
      google = Option(authGoogle.copy(devices = Seq(authGoogleDevice))),
      facebook = None,
      twitter = None,
      anonymous = None)))

  def userConfigProfileImage = UserConfigProfileImage(
    imageType = userConfigPlusImageType,
    imageUrl = imageUrl,
    secureUrl = Option(userConfigPlusSecureUrl))

  def userConfigPlusProfile = UserConfigPlusProfile(
    displayName = displayName,
    profileImage = userConfigProfileImage)

  def userConfigCollectionItem(num: Int = 0) = UserConfigCollectionItem(
    itemType = itemType.name,
    title = title + num,
    metadata = Json.parse(intent),
    categories = Option(Seq(category.name, anotherCategory.name)))

  val seqUserConfigCollectionItem: Seq[UserConfigCollectionItem] = Seq(userConfigCollectionItem(0), userConfigCollectionItem(1), userConfigCollectionItem(2))

  def userConfigCollection(num: Int = 0) = UserConfigCollection(
    name = collectionName,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
    items = seqUserConfigCollectionItem,
    collectionType = collectionType.name,
    constrains = constrains,
    wifi = wifiSeq,
    occurrence = occurrence,
    icon = apiV1CollectionIcon,
    radius = userV1Radius,
    lat = userV1Latitude,
    lng = userV1Longitude,
    alt = userV1Altitude,
    category = Option(category.name))

  val seqUserConfigCollection: Seq[UserConfigCollection] = Seq(userConfigCollection(0), userConfigCollection(1), userConfigCollection(2))

  def userConfigDevice(num: Int = 0) = UserConfigDevice(
    deviceId = deviceIdPrefix + num,
    deviceName = userDeviceName + num,
    collections = seqUserConfigCollection)

  val seqUserConfigDevice: Seq[UserConfigDevice] = Seq(userConfigDevice(0), userConfigDevice(1), userConfigDevice(2))

  def userConfigGeoInfo = UserConfigGeoInfo(
    homeMorning = None,
    homeNight = None,
    work = None,
    current = None)

  def userConfigStatusInfo = UserConfigStatusInfo(
    products = products,
    friendsReferred = friendsReferred,
    themesShared = themesShared,
    collectionsShared = collectionsShared,
    customCollections = customCollections,
    earlyAdopter = earlyAdopter,
    communityMember = communityMember,
    joinedThrough = joinedThrough,
    tester = tester)

  def userConfig(num: Int = 0) = UserConfig(
    _id = userV1Id,
    email = email,
    plusProfile = userConfigPlusProfile,
    devices = seqUserConfigDevice,
    geoInfo = userConfigGeoInfo,
    status = userConfigStatusInfo)

  val userConfig: UserConfig = userConfig(0)

  def categorizedApp(num: Int = 0) = CategorizedApp(
    packageName = applicationPackageName + num,
    category = categoryStr)

  val categorizedApp: CategorizedApp = categorizedApp(0)
  val seqCategorizedApp: Seq[CategorizedApp] = Seq(categorizedApp(0), categorizedApp(1), categorizedApp(2))

  def categorizedAppDetail(num: Int = 0) = CategorizedAppDetail(
    packageName = applicationPackageName + num,
    title = applicationName + num,
    categories = Seq(categoryStr),
    icon = apiIcon,
    free = free,
    downloads = downloads,
    stars = stars)

  val categorizedAppDetail: CategorizedAppDetail = categorizedAppDetail(0)
  val seqCategorizedAppDetail: Seq[CategorizedAppDetail] = Seq(categorizedAppDetail(0), categorizedAppDetail(1), categorizedAppDetail(2))

  def screenshots(num: Int = 0) = userV1Screenshot + num

  val seqScreenshots: Seq[String] = Seq(screenshots(0), screenshots(1), screenshots(2))

  def recommendationApp(num: Int = 0) = RecommendationApp(
    packageName = userV1PackageName + num,
    title = userV1Title + num,
    downloads = userV1Downloads,
    icon = userV1Icon,
    stars = userV1Stars,
    free = userV1Free,
    screenshots = seqScreenshots)

  val recommendationApp: RecommendationApp = recommendationApp(0)
  val seqRecommendationApp: Seq[RecommendationApp] = Seq(recommendationApp(0), recommendationApp(1), recommendationApp(2))

  val packageStats = PackagesStats(1, None)

  def collectionApp(num: Int = 0) = CollectionApp(
    stars = sharedCollectionPackageStars,
    icon = sharedCollectionPackageIcon,
    packageName = sharedCollectionPackageName + num,
    downloads = sharedCollectionDownloads,
    category = sharedCollectionCategory.name,
    title = sharedCollectionPackageTitle + num,
    free = sharedCollectionFree)

  val collectionApp: CollectionApp = collectionApp(0)
  val seqCollectionApp: Seq[CollectionApp] = Seq(collectionApp(0), collectionApp(1), collectionApp(2))

  def collectionV2(num: Int = 0) = Collection(
    name = sharedCollectionName,
    author = author,
    icon = sharedCollectionIcon,
    category = sharedCollectionCategory.name,
    community = community,
    publishedOn = userV1PublishedOnStr,
    installations = Some(userV1Installations),
    views = Some(views),
    subscriptions = Some(subscriptions),
    publicIdentifier = sharedCollectionId + num,
    appsInfo = seqCollectionApp,
    packages = seqCollectionApp map (_.packageName))

  val collectionV2: Collection = collectionV2(0)
  val seqCollectionV2: Seq[Collection] = Seq(collectionV2(0), collectionV2(1), collectionV2(2))

  def packagesByCategorySeq(num: Int = 0) = PackagesByCategory(
    category = NineCardsCategory(categoryStr),
    packages = userV1Packages)

  val seqPackagesByCategory: Seq[PackagesByCategory] = Seq(packagesByCategorySeq(0), packagesByCategorySeq(1), packagesByCategorySeq(2))

  val rankAppMap = Map(seqPackagesByCategory map (
    packagesByCategory => packagesByCategory.category.name -> packagesByCategory.packages): _*)

  val recommendationsResponse = RecommendationsResponse(items = seqRecommendationApp)

  val recommendationByAppsResponse = RecommendationsByAppsResponse(apps = seqRecommendationApp)

  val recommendationsByAppsRequest = RecommendationsByAppsRequest(userV1Packages, excludedPackages, userV1Limit)

  val loginRequest = ApiLoginRequest(email, userV1AndroidId, userV1TokenId)

  val installationRequest = InstallationRequest(userV1DeviceToken)

  val categorizeOneRequest = CategorizeRequest(seqCategorizedApp.headOption.map(_.packageName).toSeq)

  val categorizeRequest = CategorizeRequest(seqCategorizedApp.map(_.packageName))

  val recommendationsRequest = RecommendationsRequest(excludedPackages, userV1Limit)

  val createCollectionRequest = CreateCollectionRequest(sharedCollectionName, author, userV1Icon, categoryStr, community, userV1Packages)

  val updateCollectionRequest = UpdateCollectionRequest(Some(CollectionUpdateInfo(sharedCollectionName)), Some(userV1Packages))

  val updateCollectionResponse = UpdateCollectionResponse(sharedCollectionId, packageStats)

  val collectionsResponse = CollectionsResponse(seqCollectionV2)

  val rankAppsRequest = RankAppsRequest(rankAppMap, Some(userV1Localization))

  val rankAppsResponse = RankAppsResponse(rankAppMap)

  val seqSubscription = Seq(sharedCollectionId)

}
