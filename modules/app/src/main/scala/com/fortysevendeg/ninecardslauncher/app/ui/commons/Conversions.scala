package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, NineCardIntentExtras, NineCardsIntentExtras}
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollectionPackage

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

  def toNineCardIntent(app: SharedCollectionPackage): NineCardIntent = {
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

}
