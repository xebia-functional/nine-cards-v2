package cards.nine.models

import cards.nine.models.types.NineCardCategory

case class Application(
  id: Int,
  name: String,
  packageName: String,
  className: String,
  category: NineCardCategory,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

case class ApplicationData(
  name: String,
  packageName: String,
  className: String,
  category: NineCardCategory,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

object Application {

  implicit class ApplicationOps(app: Application) {

    def toData = ApplicationData(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdate,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)
  }
}