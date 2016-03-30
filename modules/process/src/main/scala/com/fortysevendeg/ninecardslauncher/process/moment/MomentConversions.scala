package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.process.commons.CommonConversions
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App, MomentTimeSlot}

trait MomentConversions extends CommonConversions {

  def toAddCardRequestSeq(items: Seq[App]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromAppMoment(zipped._1, zipped._2))

  def toAddCardRequestFromAppMoment(item: App, position: Int) = AddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toAddMomentRequest(collectionId: Int, moment: NineCardsMoment) =
    AddMomentRequest(
      collectionId = Option(collectionId),
      timeslot = toMomentTimeSlotSeq(moment),
      wifi = Seq.empty,
      headphone = false)

  def toMomentTimeSlotSeq(moment: NineCardsMoment) =
    moment match {
      case HomeMorningMoment => Seq(MomentTimeSlot(from = "8:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(MomentTimeSlot(from = "8:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "0:00", to = "8:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
    }

}
