package cards.nine.process.moment.impl

import cards.nine.commons.test.data.MomentValues._
import cards.nine.models._
import cards.nine.models.types.CardType._
import cards.nine.models.types.CollectionType._
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types._
import org.joda.time.DateTime

import scala.util.Random

trait MomentProcessImplData {

  val collectionId                        = Random.nextInt(10)
  val name: String                        = Random.nextString(5)
  val collectionType: CollectionType      = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String                        = Random.nextString(5)
  val themedColorIndex: Int               = Random.nextInt(10)
  val appsCategory: NineCardsCategory     = appsCategories(Random.nextInt(appsCategories.length))
  val originalSharedCollectionId: String  = Random.nextString(5)
  val sharedCollectionId: String          = Random.nextString(5)
  val sharedCollectionSubscribed: Boolean = Random.nextBoolean()
  val publicCollectionStatusSeq           = Seq(NotPublished, PublishedByMe, PublishedByOther)
  val publicCollectionStatus = publicCollectionStatusSeq(
    Random.nextInt(publicCollectionStatusSeq.size))

  val name1                    = "Scala Android"
  val packageName1             = "com.fortysevendeg.scala.android"
  val className1               = "ScalaAndroidActivity"
  val category1                = "Communication"
  val imagePath1               = "imagePath1"
  val dateInstalled1           = 1L
  val dateUpdate1              = 1L
  val version1                 = "22"
  val installedFromGooglePlay1 = true

  val appId                = Random.nextInt(10)
  val momentId             = Random.nextInt(10)
  val cardId               = Random.nextInt(10)
  val position: Int        = Random.nextInt(10)
  val term: String         = Random.nextString(5)
  val packageName          = Random.nextString(5)
  val className            = Random.nextString(5)
  val cardType: CardType   = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String    = Random.nextString(5)
  val notification: String = Random.nextString(5)
  val intent =
    """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["Communication"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val from = "8:00"
  val to   = "19:00"
  val days = Seq(0, 1, 1, 1, 1, 1, 0)

  val collectionId1         = 1
  val homeAppPackageName    = "com.google.android.apps.plus"
  val nightAppPackageName   = "com.Slack"
  val workAppPackageName    = "com.google.android.apps.photos"
  val transitAppPackageName = "com.google.android.apps.maps"

  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int  = Random.nextInt(8)
  val spanY: Int  = Random.nextInt(8)

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

  val nowMorning        = now.withDayOfWeek(2).withTime(10, 0, 0, 0)
  val nowAfternoon      = now.withDayOfWeek(2).withTime(18, 30, 0, 0)
  val nowNight          = now.withDayOfWeek(2).withTime(21, 0, 0, 0)
  val nowLateNight      = now.withDayOfWeek(2).withTime(3, 0, 0, 0)
  val nowMorningWeekend = now.withDayOfWeek(7).withTime(10, 0, 0, 0)

  val startHomeHour  = 8
  val endHomeHour    = 19
  val startWorkHour  = 8
  val endWorkHour    = 17
  val startNightHour = 20
  val endNightHour   = 8
  val startStudyHour = 8
  val endStudyHour   = 18

  def toSlotTime(start: Int, end: Int, days: Seq[Int]): Seq[MomentTimeSlot] = {
    val startTime = if (start < 10) s"0$start:00" else s"$start:00"
    val endTime   = if (end < 10) s"0$end:00" else s"$end:00"
    if (start > end) {
      Seq(
        MomentTimeSlot(from = startTime, to = "23:59", days = days),
        MomentTimeSlot(from = "00:00", to = endTime, days = days))
    } else {
      Seq(MomentTimeSlot(from = startTime, to = endTime, days = days))
    }
  }

  val homeWifiSSID = "homeSSID"
  val homeMoment = Moment(
    id = momentId + 10,
    collectionId = Some(momentCollectionId + 10),
    timeslot = toSlotTime(startHomeHour, endHomeHour, days = Seq(1, 1, 1, 1, 1, 1, 1)),
    wifi = Seq(homeWifiSSID),
    bluetooth = Seq.empty,
    headphone = false,
    momentType = HomeMorningMoment,
    widgets = None)

  val workWifiSSID = "workSSID"
  val workMoment = Moment(
    id = momentId + 11,
    collectionId = Some(momentCollectionId + 11),
    timeslot = toSlotTime(startWorkHour, endWorkHour, days = Seq(0, 1, 1, 1, 1, 1, 0)),
    wifi = Seq(workWifiSSID),
    bluetooth = Seq.empty,
    headphone = false,
    momentType = WorkMoment,
    widgets = None)

  val nightMoment = Moment(
    id = momentId + 12,
    collectionId = Some(momentCollectionId + 12),
    timeslot = toSlotTime(startNightHour, endNightHour, days = Seq(1, 1, 1, 1, 1, 1, 1)),
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = false,
    momentType = HomeNightMoment,
    widgets = None)

  val studyMoment = Moment(
    id = momentId + 13,
    collectionId = Some(momentCollectionId + 13),
    timeslot = toSlotTime(startStudyHour, endStudyHour, days = Seq(0, 1, 1, 1, 1, 1, 0)),
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = false,
    momentType = StudyMoment,
    widgets = None)

  val musicMoment = Moment(
    id = momentId + 14,
    collectionId = Some(momentCollectionId + 14),
    timeslot = Seq.empty,
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = true,
    momentType = MusicMoment,
    widgets = None)

  val carMoment = Moment(
    id = momentId + 15,
    collectionId = Some(momentCollectionId + 15),
    timeslot = Seq.empty,
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = false,
    momentType = CarMoment,
    widgets = None)

  val sportsMoment = Moment(
    id = momentId + 16,
    collectionId = Some(momentCollectionId + 16),
    timeslot = Seq.empty,
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = false,
    momentType = SportMoment,
    widgets = None)

  val outAndAboutMoment = Moment(
    id = momentId + 18,
    collectionId = Some(momentCollectionId + 18),
    timeslot = Seq.empty,
    wifi = Seq.empty,
    bluetooth = Seq.empty,
    headphone = false,
    momentType = OutAndAboutMoment,
    widgets = None)

  val allMoments = Seq(
    homeMoment,
    workMoment,
    nightMoment,
    studyMoment,
    musicMoment,
    sportsMoment,
    carMoment,
    outAndAboutMoment)

}
