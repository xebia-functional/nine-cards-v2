package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models.types._

object ApiValues {

  val latitude: Double = 47
  val longitude: Double = 36
  val countryCode: String = "countryCode"
  val countryName: String = "countryName"
  val street: String = "street"
  val city: String = "city"
  val postalCode: String = "postalCode"
  val apiIcon: String = "apiIcon"
  val free: Boolean = true
  val downloads: String = "100"
  val stars: Double = 4.5
  val apiCategory: NineCardsCategory = Social
  val likePackages: Seq[String] = Seq("likePackage1", "likePackage2", "likePackage3")
  val limit: Int = 20
  val apiPackageName: String = "apiPackageName"
  val apiTitle: String = "apiTitle"
  val screenshots: Seq[String] = Seq("screenshot1", "screenshot2", "screenshot3")

}

object ApiV1Values {

  val permissions: Seq[String] = Seq("permission1", "permission2")
  val itemType: CardType = AppCardType
  val title: String = "title"
  val apiV1Intent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val apiV1CollectionCategory: NineCardsCategory = Game
  val apiV1CollectionAnotherCategory: NineCardsCategory = Communication
  val apiV1CollectionName: String  = "apiCollectionName"
  val apiV1OriginalSharedCollectionId: String = "apiOriginalSharedCollectionId"
  val apiV1SharedCollectionId: String = "apiSharedCollectionId"
  val apiV1SharedCollectionSubscribed: Boolean = false
  val apiV1CollectionType: CollectionType = AppsCollectionType
  val constrains: Seq[String] = Seq("constrain1", "constrain2")
  val wifi: Seq[String] = Seq("wifi1", "wifi2")
  val occurrence: Seq[String] = Seq("occurrence1", "occurrence2")
  val apiV1CollectionIcon: String  = "GAME"
  val deviceIdPrefix: String  = "deviceIdPrefix"
  val userV1Id: String = "userV1Id"
  val displayName: String = "displayName"
  val imageUrl: String = "imageUrl"
  val products: Seq[String] = Seq("product1", "product2")

}

object ApplicationValues {

  val keyword = "keyword"
  val applicationId: Int = 1
  val applicationName: String = "applicationName"
  val applicationPackageName: String = "applicationPackageName"
  val nonExistentApplicationPackageName: String = "nonExistentApplicationPackageName"
  val applicationClassName: String = "applicationClassName"
  val applicationCategoryStr: String = "SOCIAL"
  val applicationCategory: NineCardsCategory = NineCardsCategory(appsCategoryStr)
  val dateInstalled: Long = 1l
  val dateUpdated: Long = 2l
  val version: String = "version"
  val installedFromGooglePlay: Boolean = true

  val deletedApplication: Int = 1
  val deletedApplications: Int = 2
  val updatedApplication: Int = 1
  val updatedApplications: Int = 2

}

object AppWidgetValues {

  val userHashCode: Int = 1
  val autoAdvanceViewId: Int = 1
  val initialLayout: Int = 1
  val minHeight: Int = 40
  val minResizeHeight: Int = 40
  val minResizeWidth: Int = 40
  val minWidth: Int = 40
  val appWidgetClassName: String = "appWidgetClassName"
  val appWidgetPackageName: String = "appWidgetPackageName"
  val resizeMode: Int = 40
  val updatePeriodMillis: Int = 1
  val label: String = "label"
  val preview: Int = 1

}

object CollectionValues {

  val collectionId: Int = 1
  val nonExistentCollectionId: Int = 10001
  val collectionPosition: Int = 1
  val collectionNewPosition: Int = 5
  val nonExistentCollectionPosition: Int = 10001
  val collectionName: String = "collectionName"
  val collectionType: CollectionType = FreeCollectionType
  val icon: String = "icon"
  val themedColorIndex: Int = 1
  val appsCategoryStr: String = "SOCIAL"
  val appsCategory: NineCardsCategory = NineCardsCategory(appsCategoryStr)
  val originalSharedCollectionId: String = "originalSharedCollection"
  val sharedCollectionId: String = "sharedCollectionId"
  val sharedCollectionSubscribed: Boolean = false
  val publicCollectionStatus: PublicCollectionStatus = NotPublished

