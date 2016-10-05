package cards.nine.app.ui.launcher

import cards.nine.models.types.CollectionType._
import cards.nine.models.types.NineCardCategory._
import cards.nine.models.types._
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models._
import cards.nine.process.device.models.DockApp
import cards.nine.process.user.models.{User, UserProfile}
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

  def generateOptionId(id: String) =
    Random.nextBoolean() match {
      case true => None
      case false => Some(id)
    }

  val sharedCollectionIdOption = generateOptionId(sharedCollectionId)
  val originalSharedCollectionIdOption = generateOptionId(originalSharedCollectionId)
  val sharedCollectionSubscribed: Boolean =
    if (sharedCollectionId == originalSharedCollectionId) Random.nextBoolean()
    else false

  def determinePublicCollectionStatus(): PublicCollectionStatus =
    if (sharedCollectionIdOption.isDefined && sharedCollectionSubscribed) Subscribed
    else if (sharedCollectionIdOption.isDefined && originalSharedCollectionIdOption == sharedCollectionIdOption) PublishedByOther
    else if (sharedCollectionIdOption.isDefined) PublishedByMe
    else NotPublished

  val publicCollectionStatus = determinePublicCollectionStatus()

  val cards = Seq(
    Card(
      id = 1,
      position = 0,
      term = "App 1",
      packageName = Some("package.name"),
      cardType = AppCardType,
      intent = NineCardIntent(NineCardIntentExtras()),
      imagePath = Some("imagePath"),
      notification = Some("notification")
    )
  )

  val momentType = Option("HOME")

  val moment = Moment(
    id = 1,
    collectionId = None,
    timeslot = Seq(MomentTimeSlot(from = "from-2", to = "to-2", days = 5 to 6)),
    wifi = Seq("wifi-2"),
    headphone = false,
    momentType = momentType map (NineCardsMoment(_)))

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
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

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

  val apiKey = "fake-api-key"

  val userToken = "fake-user-token"

  val email = "example@47deg.com"

  val user = User(
    id = 1,
    sessionToken = Option(userToken),
    email = Option(email),
    apiKey = Option(apiKey),
    deviceToken = None,
    marketToken = None,
    deviceName = None,
    deviceCloudId = None,
    userProfile = UserProfile(name = None, avatar = None, cover = None))

}
