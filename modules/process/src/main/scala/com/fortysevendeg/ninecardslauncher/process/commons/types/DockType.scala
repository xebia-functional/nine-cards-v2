package cards.nine.process.commons.types

import cards.nine.process.commons.DockAppTypes._

sealed trait DockType {
  val name: String
}

case object AppDockType extends DockType {
  override val name: String = app
}

case object CollectionDockType extends DockType {
  override val name: String = collection
}

case object ContactDockType extends DockType {
  override val name: String = contact
}


object DockType {

  val dockTypes = Seq(AppDockType, CollectionDockType, ContactDockType)

  def apply(name: String): DockType = dockTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}