  val deletedCollection: Int = 1
  val deletedCollections: Int = 2
  val updatedCollection: Int = 1
  val updatedCollections: Int = 2
  val newCollectionName: String = "newCollectionName"
  val newCollectionIcon: String = "newCollectionIcon"
  val newThemedColorIndex: Int = 1
  val newSharedCollectionId: String = "newSharedCollectionId"
  val nonExistentSharedCollectionId: String = "nonExistentSharedCollectionId"

}

object CardValues {

  val cardId: Int = 1
  val nonExistentCardId: Int = 10001
  val cardPosition: Int = 1
  val term: String = "cardTerm"
  val cardPackageName: String = "cardPackageName"
  val cardType: String = "APP"
  val cardIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val cardImagePath: String = "/card/image/path"
  val notification: String = "notification"

  val cardCollectionId: Int = 1

  val deletedCard: Int = 1
  val deletedCards: Int = 2
  val updatedCard: Int = 1
  val updatedCards: Int = 2

  val cardIdReorder: Int = 1
  val samePositionReorder: Int = 1
  val newPositionReorder: Int = 2
  val newCardName = "newCardName"

}

object DockAppValues {

  val dockAppId: Int = 1
  val nonExistentDockAppId: Int = 10001
  val dockAppName: String = "dockAppName"
  val dockType: String = "APP"
  val dockAppIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val dockAppImagePath: String = "/dockApp/image/path"
  val dockAppPosition: Int = 1

  val deletedDockApp: Int = 1
  val deletedDockApps: Int = 5

}

object FormedValues {

  val itemType: String = "itemType"
  val title: String = "title"
  val formedIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val uriImage: String = "uriImage"

}

object MomentValues {

  val momentId: Int = 1
  val nonExistentMomentId: Int = 10001
  val momentCollectionId: Int = 1
  val timeslotJson: String = """[{"from":"from1","to":"to1","days":[11,12,13]},{"from":"from2","to":"to2","days":[21,22,23]}]"""
  val wifiSeq: Seq[String] = Seq("wifi 1", "wifi 2", "wifi 2")
  val headphone: Boolean = false
  val momentTypeSeq = Seq("HOME", "WORK", "NIGHT", "WALK", "STUDY", "MUSIC", "CAR", "BIKE", "RUNNING")
  val momentType: NineCardsMoment = NineCardsMoment("HOME")
  val homeAppPackageName = "com.google.android.apps.plus"
  val nightAppPackageName = "com.Slack"
  val workAppPackageName = "com.google.android.apps.photos"
  val transitAppPackageName = "com.google.android.apps.maps"

  val deletedMoment: Int = 1
  val deletedMoments: Int = 2
  val updatedMoment: Int = 1

}

object SharedCollectionValues {

  val sharedCollectionPackageName: String = "sharedCollectionPackage"
  val sharedCollectionPackageTitle: String = "sharedCollectionPackageTitle"
  val sharedCollectionPackageIcon: String = "sharedCollectionPackageIcon"
  val sharedCollectionPackageStars: Double = 4.2d
  val sharedCollectionDownloads: String = "28"
  val sharedCollectionFree: Boolean = true

  val sharedCollectionId: String = "sharedCollectionId"
  val publishedOn: Long = 1l
  val author: String = "author"
  val sharedCollectionName: String = "sharedCollectionName"
  val sharedCollectionPackagesStr: Seq[String] = Seq("sharedCollectionPackage1", "sharedCollectionPackage2", "sharedCollectionPackage2")
  val views: Int = 29
  val subscriptions: Int = 6
  val sharedCollectionPublicCollectionStatus: PublicCollectionStatus = NotPublished
  val sharedCollectionCategory: NineCardsCategory = Communication
  val sharedCollectionIcon: String = "sharedCollectionIcon"
  val community: Boolean = false

  val typeShareCollection: TypeSharedCollection = TopSharedCollection
  val offset: Int = 0
  val limit: Int = 50

}

object UserValues {

