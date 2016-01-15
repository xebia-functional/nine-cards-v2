package com.fortysevendeg.ninecardslauncher.process.commons

import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntentImplicits._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, NineCardIntentExtras, UnformedApp, UnformedContact}
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, EmailCardType, PhoneCardType}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
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

  def toNineCardIntent(app: Application) = {
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
      val intent = NineCardIntent(NineCardIntentExtras(tel = phone))
      intent.setAction(openPhone)
      (intent, PhoneCardType.name)
    case UnformedContact(_, _, _, Some(info)) if info.emails.nonEmpty =>
      val address = info.emails.headOption map (_.address)
      val intent = NineCardIntent(NineCardIntentExtras(email = address))
      intent.setAction(openEmail)
      (intent, EmailCardType.name)
    case _ => // TODO 9C-234 - We should create a new action for open contact and use it here
      val intent = NineCardIntent(NineCardIntentExtras())
      (intent, AppCardType.name)
  }

}
