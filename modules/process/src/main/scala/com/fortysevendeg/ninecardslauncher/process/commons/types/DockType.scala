package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.DockAppTypes._

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


object CollectionType {

  val dockTypes = Seq(AppDockType, CollectionDockType, ContactDockType)

  def apply(name: String): DockType = dockTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}



