package cards.nine.services.persistence.impl

import cards.nine.repository.repositories._

trait PersistenceDependencies {
  val appRepository: AppRepository
  val cardRepository: CardRepository
  val collectionRepository: CollectionRepository
  val dockAppRepository: DockAppRepository
  val momentRepository: MomentRepository
  val userRepository: UserRepository
  val widgetRepository: WidgetRepository
}
