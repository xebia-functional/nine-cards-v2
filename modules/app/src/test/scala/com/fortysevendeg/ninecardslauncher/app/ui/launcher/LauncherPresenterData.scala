package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.process.commons.types.CollectionType._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppDockType, CollectionType, NineCardCategory}
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

  val collection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
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
    userProfile = UserProfile(name = None, avatar = None, cover = None))

}
