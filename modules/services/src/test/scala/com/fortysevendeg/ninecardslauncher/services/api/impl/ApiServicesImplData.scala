package cards.nine.services.api.impl

import cards.nine.api.version1.{User, _}
import cards.nine.api.version2._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.commons.test.data.UserV1Values._
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.{PackagesByCategory, Device}


trait ApiServicesImplData {

  def permissions(num: Int = 0) = userV1Permission + num

  val seqPermission: Seq[String] = Seq(permissions(0), permissions(1), permissions(2))

  def authGoogleDevice(num: Int = 0) = AuthGoogleDevice(
    name = userV1GoogleDeviceName + num,
    deviceId = userV1GoogleDeviceId + num,
    secretToken = userV1SecretToken,
    permissions = seqPermission)

  val authGoogleDevice: AuthGoogleDevice = authGoogleDevice(0)
  val seqAuthGoogleDevice: Seq[AuthGoogleDevice] = Seq(authGoogleDevice(0), authGoogleDevice(1), authGoogleDevice(2))

  def authGoogle(num: Int = 0) = AuthGoogle(
    email = userV1email,
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

  def userV1(num: Int = 0) = User(
    _id = Option(userV1Id),
    sessionToken = Option(userV1SessionToken),
    email = Option(userV1email),
    username = Option(userV1Name),
    password = Option(userV1Password),
    authData = Option(authData))

  val userV1: User = userV1(0)

  def device(num: Int = 0) = Device(
    name = userV1GoogleDeviceName + num,
    deviceId = userV1GoogleDeviceId + num,
    secretToken = userV1SecretToken,
    permissions = seqPermission)

  val device: Device = device(0)
  val seqDevice: Seq[Device] = Seq(device(0), device(1), device(2))

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
    imageUrl = userConfigPlusImageUrl,
    secureUrl = Option(userConfigPlusSecureUrl))

  def userConfigPlusProfile = UserConfigPlusProfile(
    displayName = userConfigPlusDisplayName,
    profileImage = userConfigProfileImage)

  def userConfigDevice(num: Int = 0) = UserConfigDevice(
    deviceId = userV1DeviceId,
    deviceName = userV1DeviceName,
    collections = Seq.empty) //TODO review this collections

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
    email = userV1email,
    plusProfile = userConfigPlusProfile,
    devices = seqUserConfigDevice,
    geoInfo = userConfigGeoInfo,
    status = userConfigStatusInfo)

  val userConfig: UserConfig = userConfig(0)

  def categorizedApp(num: Int = 0) = CategorizedApp(
    packageName = userV1PackageName + num,
    category = userV1Category)

  val categorizedApp: CategorizedApp = categorizedApp(0)
  val seqCategorizedApp: Seq[CategorizedApp] = Seq(categorizedApp(0), categorizedApp(1), categorizedApp(2))

  def categorizedAppDetail(num: Int = 0) = CategorizedAppDetail(
      packageName = userV1PackageName + num,
      title = userV1Title + num,
      categories = Seq(userV1Category),
      icon = userV1Icon,
      free = userV1Free,
      downloads = userV1Downloads,
      stars = userV1Stars)

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
    stars = userV1Stars,
    icon = userV1Icon,
    packageName = userV1PackageName + num,
    downloads = userV1Downloads,
    category = userV1Category,
    title = userV1Title,
    free = userV1Free)

  val collectionApp: CollectionApp = collectionApp(0)
  val seqCollectionApp: Seq[CollectionApp] = Seq(collectionApp(0), collectionApp(1), collectionApp(2))

  def collection(num: Int = 0) = Collection(
    name = sharedCollectionName,
    author = author,
    icon = userV1Icon,
    category = userV1Category,
    community = community,
    publishedOn = publishedOn.toString,
    installations = Some(userV1Installations),
    views = Some(views),
    subscriptions = Some(userV1Subscriptions),
    publicIdentifier = publicIdentifier,
    appsInfo = seqCollectionApp,
    packages = seqCollectionApp map (_.packageName))

  val collection: Collection = collection(0)
  val seqCollection: Seq[Collection] = Seq(collection(0), collection(1), collection(2))

  def packagesByCategorySeq(num: Int = 0) = PackagesByCategory(
    category = NineCardsCategory(userV1Category),
    packages = userV1Packages)

  val seqPackagesByCategory: Seq[PackagesByCategory] = Seq(packagesByCategorySeq(0), packagesByCategorySeq(1), packagesByCategorySeq(2))

  val rankAppMap = Map(seqPackagesByCategory map (
    packagesByCategory => packagesByCategory.category.name -> packagesByCategory.packages): _*)

  val recommendationsResponse = RecommendationsResponse(items = seqRecommendationApp)

  val recommendationByAppsResponse = RecommendationsByAppsResponse(apps = seqRecommendationApp)

  val recommendationsByAppsRequest = RecommendationsByAppsRequest(userV1Packages, excludedPackages, userV1Limit)

  val loginRequest = ApiLoginRequest(userV1email, userV1AndroidId, userV1TokenId)

  val installationRequest = InstallationRequest(userV1DeviceToken)

  val categorizeOneRequest = CategorizeRequest(seqCategorizedApp.headOption.map(_.packageName).toSeq)

  val categorizeRequest = CategorizeRequest(seqCategorizedApp.map(_.packageName))

  val recommendationsRequest = RecommendationsRequest(excludedPackages, userV1Limit)

  val createCollectionRequest = CreateCollectionRequest(sharedCollectionName, author, userV1Icon, userV1Category, community, userV1Packages)

  val updateCollectionRequest = UpdateCollectionRequest(Some(CollectionUpdateInfo(sharedCollectionName)), Some(userV1Packages))

  val updateCollectionResponse = UpdateCollectionResponse(sharedCollectionId, packageStats)

  val collectionsResponse = CollectionsResponse(seqCollection)

  val rankAppsRequest = RankAppsRequest(rankAppMap, Some(userV1Localization))

  val rankAppsResponse = RankAppsResponse(rankAppMap)

  val subscriptions = SubscriptionsResponse(subscriptions = Seq(sharedCollectionId))

}
