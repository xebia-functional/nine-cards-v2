package cards.nine.services.api.impl

import cards.nine.api.version1.{User, _}
import cards.nine.api.version2._
import cards.nine.commons.test.data.ApiTestData
import cards.nine.commons.test.data.ApiV1Values._
import cards.nine.commons.test.data.ApiValues._
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.commons.test.data.UserV1Values._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models.NineCardsIntentConversions
import cards.nine.models.types.NineCardsMoment
import play.api.libs.json.Json

trait ApiServicesImplData extends ApiTestData with NineCardsIntentConversions {

  def authGoogleDevice(num: Int = 0) =
    AuthGoogleDevice(
      name = userDeviceName + num,
      deviceId = userDeviceId + num,
      secretToken = marketToken,
      permissions = permissions)

  val authGoogleDevice: AuthGoogleDevice = authGoogleDevice(0)
  val seqAuthGoogleDevice: Seq[AuthGoogleDevice] =
    Seq(authGoogleDevice(0), authGoogleDevice(1), authGoogleDevice(2))

  def authGoogle(num: Int = 0) = AuthGoogle(email = email, devices = seqAuthGoogleDevice)

  val authGoogle: AuthGoogle = authGoogle(0)

  def authFacebook(num: Int = 0) =
    AuthFacebook(
      id = authFacebookId,
      accessToken = authFacebookAccessToken,
      expirationDate = authFacebookExpirationDate)

  val authFacebook: AuthFacebook = authFacebook(0)

  def authTwitter(num: Int = 0) =
    AuthTwitter(
      id = authTwitterId,
      screenName = authTwitterScreenName,
      consumerKey = authTwitterConsumerKey,
      consumerSecret = authTwitterConsumerSecret,
      authToken = authTwitterAuthToken,
      authTokenSecret = authTwitterAuthTokenSecret,
      key = authTwitterKey,
      secretKey = authTwitterSecretKey)

  val authTwitter: AuthTwitter = authTwitter(0)

  def authAnonymous(num: Int = 0) = AuthAnonymous(id = authAnonymousId)

  val authAnonymous: AuthAnonymous = authAnonymous(0)

  def authData(num: Int = 0) =
    AuthData(
      google = Some(authGoogle),
      facebook = Option(authFacebook),
      twitter = Option(authTwitter),
      anonymous = Option(authAnonymous))

  val authData: AuthData = authData(0)

  def apiUserV1(num: Int = 0) =
    User(
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
    authData = Option(
      authData.copy(
        google = Option(authGoogle.copy(devices = Seq(authGoogleDevice))),
        facebook = None,
        twitter = None,
        anonymous = None)))

  def userConfigProfileImage =
    UserConfigProfileImage(
      imageType = userConfigPlusImageType,
      imageUrl = imageUrl,
      secureUrl = Option(userConfigPlusSecureUrl))

  def userConfigPlusProfile =
    UserConfigPlusProfile(displayName = displayName, profileImage = userConfigProfileImage)

  def userConfigCollectionItem(num: Int = 0) =
    UserConfigCollectionItem(
      itemType = itemType.name,
      title = title + num,
      metadata = Json.parse(intent),
      categories = Option(Seq(category.name, anotherCategory.name)))

  val seqUserConfigCollectionItem: Seq[UserConfigCollectionItem] =
    Seq(userConfigCollectionItem(0), userConfigCollectionItem(1), userConfigCollectionItem(2))

  def userConfigCollection(num: Int = 0) =
    UserConfigCollection(
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
      radius = radius,
      lat = latitude,
      lng = longitude,
      alt = altitude,
      category = Option(category.name))

  val seqUserConfigCollection: Seq[UserConfigCollection] =
    Seq(userConfigCollection(0), userConfigCollection(1), userConfigCollection(2))

  def userConfigDevice(num: Int = 0) =
    UserConfigDevice(
      deviceId = deviceIdPrefix + num,
      deviceName = userDeviceName + num,
      collections = seqUserConfigCollection)

  val seqUserConfigDevice: Seq[UserConfigDevice] =
    Seq(userConfigDevice(0), userConfigDevice(1), userConfigDevice(2))

  def userConfigGeoInfo =
    UserConfigGeoInfo(homeMorning = None, homeNight = None, work = None, current = None)

  def userConfigStatusInfo =
    UserConfigStatusInfo(
      products = products,
      friendsReferred = friendsReferred,
      themesShared = themesShared,
      collectionsShared = collectionsShared,
      customCollections = customCollections,
      earlyAdopter = earlyAdopter,
      communityMember = communityMember,
      joinedThrough = joinedThrough,
      tester = tester)

  def userConfig(num: Int = 0) =
    UserConfig(
      _id = userV1Id,
      email = email,
      plusProfile = userConfigPlusProfile,
      devices = seqUserConfigDevice,
      geoInfo = userConfigGeoInfo,
      status = userConfigStatusInfo)

  val userConfig: UserConfig = userConfig(0)

  def categorizedApp(num: Int = 0) =
    CategorizedApp(packageName = applicationPackageName + num, categories = Seq(categoryStr))

  val categorizedApp: CategorizedApp = categorizedApp(0)
  val seqCategorizedApp: Seq[CategorizedApp] =
    Seq(categorizedApp(0), categorizedApp(1), categorizedApp(2))

  def categorizedAppDetail(num: Int = 0) =
    CategorizedAppDetail(
      packageName = applicationPackageName + num,
      title = applicationName + num,
      categories = Seq(categoryStr),
      icon = apiIcon,
      free = free,
      downloads = downloads,
      stars = stars)

