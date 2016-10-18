package cards.nine.process.device.models

import cards.nine.models.types.{DockType, EmailCategory, PhoneCategory}
import cards.nine.models.{NineCardsIntent, AppWidget, Call}

case class LastCallsContact(
  hasContact: Boolean,
  number: String,
  title: String,
  photoUri: Option[String] = None,
  lookupKey: Option[String] = None,
  lastCallDate: Long,
  calls: Seq[Call])

case class AppsWithWidgets(
  packageName: String,
  name: String,
  widgets: Seq[AppWidget])
