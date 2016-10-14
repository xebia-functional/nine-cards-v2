package cards.nine.commons.test.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models.types._

import scala.util.Random

object ApplicationValues {

  val keyword = "fake-keyword"
  val appId: Int = Random.nextInt(10)
  val name: String = Random.nextString(5)
  val resourceIcon: Int = Random.nextInt(10)
  val dateInstalled: Long = Random.nextLong()
  val dateUpdate: Long = Random.nextLong()
  val version: String = Random.nextString(5)
  val installedFromGooglePlay: Boolean = Random.nextBoolean()
  val packageName: String = Random.nextString(5)
  val className: String = Random.nextString(5)
  val category: String = Random.nextString(5)

}

object CollectionValues {

  val collectionId: Int = Random.nextInt(10)
  val nonExistentCollectionId: Int = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val position: Int = Random.nextInt(10)
  val collectionType: CollectionType = FreeCollectionType
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: String = "MISC"
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val nonExistentSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()
  val publicCollectionStatus: PublicCollectionStatus = NotPublished

}

object CardValues {

  val cardId: Int = Random.nextInt(10)
  val nonExistentCardId: Int = Random.nextInt(10) + 100
  val nonExistentPosition: Int = Random.nextInt(10) + 100
  val term: String = Random.nextString(5)
  val cardType: String = "APP"
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val packageName: String = Random.nextString(5)

}

object DockAppValues {

  val dockAppId: Int = Random.nextInt(10)
  val nonExistentDockAppId: Int = Random.nextInt(10) + 100
  val dockType: String = "APP"
  val name: String = Random.nextString(5)
  val position: Int = Random.nextInt(10)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val imagePath: String = Random.nextString(5)

}

object MomentValues {

  val momentId: Int = Random.nextInt(10)
  val nonExistentMomentId: Int = Random.nextInt(10) + 100
  val wifi1: String = Random.nextString(5)
  val wifi2: String = Random.nextString(5)
  val wifi3: String = Random.nextString(5)
  val headphone: Boolean = Random.nextBoolean()
  val wifiSeq: Seq[String] = Seq(wifi1, wifi2, wifi3)
  val wifiString: String = wifiSeq.mkString(",")
  val timeslotJson: String = """[{"from":"from1","to":"to1","days":[11,12,13]},{"from":"from2","to":"to2","days":[21,22,23]}]"""
  val collectionIdOption = Option(collectionId)
  val momentType: NineCardsMoment = HomeMorningMoment
  val momentTypeStr: String = "HOME"

}

object UserValues {

  val uId: Int = Random.nextInt(10)
  val nonExistentUserId: Int = Random.nextInt(10) + 100
  val email: String = Random.nextString(5)
  val sessionToken: String = Random.nextString(5)
  val apiKey: String = Random.nextString(5)
  val deviceToken: String = Random.nextString(5)
  val marketToken: String = Random.nextString(5)
  val nameUser: String = Random.nextString(5)
  val avatar: String = Random.nextString(5)
  val cover: String = Random.nextString(5)
  val deviceName: String = Random.nextString(5)
  val deviceCloudId: String = Random.nextString(5)

}

object WidgetValues {

  val widgetId: Int = Random.nextInt(10)
  val widgetType: String = "APP"
  val appWidgetId: Int = Random.nextInt(10) + 1
  val packageName: String = Random.nextString(5)
  val className: String = Random.nextString(5)
  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)
  val label: String = Random.nextString(5)
  val widgetImagePath: String = Random.nextString(5)
  val labelOption = Option(label)
  val widgetImagePathOption = Option(widgetImagePath)
  val widgetIntentOption = Option(intent)

  val nonExistentWidgetId: Int = Random.nextInt(10) + 100
  val nonExistentAppWidgetId: Int = Random.nextInt(10) + 100

}