  val categorizedAppDetail: CategorizedAppDetail = categorizedAppDetail(0)
  val seqCategorizedAppDetail: Seq[CategorizedAppDetail] =
    Seq(categorizedAppDetail(0), categorizedAppDetail(1), categorizedAppDetail(2))

  def screenshots(num: Int = 0) = screenshot + num

  val seqScreenshots: Seq[String] = Seq(screenshots(0), screenshots(1), screenshots(2))

  def notCategorizedApp(num: Int = 0) =
    NotCategorizedApp(
      packageName = sharedCollectionPackageName + num,
      title = sharedCollectionPackageTitle + num,
      downloads = sharedCollectionDownloads,
      icon = sharedCollectionPackageIcon,
      stars = sharedCollectionPackageStars,
      free = free,
      screenshots = seqScreenshots)

  val notCategorizedApp: NotCategorizedApp = notCategorizedApp(0)
  val seqNotCategorizedApp: Seq[NotCategorizedApp] =
    Seq(notCategorizedApp(0), notCategorizedApp(1), notCategorizedApp(2))

  val packageStats = PackagesStats(1, None)

  def collectionApp(num: Int = 0) =
    CollectionApp(
      stars = sharedCollectionPackageStars,
      icon = sharedCollectionPackageIcon,
      packageName = sharedCollectionPackageName + num,
      downloads = sharedCollectionDownloads,
      categories = Seq(category.name),
      title = sharedCollectionPackageTitle + num,
      free = sharedCollectionFree)

  val collectionApp: CollectionApp = collectionApp(0)
  val seqCollectionApp: Seq[CollectionApp] =
    Seq(collectionApp(0), collectionApp(1), collectionApp(2))

  def collectionV2(num: Int = 0) =
    Collection(
      name = sharedCollectionName,
      author = author,
      owned = owned,
      icon = sharedCollectionIcon,
      category = category.name,
      community = community,
      publishedOn = publishedOnStr,
      installations = Some(installations),
      views = Some(views),
      subscriptions = Some(subscriptions),
      publicIdentifier = sharedCollectionId + num,
      appsInfo = seqCollectionApp,
      packages = seqCollectionApp map (_.packageName))

  val collectionV2: Collection         = collectionV2(0)
  val seqCollectionV2: Seq[Collection] = Seq(collectionV2(0), collectionV2(1), collectionV2(2))

  val rankAppMap = Map(
    seqPackagesByCategory map (packagesByCategory =>
                                 packagesByCategory.category.name -> packagesByCategory.packages): _*)

  val recommendationsResponse = RecommendationsResponse(items = seqNotCategorizedApp)

  val recommendationByAppsResponse = RecommendationsByAppsResponse(apps = seqNotCategorizedApp)

  val recommendationsByAppsRequest =
    RecommendationsByAppsRequest(apiPackages, excludedPackages, limit)

  val loginRequest = ApiLoginRequest(email, androidId, tokenId)

  val installationRequest = InstallationRequest(deviceToken)

  val categorizeOneRequest = CategorizeRequest(
    seqCategorizedApp.headOption.map(_.packageName).toSeq)

  val categorizeRequest = CategorizeRequest(seqCategorizedApp.map(_.packageName))

  val recommendationsRequest = RecommendationsRequest(excludedPackages, limit)

  val createCollectionRequest = CreateCollectionRequest(
    sharedCollectionName,
    author,
    sharedCollectionPackageIcon,
    categoryStr,
    community,
    apiPackages)

  val updateCollectionRequest =
    UpdateCollectionRequest(Some(CollectionUpdateInfo(sharedCollectionName)), Some(apiPackages))

  val updateCollectionResponse = UpdateCollectionResponse(sharedCollectionId, packageStats)

  val collectionsResponse = CollectionsResponse(seqCollectionV2)

  val rankAppsRequest = RankAppsRequest(rankAppMap, Some(location))

  val rankAppsResponse = RankAppsResponse((rankAppMap map { app =>
    RankAppsCategoryResponse(app._1, app._2)
  }).toSeq)

  val seqSubscription = Seq(sharedCollectionId)

  def rankAppsCategoryResponse(num: Int = 0) =
    RankAppsCategoryResponse(category = momentTypeSeq(num), packages = Seq(apiPackages(num)))

  val seqRankAppsCategoryResponse: Seq[RankAppsCategoryResponse] =
    Seq(rankAppsCategoryResponse(0), rankAppsCategoryResponse(1), rankAppsCategoryResponse(2))

  val rankAppsByMomentResponse = RankAppsByMomentResponse(seqRankAppsCategoryResponse)

  val rankAppsByMomentRequest =
    RankAppsByMomentRequest(apiPackages, momentTypeSeq.take(3), Some(location), limit)

  def rankWidgetsResponse(num: Int = 0) =
    RankWidgetsResponse(packageName = apiPackageName + num, className = apiClassName + num)

  val seqRankWidgetsResponse: Seq[RankWidgetsResponse] =
    Seq(rankWidgetsResponse(0), rankWidgetsResponse(1), rankWidgetsResponse(2))

  def rankWidgetsWithMomentResponse(num: Int = 0) =
    RankWidgetsWithMomentResponse(
      moment = momentTypeSeq(num),
      widgets = Seq(seqRankWidgetsResponse(num)))

  val seqRankWidgetsWithMomentResponse: Seq[RankWidgetsWithMomentResponse] = Seq(
    rankWidgetsWithMomentResponse(0),
    rankWidgetsWithMomentResponse(1),
    rankWidgetsWithMomentResponse(2))

  val rankWidgetsByMomentResponse = RankWidgetsByMomentResponse(seqRankWidgetsWithMomentResponse)

  val rankWidgetsByMomentRequest =
    RankWidgetsByMomentRequest(apiPackages, momentTypeSeq.take(3), Some(location), limit)

}
