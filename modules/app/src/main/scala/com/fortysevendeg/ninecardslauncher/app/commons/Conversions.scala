package com.fortysevendeg.ninecardslauncher.app.commons

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact, ContactEmail => DeviceContactEmail, ContactInfo => DeviceContactInfo, ContactPhone => DeviceContactPhone}
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserCollectionItem}

trait Conversions {

  def toSeqUnformedApp(apps: Seq[App]): Seq[UnformedApp] = apps map toUnformedApp

  def toUnformedApp(app: App): UnformedApp = UnformedApp(
    name = app.name,
    packageName = app.packageName,
    className = app.className,
    imagePath = app.imagePath,
    category = app.category)

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

trait NineCardIntentConversions {

  def toNineCardIntent(app: App): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(NineCardsIntentExtras.openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

  def toNineCardIntent(app: RecommendedApp): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(NineCardsIntentExtras.openNoInstalledApp)
    intent
  }

  def phoneToNineCardIntent(tel: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      tel = Option(tel)))
    intent.setAction(NineCardsIntentExtras.openPhone)
    intent
  }

  def smsToNineCardIntent(tel: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      tel = Option(tel)))
    intent.setAction(NineCardsIntentExtras.openSms)
    intent
  }

  def emailToNineCardIntent(email: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      email = Option(email)))
    intent.setAction(NineCardsIntentExtras.openEmail)
    intent
  }

  def toNineCardIntent(intent: Intent): NineCardIntent = {
    val i = new NineCardIntent(NineCardIntentExtras())
    i.fill(intent)
    i
  }

  def toNineCardIntent(packageName: String, className: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(packageName),
      class_name = Option(className)))
    intent.setAction(NineCardsIntentExtras.openApp)
    intent.setClassName(packageName, className)
    intent
  }

}