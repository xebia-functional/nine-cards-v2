package cards.nine.services.persistence.impl

import cards.nine.repository.repositories._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope


trait RepositoryServicesScope
  extends Scope
  with Mockito {

  val mockAppRepository = mock[AppRepository]

  val mockCardRepository = mock[CardRepository]

  val mockCollectionRepository = mock[CollectionRepository]

  val mockDockAppRepository = mock[DockAppRepository]

  val mockMomentRepository = mock[MomentRepository]

  val mockUserRepository = mock[UserRepository]

  val mockWidgetRepository = mock[WidgetRepository]

  val persistenceServices = new PersistenceServicesImpl(
    appRepository = mockAppRepository,
    cardRepository = mockCardRepository,
    collectionRepository = mockCollectionRepository,
    dockAppRepository = mockDockAppRepository,
    momentRepository = mockMomentRepository,
    userRepository = mockUserRepository,
    widgetRepository = mockWidgetRepository)
}