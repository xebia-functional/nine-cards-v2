package com.fortysevendeg.ninecardslauncher.process.userconfig.impl

import com.fortysevendeg.ninecardslauncher.process.commons.types.Game
import com.fortysevendeg.ninecardslauncher.process.types.{AppCardType, AppsCollectionType}
import com.fortysevendeg.ninecardslauncher.services.api.RequestConfig
import com.fortysevendeg.ninecardslauncher.services.api.models._
import play.api.libs.json.JsString

trait UserConfigProcessData {

  val statusCodeOk = 200

  val requestConfig = RequestConfig("fake-device-id", "fake-token")

  val email = "example@47deg.com"

  val name = "George"

  val imageUrl = "http://www.47deg.com/image.jpg"

  val deviceIdPrefix = "fake-device-id"

  val firstDeviceId = "fake-device-id-0"

  val noDeviceId = "dont-exits-device-id"

  val deviceName = "Nexus"

  val collectionName = "Example Collection"

  val collectionIcon = "GAME"

  val collectionType = AppsCollectionType

  val collectionCategory = Game

  val itemType = AppCardType

  def createUserConfigCollectionItem(count: Int = 8): Seq[UserConfigCollectionItem] =
    (0 until count) map {
      item =>
        UserConfigCollectionItem(
          itemType = itemType.name,
          title = s"Item $item",
          metadata = JsString(""),
          categories = Option(Seq.empty)
        )
    }

  def createUserConfigCollection(count: Int = 5) =
    (0 until count) map {
      item =>
        UserConfigCollection(
          name = collectionName,
          originalSharedCollectionId = None,
          sharedCollectionId = None,
          sharedCollectionSubscribed = Option(true),
          items = createUserConfigCollectionItem(),
          collectionType = collectionType.name,
          constrains = Seq.empty,
          wifi = Seq.empty,
          occurrence = Seq.empty,
          icon = collectionIcon,
          radius = 0,
          lat = 0,
          lng = 0,
          alt = 0,
          category = Option(collectionCategory.name)
        )
    }

  def createUserConfigDevice(count: Int = 3) =
    (0 until count) map {
      item =>
        UserConfigDevice(
          deviceId = s"$deviceIdPrefix-$item",
          deviceName = s"$deviceName $item",
          collections = createUserConfigCollection()
        )
    }

  val userConfig = UserConfig(
    _id = "fake-id",
    email = email,
    plusProfile = UserConfigPlusProfile(
      displayName = name,
      profileImage = UserConfigProfileImage(
        imageType = 0,
        imageUrl = imageUrl
      )
    ),
    devices = createUserConfigDevice(),
    geoInfo = UserConfigGeoInfo(
      homeMorning = None,
      homeNight = None,
      work = None,
      current = None
    ),
    status = UserConfigStatusInfo(
      products = Seq.empty,
      friendsReferred = 0,
      themesShared = 0,
      collectionsShared = 0,
      customCollections = 0,
      earlyAdopter = false,
      communityMember = true,
      joinedThrough = None,
      tester = false
    )
  )

}
