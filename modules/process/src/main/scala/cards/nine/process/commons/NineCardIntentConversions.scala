package cards.nine.process.commons

import cards.nine.models.{Application, ApplicationData}
import cards.nine.process.commons.models.NineCardIntentExtras._
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models.{NineCardIntent, NineCardIntentExtras}
import play.api.libs.json.Json

trait NineCardIntentConversions {

  def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardIntent]

  def nineCardIntentToJson(intent: NineCardIntent) = Json.toJson(intent).toString()

  def toNineCardIntent(item: ApplicationData) = {
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

  def packageToNineCardIntent(packageName: String): NineCardIntent = {
    val intent = NineCardIntent(NineCardIntentExtras(
      package_name = Option(packageName)))
    intent.setAction(openNoInstalledApp)
    intent
  }

}
