package cards.nine.models

import cards.nine.models.types.NineCardCategory

case class ApplicationData(
  name: String,
  packageName: String,
  className: String,
  category: NineCardCategory,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)