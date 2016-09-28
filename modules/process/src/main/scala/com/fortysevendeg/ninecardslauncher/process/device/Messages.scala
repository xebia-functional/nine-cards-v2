package cards.nine.process.device

import cards.nine.process.commons.types.DockType

sealed trait ContactsFilter

case object AllContacts extends ContactsFilter

case object FavoriteContacts extends ContactsFilter

case object ContactsWithPhoneNumber extends ContactsFilter


sealed trait GetAppOrder {
  val ascending: Boolean
}

case class GetByName(ascending: Boolean) extends GetAppOrder

object GetByName extends GetByName(true)

case class GetByInstallDate(ascending: Boolean) extends GetAppOrder

object GetByInstallDate extends GetByInstallDate(false)

case class GetByCategory(ascending: Boolean) extends GetAppOrder

object GetByCategory extends GetByCategory(true)

case class IconResize(width: Int, height: Int)

case class SaveDockAppRequest(
  name: String,
  dockType: DockType,
  intent: String,
  imagePath: String,
  position: Int)