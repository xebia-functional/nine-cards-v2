package cards.nine.models

import cards.nine.models.types.NineCardCategory

case class Application(
  id: Int,
  name: String,
  packageName: String,
  className: String,
  category: String,
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