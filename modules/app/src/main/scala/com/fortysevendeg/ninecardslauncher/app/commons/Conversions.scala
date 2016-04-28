package com.fortysevendeg.ninecardslauncher.app.commons

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageCollection, CloudStorageCollectionItem, CloudStorageMoment, CloudStorageMomentTimeSlot}
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, AddCollectionRequest}
import com.fortysevendeg.ninecardslauncher.process.commons.models
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Moment, MomentTimeSlot, PrivateCard, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, AppsCollectionType, ContactCardType, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact, ContactEmail => ProcessContactEmail, ContactInfo => ProcessContactInfo, ContactPhone => ProcessContactPhone}
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}

import scala.util.Random

trait Conversions
  extends NineCardIntentConversions {

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

  def toContactInfo(item: ProcessContactInfo): ContactInfo = ContactInfo(
    emails = item.emails map toContactEmail,
    phones = item.phones map toContactPhone)

  def toContactEmail(item: ProcessContactEmail): ContactEmail = ContactEmail(
    address = item.address,
    category = item.category.toString)

  def toContactPhone(item: ProcessContactPhone): ContactPhone = ContactPhone(
    number = item.number,
    category = item.category.toString)

  def toSeqFormedCollection(collections: Seq[CloudStorageCollection]): Seq[FormedCollection] = collections map toFormedCollection

  def toFormedCollection(userCollection: CloudStorageCollection): FormedCollection = FormedCollection(
    name = userCollection.name,
    originalSharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionId = userCollection.sharedCollectionId,
    sharedCollectionSubscribed = userCollection.sharedCollectionSubscribed,
    items = userCollection.items map toFormedItem,
    collectionType = userCollection.collectionType,
    icon = userCollection.icon,
    category = userCollection.category,
    moment = userCollection.moment map toMoment)

  def toFormedItem(item: CloudStorageCollectionItem): FormedItem = FormedItem(
    itemType = item.itemType,
    title = item.title,
    intent = item.intent)

  def toAddCollectionRequest(privateCollection: PrivateCollection): AddCollectionRequest =
    AddCollectionRequest(
      name = privateCollection.name,
      collectionType = privateCollection.collectionType,
      icon = privateCollection.icon,
      themedColorIndex = privateCollection.themedColorIndex,
      appsCategory = privateCollection.appsCategory)

  def toAddCardRequest(privateCard: PrivateCard): AddCardRequest =
    AddCardRequest(
      term = privateCard.term,
      packageName = privateCard.packageName,
      cardType = privateCard.cardType,
      intent = privateCard.intent,
      imagePath = privateCard.imagePath)

  def toAddCardRequest(contact: Contact): AddCardRequest =
    AddCardRequest(
      term = contact.name,
      packageName = None,
      cardType = ContactCardType,
      intent = contactToNineCardIntent(contact.lookupKey),
      imagePath = contact.photoUri)

  def toAddCollectionRequest(collection: SharedCollection): AddCollectionRequest =
    AddCollectionRequest(
      name = collection.name,
      collectionType = AppsCollectionType,
      icon = collection.icon,
      themedColorIndex = Random.nextInt(numSpaces),
      appsCategory = Option(collection.category),
      originalSharedCollectionId = Option(collection.sharedCollectionId))

  def toAddCardRequest(app: SharedCollectionPackage): AddCardRequest =
    AddCardRequest(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = NoInstalledAppCardType,
      intent = toNineCardIntent(app),
      imagePath = "")

  def toAddCardRequest(app: App): AddCardRequest =
    AddCardRequest(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = app.imagePath)

}

trait NineCardIntentConversions {

  def toNineCardIntent(app: App): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(models.NineCardsIntentExtras.openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

  def toNineCardIntent(app: SharedCollectionPackage): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(models.NineCardsIntentExtras.openNoInstalledApp)
    intent
  }

  def toNineCardIntent(app: RecommendedApp): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(models.NineCardsIntentExtras.openNoInstalledApp)
    intent
  }

  def phoneToNineCardIntent(tel: String): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      tel = Option(tel)))
    intent.setAction(models.NineCardsIntentExtras.openPhone)
    intent
  }

  def smsToNineCardIntent(tel: String): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      tel = Option(tel)))
    intent.setAction(models.NineCardsIntentExtras.openSms)
    intent
  }

  def emailToNineCardIntent(email: String): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      email = Option(email)))
    intent.setAction(models.NineCardsIntentExtras.openEmail)
    intent
  }

  def contactToNineCardIntent(lookupKey: String): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      contact_lookup_key = Option(lookupKey)))
    intent.setAction(models.NineCardsIntentExtras.openContact)
    intent
  }

  def toNineCardIntent(intent: Intent): models.NineCardIntent = {
    val i = new models.NineCardIntent(models.NineCardIntentExtras())
    i.fill(intent)
    i
  }

  def toNineCardIntent(packageName: String, className: String): models.NineCardIntent = {
    val intent = models.NineCardIntent(models.NineCardIntentExtras(
      package_name = Option(packageName),
      class_name = Option(className)))
    intent.setAction(models.NineCardsIntentExtras.openApp)
    intent.setClassName(packageName, className)
    intent
  }

  def toMoment(cloudStorageMoment: CloudStorageMoment) =
    Moment(
      collectionId = None,
      timeslot = cloudStorageMoment.timeslot map toTimeSlot,
      wifi = cloudStorageMoment.wifi,
      headphone = cloudStorageMoment.headphones,
      momentType = cloudStorageMoment.momentType)

  def toTimeSlot(cloudStorageMomentTimeSlot: CloudStorageMomentTimeSlot) =
    MomentTimeSlot(
      from = cloudStorageMomentTimeSlot.from,
      to = cloudStorageMomentTimeSlot.to,
      days = cloudStorageMomentTimeSlot.days)

}