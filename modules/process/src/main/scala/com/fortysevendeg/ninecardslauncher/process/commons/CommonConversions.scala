package com.fortysevendeg.ninecardslauncher.process.commons

import com.fortysevendeg.ninecardslauncher.process.collection.models.FormedMoment
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Moment, MomentTimeSlot, _}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.SaveMomentRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.AddMomentRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}

trait CommonConversions extends NineCardIntentConversions {

  def toCollection(servicesCollection: ServicesCollection): Collection = Collection(
    id = servicesCollection.id,
    position = servicesCollection.position,
    name = servicesCollection.name,
    collectionType = CollectionType(servicesCollection.collectionType),
    icon = servicesCollection.icon,
    themedColorIndex = servicesCollection.themedColorIndex,
    appsCategory = servicesCollection.appsCategory map (NineCardCategory(_)),
    originalSharedCollectionId = servicesCollection.originalSharedCollectionId,
    sharedCollectionId = servicesCollection.sharedCollectionId,
    sharedCollectionSubscribed = servicesCollection.sharedCollectionSubscribed,
    cards = servicesCollection.cards map toCard,
    moment = servicesCollection.moment map toMoment)

  def toCard(servicesCard: ServicesCard): Card = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = CardType(servicesCard.cardType),
    intent = jsonToNineCardIntent(servicesCard.intent),
    imagePath = servicesCard.imagePath,
    notification = servicesCard.notification)

  def toMoment(servicesMoment: ServicesMoment): Moment = Moment(
    id = servicesMoment.id,
    collectionId = servicesMoment.collectionId,
    timeslot = servicesMoment.timeslot map toTimeSlot,
    wifi = servicesMoment.wifi,
    headphone = servicesMoment.headphone,
    momentType = servicesMoment.momentType map (NineCardsMoment(_)))

  def toTimeSlot(servicesMomentTimeSlot:  ServicesMomentTimeSlot): MomentTimeSlot = MomentTimeSlot(
    from = servicesMomentTimeSlot.from,
    to = servicesMomentTimeSlot.to,
    days = servicesMomentTimeSlot.days)

  def toAddMomentRequest(moment: SaveMomentRequest): AddMomentRequest =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))

  def toAddMomentRequest(moment: Moment): AddMomentRequest =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))

  def toAddMomentRequest(moment: FormedMoment): AddMomentRequest =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))

  def toAddMomentRequest(collectionId: Option[Int], moment: NineCardsMoment): AddMomentRequest =
    AddMomentRequest(
      collectionId = collectionId,
      timeslot = toServicesMomentTimeSlotSeq(moment),
      wifi = toWifiSeq(moment),
      headphone = false,
      momentType = Option(moment.name))

  def toServicesMomentTimeSlot(timeSlot: MomentTimeSlot): ServicesMomentTimeSlot =
    ServicesMomentTimeSlot(
      from = timeSlot.from,
      to = timeSlot.to,
      days = timeSlot.days)

  def toWifiSeq(moment: NineCardsMoment): Seq[Nothing] =
    moment match {
      case HomeMorningMoment => Seq.empty
      case WorkMoment => Seq.empty
      case HomeNightMoment => Seq.empty
    }

  def toServicesMomentTimeSlotSeq(moment: NineCardsMoment): Seq[ServicesMomentTimeSlot] =
    moment match {
      case HomeMorningMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(ServicesMomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), ServicesMomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
    }

}
