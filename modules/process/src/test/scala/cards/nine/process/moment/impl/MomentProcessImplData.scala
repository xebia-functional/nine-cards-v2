package cards.nine.process.moment.impl

import cards.nine.models._
import cards.nine.models.types.CardType._
import cards.nine.models.types.CollectionType._
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types._
import org.joda.time.DateTime

import scala.util.Random

trait MomentProcessImplData {

  val collectionId = Random.nextInt(10)
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardsCategory = appsCategories(Random.nextInt(appsCategories.length))
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()
  val publicCollectionStatusSeq = Seq(NotPublished, PublishedByMe, PublishedByOther, Subscribed)
  val publicCollectionStatus = publicCollectionStatusSeq(Random.nextInt(publicCollectionStatusSeq.size))

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val category1 = "Communication"
  val imagePath1 = "imagePath1"
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val appId = Random.nextInt(10)
  val momentId = Random.nextInt(10)
  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = Random.nextString(5)
  val className = Random.nextString(5)
  val cardType: CardType = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["Communication"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val from = "8:00"
  val to = "19:00"
  val days = Seq(0, 1, 1, 1, 1, 1, 0)

  val collectionId1 = 1
  val homeAppPackageName = "com.google.android.apps.plus"
  val nightAppPackageName = "com.Slack"
  val workAppPackageName = "com.google.android.apps.photos"
  val transitAppPackageName = "com.google.android.apps.maps"
  val seqMomentType = Seq("HOME", "WORK", "NIGHT", "WALK", "STUDY", "MUSIC", "CAR", "BIKE", "RUNNING")

  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)

  val ssid: String = Random.nextString(5)

  val item: Int = Random.nextInt(5)

  val homeApp =
    ApplicationData(
      name = name,
      packageName = homeAppPackageName,
      className = className1,
      category = NineCardsCategory(category1),
      dateInstalled = dateInstalled1,
      dateUpdated = dateUpdate1,
      version = version1,
      installedFromGooglePlay = installedFromGooglePlay1)

  val workApp = homeApp.copy(packageName = workAppPackageName)

  val nightApp = homeApp.copy(packageName = nightAppPackageName)

  val transitApp = homeApp.copy(packageName = transitAppPackageName)

  val now = DateTime.now()

  val nowMorning = now.withDayOfWeek(2).withTime(10, 0, 0, 0)
  val nowLateNight = now.withDayOfWeek(2).withTime(3, 0, 0, 0)
  val nowAfternoon = now.withDayOfWeek(2).withTime(18, 0, 0, 0)
  val nowNight = now.withDayOfWeek(2).withTime(21, 0, 0, 0)
  val nowMorningWeekend = now.withDayOfWeek(7).withTime(10, 0, 0, 0)

  val homeMorningId = 1
  val homeMorningCollectionId = Option(1)
  val homeWifi = Seq("homeWifi")
  val homeMorningFrom = "08:00"
  val homeMorningTo = "19:00"
  val homeMorningDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val homeMorningTimeSlot =
    Seq(MomentTimeSlot(
      from = homeMorningFrom,
      to = homeMorningTo,
      days = homeMorningDays))

  val homeMorningMoment =
    Moment(
      id = homeMorningId,
      collectionId = homeMorningCollectionId,
      timeslot = homeMorningTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(seqMomentType(0))))

  val workId = 2
  val workCollectionId = Option(2)
  val workWifi = Seq("workWifi")
  val workFrom = "08:00"
  val workTo = "17:00"
  val workDays = Seq(0, 1, 1, 1, 1, 1, 0)

  val workTimeSlot =
    Seq(MomentTimeSlot(
      from = workFrom,
      to = workTo,
      days = workDays))

  val workMoment =
    Moment(
      id = workId,
      collectionId = workCollectionId,
      timeslot = workTimeSlot,
      wifi = workWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(seqMomentType(1))))

  val homeNightId = 3
  val homeNightCollectionId = Option(3)
  val homeNightFrom1 = "19:00"
  val homeNightTo1 = "23:59"
  val homeNightFrom2 = "00:00"
  val homeNightTo2 = "08:00"
  val homeNightDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val homeNightTimeSlot =
    Seq(
      MomentTimeSlot(
        from = homeNightFrom1,
        to = homeNightTo1,
        days = homeNightDays),
      MomentTimeSlot(
        from = homeNightFrom2,
        to = homeNightTo2,
        days = homeNightDays))

  val homeNightMoment =
    Moment(
      id = homeNightId,
      collectionId = homeNightCollectionId,
      timeslot = homeNightTimeSlot,
      wifi = homeWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(seqMomentType(2))))

  val transitId = 4
  val transitCollectionId = Option(4)
  val transitWifi = Seq.empty
  val transitFrom1 = "00:00"
  val transitTo1 = "23:59"
  val transitDays = Seq(1, 1, 1, 1, 1, 1, 1)

  val transitTimeSlot =
    Seq(
      MomentTimeSlot(
        from = transitFrom1,
        to = transitTo1,
        days = transitDays))

  val transitMoment =
    Moment(
      id = transitId,
      collectionId = transitCollectionId,
      timeslot = transitTimeSlot,
      wifi = transitWifi,
      headphone = false,
      momentType = Option(NineCardsMoment(seqMomentType(3))))

}
