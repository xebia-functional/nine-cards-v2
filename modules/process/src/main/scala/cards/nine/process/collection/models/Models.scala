package cards.nine.process.collection.models

import cards.nine.models.types.{CollectionType, NineCardCategory, NineCardsMoment}
import cards.nine.process.commons.models.{FormedWidget, MomentTimeSlot}

case class UnformedContact(
  name: String,
  lookupKey: String,
  photoUri: String,
  info: Option[ContactInfo] = None)

case class ContactInfo(
  emails: Seq[ContactEmail],
  phones: Seq[ContactPhone])

case class ContactEmail(
  address: String,
  category: String)

case class ContactPhone(
  number: String,
  category: String)

case class FormedCollection(
   name: String,
   originalSharedCollectionId: Option[String],
   sharedCollectionId: Option[String],
   sharedCollectionSubscribed: Option[Boolean],
   items: Seq[FormedItem],
   collectionType: CollectionType,
   icon: String,
   category: Option[NineCardCategory],
   moment: Option[FormedMoment])

case class FormedItem(
  itemType: String,
  title: String,
  intent: String,
  uriImage: Option[String] = None)

case class FormedMoment(
  collectionId: Option[Int],
  timeslot: Seq[MomentTimeSlot],
  wifi: Seq[String],
  headphone: Boolean,
  momentType: Option[NineCardsMoment],
  widgets: Option[Seq[FormedWidget]])

case class PackagesByCategory(
  category: String,
  packages: Seq[String])
