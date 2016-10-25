package cards.nine.app.ui.launcher.types

import cards.nine.app.ui.launcher.types.AppsAlphabeticalNames._
import cards.nine.app.ui.launcher.types.ContactsMenuOptionNames._

sealed trait AppsMenuOption {
  val name: String
}

case object AppsAlphabetical extends AppsMenuOption {
  override val name: String = appsAlphabetical
}

case object AppsByCategories extends AppsMenuOption {
  override val name: String = appsByCategories
}

case object AppsByLastInstall extends AppsMenuOption {
  override val name: String = appsByLastInstall
}

object AppsMenuOption {
  val list = Seq(AppsAlphabetical, AppsByCategories, AppsByLastInstall)

  def apply(o: AppsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[AppsMenuOption] = list find (_.name == status)
}

object AppsAlphabeticalNames {
  val appsAlphabetical = "AppsAlphabetical"
  val appsByCategories = "AppsByCategories"
  val appsByLastInstall = "AppsByLastInstall"
}

sealed trait ContactsMenuOption {
  val name: String
}

case object ContactsAlphabetical extends ContactsMenuOption {
  override val name: String = contactsAlphabetical
}

case object ContactsFavorites extends ContactsMenuOption {
  override val name: String = contactsFavorites
}

case object ContactsByLastCall extends ContactsMenuOption {
  override val name: String = contactsByLastCall
}

object ContactsMenuOption {
  val list = Seq(ContactsAlphabetical, ContactsFavorites, ContactsByLastCall)

  def apply(o: ContactsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[ContactsMenuOption] = list find (_.name == status)

}

object ContactsMenuOptionNames {
  val contactsAlphabetical = "ContactsAlphabetical"
  val contactsFavorites = "ContactsFavorites"
  val contactsByLastCall = "ContactsByLastCall"
}