package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedApp
import com.fortysevendeg.ninecardslauncher.process.commons.CommonConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.PrivateCard
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => ServicesApp, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}

trait MomentConversions extends CommonConversions {

  def toMomentSeq(servicesMomentSeq: Seq[ServicesMoment]) = servicesMomentSeq map toMoment

  def toApp(servicesApp: ServicesApp): App = App(
      name = servicesApp.name,
      packageName = servicesApp.packageName,
      className = servicesApp.className,
      imagePath = servicesApp.imagePath)

  def toApp(unformedApp: UnformedApp): App = App(
      name = unformedApp.name,
      packageName = unformedApp.packageName,
      className = unformedApp.className,
      imagePath = unformedApp.imagePath)

  def toAddCardRequestSeq(items: Seq[App]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromAppMoment(zipped._1, zipped._2))

  def toAddCardRequestFromAppMoment(item: App, position: Int): AddCardRequest = AddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toPrivateCard(app: App): PrivateCard =
    PrivateCard(
      term = app.name,
      packageName = Some(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = app.imagePath)

}
