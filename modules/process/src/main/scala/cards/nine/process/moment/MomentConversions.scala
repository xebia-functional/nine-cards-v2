package cards.nine.process.moment

import cards.nine
import cards.nine.models.ApplicationData
import cards.nine.models.types.AppCardType
import cards.nine.process.commons.CommonConversions
import cards.nine.process.commons.models.{Collection, Moment, MomentWithCollection, PrivateCard}
import cards.nine.services.persistence._

trait MomentConversions extends CommonConversions {

  def toMomentSeq(servicesMomentSeq: Seq[nine.models.Moment]) = servicesMomentSeq map toMoment

  def toAddCardRequestSeq(items: Seq[ApplicationData]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromAppMoment(zipped._1, zipped._2))

  def toAddCardRequestFromAppMoment(item: ApplicationData, position: Int): AddCardRequest = AddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = None)

  def toPrivateCard(app: ApplicationData): PrivateCard =
    PrivateCard(
      term = app.name,
      packageName = Some(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = None)

  def toMomentWithCollection(moment: Moment, collection: Collection): MomentWithCollection =
    MomentWithCollection(
      collection = collection,
      timeslot = moment.timeslot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType
    )

}
