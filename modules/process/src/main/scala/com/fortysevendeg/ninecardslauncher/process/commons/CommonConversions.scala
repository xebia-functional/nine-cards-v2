package com.fortysevendeg.ninecardslauncher.process.commons

import com.fortysevendeg.ninecardslauncher.process.commons.models.{Moment, MomentTimeSlot, _}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}

trait CommonConversions extends NineCardIntentConversions {

  def toCollection(servicesCollection: ServicesCollection) = Collection(
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

  def toCard(servicesCard: ServicesCard) = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = CardType(servicesCard.cardType),
    intent = jsonToNineCardIntent(servicesCard.intent),
    imagePath = servicesCard.imagePath,
    notification = servicesCard.notification)

  def toMoment(servicesMoment: ServicesMoment) = Moment(
    collectionId = servicesMoment.collectionId,
    timeslot = servicesMoment.timeslot map toTimeSlot,
    wifi = servicesMoment.wifi,
    headphone = servicesMoment.headphone,
    momentType = servicesMoment.momentType map (NineCardsMoment(_)))

  def toTimeSlot(servicesMomentTimeSlot:  ServicesMomentTimeSlot) = MomentTimeSlot(
    from = servicesMomentTimeSlot.from,
    to = servicesMomentTimeSlot.to,
    days = servicesMomentTimeSlot.days)

}
