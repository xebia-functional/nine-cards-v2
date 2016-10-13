package cards.nine.services.api.impl

import cards.nine.api._
import cards.nine.api.version2.CollectionUpdateInfo
import cards.nine.models.PackagesByCategory
import cards.nine.models.types.NineCardsCategory
import cards.nine.services.api.CategorizedDetailPackage

import scala.util.Random

trait ApiServicesImplData {

  def generateUser =
    version1.User(
      _id = Some(Random.nextString(10)),
      sessionToken = Some(Random.nextString(10)),
      email = Some(Random.nextString(10)),
      username = Some(Random.nextString(10)),
      password = Some(Random.nextString(10)),
      authData = Some(generateAuthData))

  def generateAuthData =
    version1.AuthData(
      google = Some(generateAuthGoogle),
      facebook = None,
      twitter = None,
      anonymous = None)

  def generateAuthGoogle =
    version1.AuthGoogle(
      email = Random.nextString(10),
      devices = generateAuthGoogleDevices(2))

  def generateAuthGoogleDevices(num: Int = 10) =
    1 to num map { _ =>
      version1.AuthGoogleDevice(
        name = Random.nextString(10),
        deviceId = Random.nextString(10),
        secretToken = Random.nextString(10),
        permissions = generatePermissions())
    }

  def generatePermissions(num: Int = 10): Seq[String] = 1 to num map { n => s"Permission $n" }

  def generateCategorizedApps(num: Int = 10) =
    1 to num map { _ =>
      generateCategorizedApp
    }

  def generateCategorizedAppsDetail(num: Int = 10) =
    1 to num map { _ =>
      generateCategorizedAppDetail
    }

  def generateCategorizedApp =
    version2.CategorizedApp(
      packageName = Random.nextString(10),
      category = "SOCIAL")

  def generateCategorizedAppDetail =
    version2.CategorizedAppDetail(
      packageName = Random.nextString(10),
      title = Random.nextString(10),
      categories = Seq("SOCIAL"),
      icon = Random.nextString(10),
      free = Random.nextBoolean(),
      downloads = "500,000,000+",
      stars = Random.nextDouble() * 5)

  def generateUserConfig =
    version1.UserConfig(
      Random.nextString(10),
      Random.nextString(10),
      generateUserConfigPlusProfile,
      generateUserConfigDeviceSeq(),
      generateUserConfigGeoInfo,
      generateUserConfigStatusInfo)

  def generateUserConfigPlusProfile =
    version1.UserConfigPlusProfile(
      Random.nextString(10),
      generateUserConfigProfileImage)

  def generateUserConfigProfileImage =
    version1.UserConfigProfileImage(
      imageType = Random.nextInt(10),
      imageUrl = Random.nextString(10),
      secureUrl = Option(Random.nextString(10)))

  def generateUserConfigDeviceSeq(num: Int = 10) =
    1 to num map { _ =>
      version1.UserConfigDevice(
        Random.nextString(10),
        Random.nextString(10),
        Seq.empty)
    }

  def generateUserConfigGeoInfo =
    version1.UserConfigGeoInfo(
      homeMorning = None,
      homeNight = None,
      work = None,
      current = None)

  def generateUserConfigStatusInfo =
    version1.UserConfigStatusInfo(
      products = Seq.empty,
      friendsReferred = Random.nextInt(10),
      themesShared = Random.nextInt(10),
      collectionsShared = Random.nextInt(10),
      customCollections = Random.nextInt(10),
      earlyAdopter = false,
      communityMember = false,
      joinedThrough = None,
      tester = false)

  def generateRecommendationApp =
    version2.RecommendationApp(
      packageName = Random.nextString(10),
      title = Random.nextString(10),
      downloads = "500,000,000+",
      icon = Random.nextString(10),
      stars = Random.nextDouble() * 5,
      free = Random.nextBoolean(),
      screenshots = Seq("screenshot1", "screenshot2", "screenshot3"))

  def generateCollection(collectionApps: Seq[cards.nine.api.version2.CollectionApp]) =
    version2.Collection(
      name = Random.nextString(10),
      author = Random.nextString(10),
      icon = Random.nextString(10),
      category = "SOCIAL",
      community = Random.nextBoolean(),
      publishedOn = "\"2016-08-19T09:39:00.359000\"",
      installations = Some(Random.nextInt(10)),
      views = Some(Random.nextInt(100)),
      subscriptions = Some(Random.nextInt()),
      publicIdentifier = Random.nextString(10),
      appsInfo = collectionApps,
      packages = collectionApps map (_.packageName))

