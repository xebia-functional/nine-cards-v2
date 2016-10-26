package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.models.reads.MomentImplicits
import cards.nine.models.types._
import cards.nine.models.{Moment, MomentData, MomentTimeSlot}
import play.api.libs.json.Json

trait MomentTestData extends WidgetTestData {

  import MomentImplicits._

  def moment(num: Int = 0) = Moment(
    id = momentId + num,
    collectionId = Option(momentCollectionId + num),
    timeslot = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi = Seq(wifiSeq(num)),
    headphone = headphone,
    momentType = NineCardsMoment(momentTypeSeq(num)),
    widgets = Option(seqWidgetData))

  val moment: Moment = moment(0)
  val seqMoment: Seq[Moment] = Seq(moment(0), moment(1), moment(2))

  val momentData: MomentData = moment.toData
  val seqMomentData: Seq[MomentData] = seqMoment map (_.toData)

  //TODO: Remove when solves this method in the implementation
  private[this] def toMomentTimeSlotSeq(moment: NineCardsMoment): Seq[MomentTimeSlot] =
    moment match {
      case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case MusicMoment => Seq.empty
      case CarMoment => Seq.empty
      case SportMoment => Seq.empty
      case OutAndAboutMoment => Seq(MomentTimeSlot(from = "00:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case _ => Seq.empty
    }

  def momentData(infoMoment: (NineCardsMoment, Option[String])) =
    MomentData(
      collectionId = None,
      timeslot = toMomentTimeSlotSeq(infoMoment._1),
      wifi = infoMoment._2.toSeq,
      headphone = false,
      momentType = infoMoment._1)


  val minMomentsWithWifi = Seq(momentData(NineCardsMoment.defaultMoment, None))
  val nightMoment = Seq(momentData(HomeNightMoment, Option("wifi")))

}
