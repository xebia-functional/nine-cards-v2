package com.fortysevendeg.ninecardslauncher.process.collection.models

import com.fortysevendeg.ninecardslauncher.process.commons.types.{CollectionType, NineCardCategory}

case class UnformedApp(
  name: String,
  packageName: String,
  className: String,
  imagePath: String,
  category: NineCardCategory)

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
   category: Option[NineCardCategory])

case class FormedItem(
  itemType: String,
  title: String,
  intent: String,
  uriImage: Option[String] = None)
