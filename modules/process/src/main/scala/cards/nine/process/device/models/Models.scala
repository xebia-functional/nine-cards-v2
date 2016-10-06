package cards.nine.process.device.models

import cards.nine.models.types.{DockType, EmailCategory, PhoneCategory}
import cards.nine.models.{AppWidget, Call}
import cards.nine.process.commons.models.NineCardIntent

case class LastCallsContact(
  hasContact: Boolean,
  number: String,
  title: String,
  photoUri: Option[String] = None,
  lookupKey: Option[String] = None,
  lastCallDate: Long,
  calls: Seq[Call])

case class ContactEmail(
  address: String,
  category: EmailCategory)

case class ContactPhone(
  number: String,
  category: PhoneCategory)

case class ProcessDockApp(
  name: String,
  dockType: DockType,
  intent: NineCardIntent,
  imagePath: String,
  position: Int)

case class AppsWithWidgets(
  packageName: String,
  name: String,
  widgets: Seq[AppWidget])

case class TermCounter(
  term: String,
  count: Int)

