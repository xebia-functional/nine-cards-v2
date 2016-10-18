package cards.nine.models

import cards.nine.models.NineCardsIntentExtras._
import cards.nine.models.NineCardsIntentImplicits._
import play.api.libs.json.Json

trait NineCardsIntentConversions {

  def jsonToNineCardIntent(json: String) = Json.parse(json).as[NineCardsIntent]

  def nineCardIntentToJson(intent: NineCardsIntent) = Json.toJson(intent).toString()

  def toNineCardIntent(item: ApplicationData) = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(item.packageName),
      class_name = Option(item.className)))
    intent.setAction(openApp)
    intent.setClassName(item.packageName, item.className)
    intent
  }

  def toNineCardIntent(app: Application) = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(app.packageName),
      class_name = Option(app.className)))
    intent.setAction(openApp)
    intent.setClassName(app.packageName, app.className)
    intent
  }

  def packageToNineCardIntent(packageName: String): NineCardsIntent = {
    val intent = NineCardsIntent(NineCardsIntentExtras(
      package_name = Option(packageName)))
    intent.setAction(openNoInstalledApp)
    intent
  }

}
