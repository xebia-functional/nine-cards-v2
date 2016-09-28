package cards.nine.process.commons

import cards.nine.process.collection.models.{UnformedApp, UnformedContact}
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models.NineCardIntentExtras._
import cards.nine.process.commons.models.{NineCardIntent, NineCardIntentExtras}
import cards.nine.process.commons.types.{ContactCardType, EmailCardType, PhoneCardType}
import cards.nine.process.moment.models.{App => MomentApp}
import cards.nine.services.apps.models.Application
import cards.nine.services.persistence.models.App
import play.api.libs.json.Json

trait NineCardIntentConversions {

  def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardIntent]

  def nineCardIntentToJson(intent: NineCardIntent) = Json.toJson(intent).toString()

  def toNineCardIntent(item: UnformedApp) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(item.packageName),
      class_name = Option(item.className)))
    intent.setAction(openApp)
    intent.setClassName(item.packageName, item.className)
    intent
  }

  def toNineCardIntent(item: MomentApp) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(item.packageName),
      class_name = Option(item.className)))
    intent.setAction(openApp)
    intent.setClassName(item.packageName, item.className)
    intent
  }

  def toNineCardIntent(app: Application) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

  def toNineCardIntent(app: App) = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

  def toNineCardIntent(item: UnformedContact): (NineCardIntent, String) = item match {
    case UnformedContact(_, _, _, Some(info)) if info.phones.nonEmpty =>
      val phone = info.phones.headOption map (_.number)
      val intent = NineCardIntent(NineCardIntentExtras(tel = phone, contact_lookup_key = Some(item.lookupKey)))
      intent.setAction(openPhone)
      (intent, PhoneCardType.name)
    case UnformedContact(_, _, _, Some(info)) if info.emails.nonEmpty =>
      val address = info.emails.headOption map (_.address)
      val intent = NineCardIntent(NineCardIntentExtras(email = address, contact_lookup_key = Some(item.lookupKey)))
      intent.setAction(openEmail)
      (intent, EmailCardType.name)
    case _ =>
      val intent = NineCardIntent(NineCardIntentExtras(contact_lookup_key = Some(item.lookupKey)))
      intent.setAction(openContact)
      (intent, ContactCardType.name)
  }

  def packageToNineCardIntent(packageName: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(packageName)))
    intent.setAction(openNoInstalledApp)
    intent
  }

}
