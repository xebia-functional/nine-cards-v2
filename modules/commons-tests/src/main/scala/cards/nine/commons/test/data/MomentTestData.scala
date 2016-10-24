package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.models.reads.MomentImplicits
import cards.nine.models.types.NineCardsMoment
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
    momentType = Option(NineCardsMoment(momentTypeSeq(num))),
    widgets = Option(seqWidgetData))

  val moment: Moment = moment(0)
  val seqMoment: Seq[Moment] = Seq(moment(0), moment(1), moment(2))

  val momentData: MomentData = moment.toData
  val seqMomentData: Seq[MomentData] = seqMoment map (_.toData)

}
