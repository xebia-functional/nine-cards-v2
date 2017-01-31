/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.persistence.impl

import cards.nine.repository.repositories._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait RepositoryServicesScope extends Scope with Mockito {

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
