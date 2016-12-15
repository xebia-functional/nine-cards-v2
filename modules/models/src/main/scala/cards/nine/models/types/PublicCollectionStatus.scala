package cards.nine.models.types

sealed trait PublicCollectionStatus

case object NotPublished extends PublicCollectionStatus

case object PublishedByMe extends PublicCollectionStatus

case object PublishedByOther extends PublicCollectionStatus