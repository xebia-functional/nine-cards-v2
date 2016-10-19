package cards.nine.services.api.impl

import cards.nine.api._
import cards.nine.api.version1.{User, _}
import cards.nine.api.version2._
import cards.nine.commons.test.data.UserV1Values._
import cards.nine.models._
import cards.nine.models.types.NineCardsCategory

import scala.util.Random

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
    packageName = userV1PackageName,
    category = userV1Category)

  val categorizedApp: CategorizedApp = categorizedApp(0)
  val seqCategorizedApp: Seq[CategorizedApp] = Seq(categorizedApp(0), categorizedApp(1), categorizedApp(2))

  def categorizedAppDetail(num: Int = 0) = CategorizedAppDetail(
      packageName = userV1PackageName,
      title = userV1Title,
      categories = Seq(userV1Category),
      icon = userV1Icon,
      free = Random.nextBoolean(),
      downloads = "500,000,000+",
      stars = Random.nextDouble() * 5)

  val categorizedAppDetail: CategorizedAppDetail = categorizedAppDetail(0)
  val seqCategorizedAppDetail: Seq[CategorizedAppDetail] = Seq(categorizedAppDetail(0), categorizedAppDetail(1), categorizedAppDetail(2))

  val loginRequest = ApiLoginRequest(userV1email, userV1AndroidId, userV1TokenId)

  val installationRequest = InstallationRequest(userV1DeviceToken)

  val categorizeOneRequest = CategorizeRequest(seqCategorizedApp.headOption.map(_.packageName).toSeq)

  val categorizeRequest = version2.CategorizeRequest(seqCategorizedApp.map(_.packageName))


//  def generatePackagesByCategorySeq(num: Int = 10) =
//    1 to num map { _ =>
//      PackagesByCategory(
//        category = NineCardsCategory(category),
//        packages = packages
//      )
//    }
//

//
//  def generateCategorizedAppsDetail(num: Int = 10) =
//    1 to num map { _ =>
//      generateCategorizedAppDetail
//    }
//

//

//
//
//
//  def generateRecommendationApp =
//    version2.RecommendationApp(
//      packageName = Random.nextString(10),
//      title = Random.nextString(10),
//      downloads = "500,000,000+",
//      icon = Random.nextString(10),
//      stars = Random.nextDouble() * 5,
//      free = Random.nextBoolean(),
//      screenshots = Seq("screenshot1", "screenshot2", "screenshot3"))
//
//  def generateCollection(collectionApps: Seq[cards.nine.api.version2.CollectionApp]) =
//    version2.Collection(
//      name = Random.nextString(10),
//      author = Random.nextString(10),
//      icon = Random.nextString(10),
//      category = "SOCIAL",
//      community = Random.nextBoolean(),
//      publishedOn = "\"2016-08-19T09:39:00.359000\"",
//      installations = Some(Random.nextInt(10)),
//      views = Some(Random.nextInt(100)),
//      subscriptions = Some(Random.nextInt()),
//      publicIdentifier = Random.nextString(10),
//      appsInfo = collectionApps,
//      packages = collectionApps map (_.packageName))
//
//  def generateCollectionApp =
//    version2.CollectionApp(
//      stars = Random.nextDouble() * 5,
//      icon = Random.nextString(10),
//      packageName = Random.nextString(10),
//      downloads = "500,000,000+",
//      category = "SOCIAL",
//      title = Random.nextString(10),
//      free = Random.nextBoolean())
//
//  val offset = 0
//
//  val limit = 20
//
//  val category = "COMMUNICATION"
//
//  val name = "Name"
//
//  val author = "Author"
//
//  val packages = List("Package1", "Package2")
//
//  val excludedPackages = List("Package3", "Package4")
//
//  val icon = "Icon"
//
//  val community = true
//
//  val collectionTypeTop = "top"
//  val collectionTypeLatest = "latest"
//  val collectionTypeUnknown = "unknown"
//
////  val user = generateUser
//

//
  val categorizeAppsDetail = generateCategorizedAppsDetail()
//
//  val categorizedDetailPackages = categorizeAppsDetail map { app =>
//    CategorizedDetailPackage(
//      packageName = app.packageName,
//      title = app.title,
//      category = app.categories.headOption,
//      icon = app.icon,
//      free = app.free,
//      downloads = app.downloads,
//      stars = app.stars)
//  }
//
//  val recommendationApps = 1 to 10 map (_ => generateRecommendationApp)
//
//  val recommendationResponse = version2.RecommendationsResponse(recommendationApps)
//
//  val recommendationByAppsResponse = version2.RecommendationsByAppsResponse(recommendationApps)
//
//  val collectionApps1 = 1 to 5 map (_ => generateCollectionApp)
//  val collectionApps2 = 1 to 5 map (_ => generateCollectionApp)
//  val collectionApps3 = 1 to 5 map (_ => generateCollectionApp)
//
//  val collections = Seq(
//    generateCollection(collectionApps1),
//    generateCollection(collectionApps2),
//    generateCollection(collectionApps3))
//
//  val sharedCollection = generateCollection(collectionApps1)
//
////  val userConfig = generateUserConfig
//
//  val apiKey = Random.nextString(10)
//
//  val sessionToken = Random.nextString(20)
//
//  val deviceId = "device-id"
//
//  val deviceToken = Random.nextString(20)
//
//  val secretToken = Random.nextString(20)
//
////  val permissions = Seq("permission1", "permission2")
//
//  val email = "email@dot.com"
//
//  val packageName = Random.nextString(20)
//
//  val location = "ES"
//
//  val androidId = Random.nextString(10)
//
//  val tokenId = Random.nextString(30)
//
//  val sharedCollectionId = Random.nextString(30)
//
//  val packageStats = version2.PackagesStats(1, None)
//
//  val subscriptions =  version2.SubscriptionsResponse(subscriptions = Seq(sharedCollectionId))
//
//  val createCollectionRequest = version2.CreateCollectionRequest(name, author, icon, category, community, packages)
//
//  val updateCollectionRequest = version2.UpdateCollectionRequest(Some(CollectionUpdateInfo(name)), Some(packages))
//
//  val updateCollectionResponse = version2.UpdateCollectionResponse(sharedCollectionId, packageStats)
//
//  val recommendationsRequest = version2.RecommendationsRequest(excludedPackages, limit)
//
//  val recommendationsByAppsRequest = version2.RecommendationsByAppsRequest(packages, excludedPackages, limit)
//




//
//
//  val packagesByCategorySeq = generatePackagesByCategorySeq()
//
//  val items = Map(packagesByCategorySeq map (
//    packagesByCategory => packagesByCategory.category.name -> packagesByCategory.packages): _*)
//
//  val rankAppsRequest = version2.RankAppsRequest(items, Some(location))
//
//  val rankAppsResponse = version2.RankAppsResponse(items)
}
