package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.device.models.{AppCategorized, Contact, ContactInfo => DeviceContactInfo,
  ContactEmail => DeviceContactEmail, ContactPhone => DeviceContactPhone}
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollectionItem, UserCollection}

trait Conversions {

  def toSeqUnformedApp(apps: Seq[AppCategorized]): Seq[UnformedApp] = apps map toUnformedApp

  def toUnformedApp(appCategorized: AppCategorized): UnformedApp = UnformedApp(
    name = appCategorized.name,
    packageName = appCategorized.packageName,
    className = appCategorized.className,
    imagePath = appCategorized.imagePath getOrElse "", // TODO image default?
    category = appCategorized.category getOrElse misc,
    starRating = appCategorized.starRating getOrElse 0,
    numDownloads = appCategorized.numDownloads getOrElse "",
    ratingsCount = appCategorized.ratingsCount getOrElse 0,
    commentCount = appCategorized.commentCount getOrElse 0)

  def toSeqUnformedContact(contacts: Seq[Contact]): Seq[UnformedContact] = contacts map toUnformedContact

  def toUnformedContact(contact: Contact): UnformedContact = UnformedContact(
    name = contact.name,
    lookupKey = contact.lookupKey,
    photoUri = contact.photoUri,
    info = contact.info map toContactInfo)

  def toContactInfo(item: DeviceContactInfo): ContactInfo = ContactInfo(
    emails = item.emails map toContactEmail,
    phones = item.phones map toContactPhone)

  def toContactEmail(item: DeviceContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category.toString)

  def toContactPhone(item: DeviceContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category.toString)

  def toSeqFormedCollection(collections: Seq[UserCollection]): Seq[FormedCollection] = collections map toFormedCollection

  def toFormedCollection(userCollection: UserCollection): FormedCollection = FormedCollection(
    name = userCollection.name,
    originalSharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
    items = userCollection.items map toFormedItem,
    collectionType = userCollection.collectionType,
    constrains = userCollection.constrains,
    icon = userCollection.icon,
    category = userCollection.category)

  def toFormedItem(item: UserCollectionItem): FormedItem = FormedItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.intent)

}
