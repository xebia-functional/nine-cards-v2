package cards.nine.app.commons

import android.content.Intent
import cards.nine.app.ui.commons.Constants._
import cards.nine.models.ApplicationData
import cards.nine.models.types.{AppCardType, AppsCollectionType, ContactCardType, NoInstalledAppCardType}
import cards.nine.process.cloud.models._
import cards.nine.process.collection.models._
import cards.nine.process.collection.{AddCardRequest, AddCollectionRequest}
import cards.nine.process.commons.NineCardIntentConversions
import cards.nine.process.commons.models._
import cards.nine.process.device.SaveDockAppRequest
import cards.nine.process.device.models.{Contact, ContactEmail => ProcessContactEmail, ContactInfo => ProcessContactInfo, ContactPhone => ProcessContactPhone}
import cards.nine.process.moment.SaveMomentRequest
import cards.nine.process.recommendations.models.RecommendedApp
import cards.nine.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}

import scala.util.Random

trait Conversions
  extends AppNineCardIntentConversions {

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
    originalSharedCollectionId = userCollection.originalSharedCollectionId,
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
      appsCategory = privateCollection.appsCategory,
      cards = privateCollection.cards map toAddCardRequest,
      moment = privateCollection.moment)

  def toAddCardRequest(privateCard: PrivateCard): AddCardRequest =
    AddCardRequest(
      term = privateCard.term,
      packageName = privateCard.packageName,
      cardType = privateCard.cardType,
      intent = privateCard.intent,
      imagePath = privateCard.imagePath)

  def toAddCardRequest(card: Card): AddCardRequest =
    AddCardRequest(
      term = card.term,
      packageName = card.packageName,
      cardType = card.cardType,
      intent = card.intent,
      imagePath = card.imagePath)

  def toAddCardRequest(contact: Contact): AddCardRequest =
    AddCardRequest(
      term = contact.name,
      packageName = None,
      cardType = ContactCardType,
      intent = contactToNineCardIntent(contact.lookupKey),
      imagePath = Option(contact.photoUri))

  def toAddCollectionRequestFromSharedCollection(collection: SharedCollection, cards: Seq[AddCardRequest]): AddCollectionRequest =
    AddCollectionRequest(
      name = collection.name,
      collectionType = AppsCollectionType,
      icon = collection.icon,
      themedColorIndex = Random.nextInt(numSpaces),
      appsCategory = Option(collection.category),
      cards = cards,
      moment = None,
      sharedCollectionId = Option(collection.sharedCollectionId),
      originalSharedCollectionId = Option(collection.sharedCollectionId))

  def toAddCardRequest(app: SharedCollectionPackage): AddCardRequest =
    AddCardRequest(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = NoInstalledAppCardType,
      intent = toNineCardIntent(app),
      imagePath = None)

  def toAddCardRequest(app: ApplicationData): AddCardRequest =
    AddCardRequest(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = None)

  def toAddCardRequest(app: RecommendedApp): AddCardRequest =
    AddCardRequest(
      term = app.title,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = None)

  def toSaveDockAppRequest(cloudStorageDockApp: CloudStorageDockApp): SaveDockAppRequest =
    SaveDockAppRequest(
      name = cloudStorageDockApp.name,
      dockType = cloudStorageDockApp.dockType,
      intent = cloudStorageDockApp.intent,
      imagePath = cloudStorageDockApp.imagePath,
      position = cloudStorageDockApp.position)

}

trait AppNineCardIntentConversions extends NineCardIntentConversions {

  def toNineCardIntent(app: SharedCollectionPackage): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(NineCardIntentExtras.openNoInstalledApp)
    intent
  }

  def toNineCardIntent(app: RecommendedApp): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName)))
    intent.setAction(NineCardIntentExtras.openNoInstalledApp)
    intent
  }

  def phoneToNineCardIntent(lookupKey: Option[String], tel: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      contact_lookup_key = lookupKey,
      tel = Option(tel)))
    intent.setAction(NineCardIntentExtras.openPhone)
    intent
  }

  def smsToNineCardIntent(lookupKey: Option[String], tel: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      contact_lookup_key = lookupKey,
      tel = Option(tel)))
    intent.setAction(NineCardIntentExtras.openSms)
    intent
  }

  def emailToNineCardIntent(lookupKey: Option[String], email: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      contact_lookup_key = lookupKey,
      email = Option(email)))
    intent.setAction(NineCardIntentExtras.openEmail)
    intent
  }

  def contactToNineCardIntent(lookupKey: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      contact_lookup_key = Option(lookupKey)))
    intent.setAction(NineCardIntentExtras.openContact)
    intent
  }

  def toNineCardIntent(intent: Intent): NineCardIntent = {
    val i = NineCardIntent(NineCardIntentExtras())
    i.fill(intent)
    i
  }

  def toNineCardIntent(packageName: String, className: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(packageName),
      class_name = Option(className)))
    intent.setAction(NineCardIntentExtras.openApp)
    intent.setClassName(packageName, className)
    intent
  }

  def toMoment(cloudStorageMoment: CloudStorageMoment): FormedMoment =
    FormedMoment(
      collectionId = None,
      timeslot = cloudStorageMoment.timeslot map toTimeSlot,
      wifi = cloudStorageMoment.wifi,
      headphone = cloudStorageMoment.headphones,
      momentType = cloudStorageMoment.momentType,
      widgets = cloudStorageMoment.widgets map toFormedWidgetSeq)

  def toSaveMomentRequest(cloudStorageMoment: CloudStorageMoment): SaveMomentRequest =
    SaveMomentRequest(
      collectionId = None,
      timeslot = cloudStorageMoment.timeslot map toTimeSlot,
      wifi = cloudStorageMoment.wifi,
      headphone = cloudStorageMoment.headphones,
      momentType = cloudStorageMoment.momentType,
      widgets = cloudStorageMoment.widgets map toFormedWidgetSeq)

  def toFormedWidgetSeq(widgets: Seq[CloudStorageWidget]) =
    widgets map toFormedWidget

  def toFormedWidget(widget: CloudStorageWidget): FormedWidget =
    FormedWidget(
      packageName = widget.packageName,
      className = widget.className,
      startX = widget.area.startX,
      startY = widget.area.startY,
      spanX = widget.area.spanX,
      spanY = widget.area.spanY,
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

  def toTimeSlot(cloudStorageMomentTimeSlot: CloudStorageMomentTimeSlot): MomentTimeSlot =
    MomentTimeSlot(
      from = cloudStorageMomentTimeSlot.from,
      to = cloudStorageMomentTimeSlot.to,
      days = cloudStorageMomentTimeSlot.days)

}