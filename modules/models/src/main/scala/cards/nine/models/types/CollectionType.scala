package cards.nine.models.types

sealed trait CollectionType {
  val name: String
}

case object AppsCollectionType extends CollectionType {
  override val name: String = "APPS"
}

case object ContactsCollectionType extends CollectionType {
  override val name: String = "CONTACTS"
}

case object MomentCollectionType extends CollectionType {
  override val name: String = "MOMENT"
}

case object FreeCollectionType extends CollectionType {
  override val name: String = "FREE"
}

object CollectionType {

  val collectionTypes = Seq(AppsCollectionType, ContactsCollectionType, MomentCollectionType, FreeCollectionType)

  def apply(name: String): CollectionType = collectionTypes find (_.name == name) getOrElse FreeCollectionType

}



