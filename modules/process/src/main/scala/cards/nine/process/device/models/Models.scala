package cards.nine.process.device.models

import cards.nine.models.types.{DockType, EmailCategory, PhoneCategory}
import cards.nine.models.{NineCardsIntent, AppWidget, Call}


case class AppsWithWidgets(
  packageName: String,
  name: String,
  widgets: Seq[AppWidget])
