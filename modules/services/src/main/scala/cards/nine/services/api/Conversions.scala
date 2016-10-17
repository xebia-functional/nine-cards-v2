package cards.nine.services.api

import cards.nine.api._
import cards.nine.models._
import org.joda.time.format.DateTimeFormat

import scala.util.{Success, Try}

trait Conversions {

  def toUser(
    email: String,
    device: LoginV1Device
    ): cards.nine.api.version1.User =
    version1.User(
      _id = None,
      email = None,
      sessionToken = None,
      username = None,
      password = None,
      authData = Some(version1.AuthData(
        google = Some(version1.AuthGoogle(
          email = email,
          devices = List(fromGoogleDevice(device))
        )),
        facebook = None,
        twitter = None,
        anonymous = None)))

  def fromGoogleDevice(device: LoginV1Device): cards.nine.api.version1.AuthGoogleDevice =
    version1.AuthGoogleDevice(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toLoginResponseV1(statusCode: Int, user: cards.nine.api.version1.User): LoginResponseV1 =
    LoginResponseV1(
      statusCode,
      userId = user._id,
      sessionToken = user.sessionToken,
      email = user.email,
      devices = (for {
        data <- user.authData
        google <- data.google
      } yield toGoogleDeviceSeq(google.devices)) getOrElse Seq.empty)

  def toGoogleDeviceSeq(devices: Seq[cards.nine.api.version1.AuthGoogleDevice]): Seq[LoginV1Device] = devices map toGoogleDevice

  def toGoogleDevice(device: cards.nine.api.version1.AuthGoogleDevice): LoginV1Device =
    LoginV1Device(
      name = device.name,
      deviceId = device.deviceId,
      secretToken = device.secretToken,
      permissions = device.permissions)

  def toCategorizedPackage(packageName: String, categorizeResponse: cards.nine.api.version2.CategorizeResponse): CategorizedPackage =
    CategorizedPackage(packageName, categorizeResponse.items.find(_.packageName == packageName).map(_.category))

  def toCategorizedPackages(categorizeResponse: cards.nine.api.version2.CategorizeResponse): Seq[CategorizedPackage] =
    categorizeResponse.items.map(app => CategorizedPackage(app.packageName, Some(app.category)))

  def toCategorizedDetailPackages(categorizeResponse: cards.nine.api.version2.CategorizeDetailResponse): Seq[CategorizedDetailPackage] =
    categorizeResponse.items.map { app =>
      CategorizedDetailPackage(
        packageName = app.packageName,
        title = app.title,
        category = app.categories.headOption,
        icon = app.icon,
        free = app.free,
        downloads = app.downloads,
        stars = app.stars)
    }

  def toUserConfig(apiUserConfig: cards.nine.api.version1.UserConfig): UserV1 =
    UserV1(
      _id = apiUserConfig._id,
      email = apiUserConfig.email,
      plusProfile = toUserConfigPlusProfile(apiUserConfig.plusProfile),
      devices = apiUserConfig.devices map toUserConfigDevice,
      status = toUserConfigStatusInfo(apiUserConfig.status))

  def toUserConfigPlusProfile(apiPlusProfile: cards.nine.api.version1.UserConfigPlusProfile): UserV1PlusProfile =
    UserV1PlusProfile(
      displayName = apiPlusProfile.displayName,
      profileImage = toUserConfigProfileImage(apiPlusProfile.profileImage))

  def toUserConfigProfileImage(apiProfileImage: cards.nine.api.version1.UserConfigProfileImage): UserV1ProfileImage =
    UserV1ProfileImage(
      imageType = apiProfileImage.imageType,
      imageUrl = apiProfileImage.imageUrl)

  def toUserConfigDevice(apiDevice: cards.nine.api.version1.UserConfigDevice): UserV1Device =
    UserV1Device(
      deviceId = apiDevice.deviceId,
      deviceName = apiDevice.deviceName,
      collections = apiDevice.collections map toUserConfigCollection)

  def toUserConfigCollection(apiCollection: cards.nine.api.version1.UserConfigCollection): UserV1Collection =
    UserV1Collection(
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

  def toUserConfigCollectionItem(apiCollectionItem: cards.nine.api.version1.UserConfigCollectionItem): UserV1CollectionItem =
    UserV1CollectionItem(
      itemType = apiCollectionItem.itemType,
      title = apiCollectionItem.title,
      metadata = apiCollectionItem.metadata,
      categories = apiCollectionItem.categories)

  def toUserConfigStatusInfo(apiStatusInfo: cards.nine.api.version1.UserConfigStatusInfo): UserV1StatusInfo =
    UserV1StatusInfo(
      products = apiStatusInfo.products,
      friendsReferred = apiStatusInfo.friendsReferred,
      themesShared = apiStatusInfo.themesShared,
      collectionsShared = apiStatusInfo.collectionsShared,
      customCollections = apiStatusInfo.customCollections,
      earlyAdopter = apiStatusInfo.earlyAdopter,
      communityMember = apiStatusInfo.communityMember,
      joinedThrough = apiStatusInfo.joinedThrough,
      tester = apiStatusInfo.tester)

  def toRecommendationAppSeq(apps: Seq[cards.nine.api.version2.RecommendationApp]): Seq[RecommendationApp] =
    apps map toRecommendationApp

  def toRecommendationApp(app: cards.nine.api.version2.RecommendationApp): RecommendationApp =
    RecommendationApp(
      packageName = app.packageName,
      name = app.title,
      downloads = app.downloads,
      icon = app.icon,
      stars = app.stars,
      free = app.free,
      screenshots = app.screenshots)

  def toSharedCollectionResponseSeq(collections: Seq[cards.nine.api.version2.Collection]): Seq[SharedCollection] =
    collections map toSharedCollection

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

  def toSharedCollection(collection: cards.nine.api.version2.Collection) =
    SharedCollection(
      id = collection.publicIdentifier,
      sharedCollectionId = collection.publicIdentifier,
      publishedOn = formatPublishedDate(collection.publishedOn),
      author = collection.author,
      name = collection.name,
      packages = collection.packages,
      resolvedPackages = toSharedCollectionPackageResponseSeq(collection.appsInfo),
      views = collection.views getOrElse 0,
      subscriptions = collection.subscriptions,
      category = collection.category,
      icon = collection.icon,
      community = collection.community)

  def toSharedCollectionPackageResponseSeq(packages: Seq[cards.nine.api.version2.CollectionApp]): Seq[SharedCollectionPackageResponse] =
    packages map toSharedCollectionPackageResponse

  def toSharedCollectionPackageResponse(item: cards.nine.api.version2.CollectionApp): SharedCollectionPackageResponse =
    SharedCollectionPackageResponse(
      packageName = item.packageName,
      title = item.title,
      icon = item.icon,
      stars = item.stars,
      downloads = item.downloads,
      free = item.free)

  def toSubscriptionResponseSeq(subscriptions: Seq[String]): Seq[SubscriptionResponse] =
    subscriptions map toSubscriptionResponse

  def toSubscriptionResponse(subscription: String) =
    SubscriptionResponse(
      sharedCollectionId = subscription)

  def toItemsMap(packagesByCategorySeq: Seq[PackagesByCategory]) =
    Map(packagesByCategorySeq map (
      packagesByCategory => packagesByCategory.category.name -> packagesByCategory.packages): _*)

  def toRankAppsResponse(items: Map[String, Seq[String]]) =
    (items map {
      case (category, packages) => RankAppsResponse(category = category, packages = packages)
    }).toSeq

}
