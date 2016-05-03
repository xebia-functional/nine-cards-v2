package com.fortysevendeg.ninecardslauncher.process.commons.types

import com.fortysevendeg.ninecardslauncher.process.commons.CollectionTypes._

sealed trait CollectionType {
  val name: String
}

case object AppsCollectionType extends CollectionType {
  override val name: String = apps
}

case object ContactsCollectionType extends CollectionType {
  override val name: String = contacts
}

case object MomentCollectionType extends CollectionType {
  override val name: String = moment
}

case object FreeCollectionType extends CollectionType {
  override val name: String = free
}

object CollectionType {

  val collectionTypes = Seq(AppsCollectionType, ContactsCollectionType, MomentCollectionType, FreeCollectionType)

  def apply(name: String): CollectionType = collectionTypes find (_.name == name) getOrElse FreeCollectionType

}



