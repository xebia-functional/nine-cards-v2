package cards.nine.models.types

sealed trait DockType {
  val name: String
}

case object AppDockType extends DockType {
  override val name: String = "APP"
}

case object CollectionDockType extends DockType {
  override val name: String = "COLLECTION"
}

case object ContactDockType extends DockType {
  override val name: String = "CONTACT"
}


object DockType {

  val dockTypes = Seq(AppDockType, CollectionDockType, ContactDockType)

  def apply(name: String): DockType = dockTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}