  val userId: Int = 1
  val newUserId: Int = 10
  val nonExistentUserId: Int = 10001
  val email: String = "user@email.com"
  val sessionToken: String = "sessionToken"
  val apiKey: String = "apiKey"
  val deviceToken: String = "deviceToken"
  val anotherDeviceToken = "anotherDeviceToken"
  val marketToken: String = "marketToken"
  val userDeviceName: String = "deviceName"
  val anotherUserDeviceName = "anotherDeviceName"
  val userDeviceId = "deviceId"
  val deviceCloudId: String = "deviceCloudId"
  val anotherDeviceCloudId = "anotherDeviceCloudId"
  val userName: String = "userName"
  val avatar: String = "avatar"
  val cover: String = "cover"
  val androidId = "androidId"
  val emailTokenId = "emailTokenId"

  val deletedUser: Int = 1
  val deletedUsers: Int = 2
  val updatedUser: Int = 1
  val updatedUsers: Int = 2

}


object UserV1Values {

  val baseUrl = "http://baseUrl"
  val statusCode = 200

  val userV1DeviceId: String = "userV1DeviceId"
  val userV1DeviceName: String = "userV1DeviceName"
  val userV1DeviceToken: String = "userV1DeviceToken"
  val userV1Token: String = "userV1Token"
  val userV1MarketToken: String = "userV1MarketToken"
  val userV1ApiKey: String = "userV1ApiKey"
  val userV1AndroidId: String = "userV1AndroidId"
  val userV1TokenId: String = "userV1TokenId"

  val userV1AppId: String = "userV1AppId"
  val userV1AppKey: String = "userV1AppKey"
  val userV1Localization: String = "EN"

  val userV1Id: String = "userV1Id"
  val userV1SessionToken: String = "userV1SessionToken"
  val userV1email: String = "userV1@email.com"
  val userV1Name: String = "userV1Name"
  val userV1Password: String = "userV1Password"
  val userV1GoogleDeviceName: String = "userV1GoogleDeviceName"
  val userV1GoogleDeviceId: String = "userV1GoogleDeviceId"
  val userV1SecretToken: String = "userV1SecretToken"
  val userV1Permission: String = "userV1Permission"
  val authFacebookId: String = "authFacebookId"
  val authFacebookAccessToken: String = "authFacebookAccessToken"
  val authFacebookExpirationDate: Long = 10l
  val authTwitterId: String = "authTwitterId"
  val authTwitterScreenName: String = "authTwitterScreenName"
  val authTwitterConsumerKey: String = "authTwitterConsumerKey"
  val authTwitterConsumerSecret: String = "authTwitterConsumerSecret"
  val authTwitterAuthToken: String = "authTwitterAuthToken"
  val authTwitterAuthTokenSecret: String = "authTwitterAuthTokenSecret"
  val authTwitterKey: String = "authTwitterKey"
  val authTwitterSecretKey: String = "authTwitterSecretKey"
  val authAnonymousId: String = "authAnonymousId"

  val userConfigPlusDisplayName: String = "userConfigPlusDisplayName"
  val userConfigPlusImageType: Int = 0
  val userConfigPlusImageUrl: String = "userConfigPlusImageUrl"
  val userConfigPlusSecureUrl: String = "userConfigPlusSecureUrl"

  val products: Seq[String] = Seq("product1", "product2", "product3")
  val friendsReferred: Int = 5
  val themesShared: Int = 2
  val collectionsShared: Int = 10
  val customCollections: Int = 4
  val earlyAdopter: Boolean = false
  val communityMember: Boolean = false
  val joinedThrough: Option[String] = None
  val tester: Boolean = false


}


object WidgetValues {

  val widgetId: Int = 1
  val nonExistentWidgetId: Int = 10001
  val widgetMomentId: Int = 1
  val nonExistentWidgetMomentId: Int = 1
  val widgetPackageName: String = "widgetPackageName"
  val widgetClassName: String = "widgetClassName"
  val appWidgetId: Int = 1
  val nonExistentAppWidgetId: Int = 10001
  val startX: Int = 0
  val startY: Int = 0
  val spanX: Int = 1
  val spanY: Int = 1
  val widgetType: String = "APP"
  val label: String = "widget label"
  val widgetImagePath: String = "/widget/image/path"
  val widgetIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val displaceX: Int = 2
  val displaceY: Int = 2
  val increaseX: Int = 1
  val increaseY: Int = 1

  val deletedWidget: Int = 1
  val deletedWidgets: Int = 2
  val updatedWidget: Int = 1
  val updatedWidgets: Int = 2

}