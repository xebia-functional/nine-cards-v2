package cards.nine.commons.test.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.models.reads.MomentImplicits
import cards.nine.models.{Moment, MomentData, MomentTimeSlot}
import cards.nine.models.types.NineCardsMoment
import play.api.libs.json.Json

trait MomentTestData extends WidgetTestData {

  import MomentImplicits._

  def createMomentData(
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: NineCardsMoment = momentType): MomentData =
    MomentData(
      collectionId = collectionId,
      timeslot = timeslot,
      wifi = wifi,
      headphone = headphone,
      momentType = Option(momentType),
      widgets = Option(seqWidgetData))

  def createSeqMoment(
    num: Int = 5,
    id: Int = momentId,
    collectionId: Option[Int] = collectionIdOption,
    timeslot: Seq[MomentTimeSlot] = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi: Seq[String] = wifiSeq,
    headphone: Boolean = headphone,
    momentType: NineCardsMoment = momentType): Seq[Moment] = List.tabulate(num)(
    item =>
      Moment(
        id = id + item,
        collectionId = collectionId,
        timeslot = timeslot,
        wifi = wifi,
        headphone = headphone,
        momentType = Option(momentType),
        widgets = Option(seqWidgetData)))

  def createSeqMomentData(
    num: Int = 5) :Seq[MomentData]  =
    List.tabulate(num)(item => createMomentData())

  val seqMoment: Seq[Moment] = createSeqMoment()

  val moment = seqMoment(0)
  val momentData = createMomentData()
  val seqMomentData = createSeqMomentData()


}