  def generateCollectionApp =
    version2.CollectionApp(
      stars = Random.nextDouble() * 5,
      icon = Random.nextString(10),
      packageName = Random.nextString(10),
      downloads = "500,000,000+",
      category = "SOCIAL",
      title = Random.nextString(10),
      free = Random.nextBoolean())

  val offset = 0

  val limit = 20

  val category = "COMMUNICATION"

  val name = "Name"

  val author = "Author"

  val packages = List("Package1", "Package2")

  val excludedPackages = List("Package3", "Package4")

  val icon = "Icon"

  val community = true

  val collectionTypeTop = "top"
  val collectionTypeLatest = "latest"
  val collectionTypeUnknown = "unknown"

  val user = generateUser

  val categorizeApps = generateCategorizedApps()

  val categorizeAppsDetail = generateCategorizedAppsDetail()

  val categorizedDetailPackages = categorizeAppsDetail map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.title,
      category = app.categories.headOption,
      icon = app.icon,
      free = app.free,
      downloads = app.downloads,
      stars = app.stars)
  }

  val recommendationApps = 1 to 10 map (_ => generateRecommendationApp)

  val recommendationResponse = version2.RecommendationsResponse(recommendationApps)

  val recommendationByAppsResponse = version2.RecommendationsByAppsResponse(recommendationApps)

  val collectionApps1 = 1 to 5 map (_ => generateCollectionApp)
  val collectionApps2 = 1 to 5 map (_ => generateCollectionApp)
  val collectionApps3 = 1 to 5 map (_ => generateCollectionApp)

  val collections = Seq(
    generateCollection(collectionApps1),
    generateCollection(collectionApps2),
    generateCollection(collectionApps3))

  val sharedCollection = generateCollection(collectionApps1)

  val userConfig = generateUserConfig

  val apiKey = Random.nextString(10)

  val sessionToken = Random.nextString(20)

  val deviceId = "device-id"

  val deviceToken = Random.nextString(20)

  val secretToken = Random.nextString(20)

  val permissions = Seq("permission1", "permission2")

  val email = "email@dot.com"

  val packageName = Random.nextString(20)

  val location = "ES"

  val androidId = Random.nextString(10)

  val tokenId = Random.nextString(30)

  val sharedCollectionId = Random.nextString(30)

  val packageStats = version2.PackagesStats(1, None)

  val subscriptions =  version2.SubscriptionsResponse(subscriptions = Seq(sharedCollectionId))

  val createCollectionRequest = version2.CreateCollectionRequest(name, author, icon, category, community, packages)

  val updateCollectionRequest = version2.UpdateCollectionRequest(Some(CollectionUpdateInfo(name)), Some(packages))

  val updateCollectionResponse = version2.UpdateCollectionResponse(sharedCollectionId, packageStats)

  val recommendationsRequest = version2.RecommendationsRequest(excludedPackages, limit)

  val recommendationsByAppsRequest = version2.RecommendationsByAppsRequest(packages, excludedPackages, limit)

  val categorizeRequest = version2.CategorizeRequest(categorizeApps.map(_.packageName))

  val categorizeOneRequest = version2.CategorizeRequest(categorizeApps.headOption.map(_.packageName).toSeq)

  val installationRequest = version2.InstallationRequest(deviceToken)

  val loginRequest = version2.ApiLoginRequest(email, androidId, tokenId)

  val loginV1User = version1.User(
    _id = None,
    email = None,
    sessionToken = None,
    username = None,
    password = None,
    authData = Some(version1.AuthData(
      google = Some(version1.AuthGoogle(
        email = email,
        devices = List(version1.AuthGoogleDevice(
          name = name,
          deviceId = deviceId,
          secretToken = secretToken,
          permissions = permissions))
      )),
      facebook = None,
      twitter = None,
      anonymous = None)))

  def generatePackagesByCategorySeq(num: Int = 10) =
    1 to num map { _ =>
      PackagesByCategory(
        category = NineCardsCategory(category),
        packages = packages
      )
    }

  val packagesByCategorySeq = generatePackagesByCategorySeq()

  val items = Map(packagesByCategorySeq map (
    packagesByCategory => packagesByCategory.category.name -> packagesByCategory.packages): _*)

  val rankAppsRequest = version2.RankAppsRequest(items, Some(location))

  val rankAppsResponse = version2.RankAppsResponse(items)
}
