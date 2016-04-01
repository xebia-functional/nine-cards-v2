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

case object HomeMorningCollectionType extends CollectionType {
  override val name: String = homeMorning
}

case object HomeNightCollectionType extends CollectionType {
  override val name: String = homeNight
}

case object WorkCollectionType extends CollectionType {
  override val name: String = work
}

case object FreeCollectionType extends CollectionType {
  override val name: String = free
}

object CollectionType {

  val momentsCollectionTypes = Seq(HomeMorningCollectionType, WorkCollectionType, HomeNightCollectionType)

  val generalCollectionTypes = Seq(AppsCollectionType, ContactsCollectionType, FreeCollectionType)

  val collectionTypes = generalCollectionTypes ++ momentsCollectionTypes

  def apply(name: String): CollectionType = collectionTypes find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"The key '$name' is not a valid CollectionType"))

}



