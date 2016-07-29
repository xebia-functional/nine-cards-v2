package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.process.user.models.{User, UserProfile}
import play.api.libs.json.Json

import scala.util.Random

trait LauncherPresenterData {

  val collectionId = Random.nextInt(10)
  val position = Random.nextInt(10)
  val nonExistentCollectionId = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardCategory = appsCategories(Random.nextInt(appsCategories.length))
  val appsCategoryName = appsCategory.name
  val constrains: String = Random.nextString(5)
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()

  val cards = Seq(
    Card(
      id = 1,
      position = 0,
      term = "App 1",
      packageName = Some("package.name"),
      cardType = AppCardType,
      intent = NineCardIntent(NineCardIntentExtras()),
      imagePath = "imagePath",
      notification = Some("notification")
    )
  )

  val momentType = Option("HOME")

  val momentCollection = Moment(
    collectionId = Some(1),
    timeslot = Seq(MomentTimeSlot(from = "from-1", to = "to-1", days = 0 to 4)),
    wifi = Seq("wifi-1"),
    headphone = true,
    momentType = momentType map (NineCardsMoment(_)))

  val moment = Moment(
    collectionId = None,
    timeslot = Seq(MomentTimeSlot(from = "from-2", to = "to-2", days = 5 to 6)),
    wifi = Seq("wifi-2"),
    headphone = false,
    momentType = momentType map (NineCardsMoment(_)))

  val moments = Seq(momentCollection, moment)

  val collection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    cards = cards,
    moment = Option(moment),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val packageName = "com.fortysevendeg.scala.android"
  val imagePath = "imagePath1"

  val intentStr = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val intent = Json.parse(intentStr).as[NineCardIntent]

  val dockApp = DockApp(
    name = packageName,
    dockType = AppDockType,
    intent = intent,
    imagePath = imagePath,
    position = 0)

  val userId = "fake-user-id"

  val userToken = "fake-user-token"

  val email = "example@47deg.com"

  val user = User(
    id = 1,
    sessionToken = Option(userToken),
    email = Option(email),
    userId = Option(userId),
    installationId = None,
    deviceToken = None,
    androidToken = None,
    deviceName = None,
    deviceCloudId = None,
    userProfile = UserProfile(name = None, avatar = None, cover = None))

}
