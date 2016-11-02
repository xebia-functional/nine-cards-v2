package cards.nine.app.ui.launcher.types

sealed trait AppsMenuOption {
  val name: String
}

case object AppsAlphabetical extends AppsMenuOption {
  override val name: String = "AppsAlphabetical"
}

case object AppsByCategories extends AppsMenuOption {
  override val name: String = "AppsByCategories"
}

case object AppsByLastInstall extends AppsMenuOption {
  override val name: String = "AppsByLastInstall"
}

object AppsMenuOption {
  val list = Seq(AppsAlphabetical, AppsByCategories, AppsByLastInstall)

  def apply(o: AppsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[AppsMenuOption] = list find (_.name == status)
}

sealed trait ContactsMenuOption {
  val name: String
}

case object ContactsAlphabetical extends ContactsMenuOption {
  override val name: String = "ContactsAlphabetical"
}

case object ContactsFavorites extends ContactsMenuOption {
  override val name: String = "ContactsFavorites"
}

case object ContactsByLastCall extends ContactsMenuOption {
  override val name: String = "ContactsByLastCall"
}

object ContactsMenuOption {
  val list = Seq(ContactsAlphabetical, ContactsFavorites, ContactsByLastCall)

  def apply(o: ContactsMenuOption): Int = list.indexOf(o)

  def apply(status: String): Option[ContactsMenuOption] = list find (_.name == status)

}
