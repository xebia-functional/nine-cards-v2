package cards.nine.models

import cards.nine.models.types.{AppCardType, NineCardsCategory}

case class Application(
  id: Int,
  name: String,
  packageName: String,
  className: String,
  category: NineCardsCategory,
  dateInstalled: Long,
  dateUpdated: Long,
  version: String,
  installedFromGooglePlay: Boolean)

case class ApplicationData(
  name: String,
  packageName: String,
  className: String,
  category: NineCardsCategory,
  dateInstalled: Long,
  dateUpdated: Long,
  version: String,
  installedFromGooglePlay: Boolean)

object Application extends NineCardsIntentConversions {

  implicit class ApplicationOps(app: Application) {

    def toData = ApplicationData(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category,
      dateInstalled = app.dateInstalled,
      dateUpdated = app.dateUpdated,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)
  }

  implicit class ApplicationDataOps(app: ApplicationData) {

    def toApp(id: Int) = Application(
      id = id,
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category,
      dateInstalled = app.dateInstalled,
      dateUpdated = app.dateUpdated,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)

    def toCardData: CardData =
      CardData(
        term = app.name,
        packageName = Some(app.packageName),
        cardType = AppCardType,
        intent = toNineCardIntent(app),
        imagePath = None)
  }
}