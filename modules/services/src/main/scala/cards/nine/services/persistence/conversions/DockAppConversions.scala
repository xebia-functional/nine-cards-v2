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

package cards.nine.services.persistence.conversions

import cards.nine.models.types.DockType
import cards.nine.models.{DockApp, DockAppData, NineCardsIntentConversions}
import cards.nine.repository.model.{
  DockApp => RepositoryDockApp,
  DockAppData => RepositoryDockAppData
}

trait DockAppConversions extends NineCardsIntentConversions {

  def toDockApp(dockApp: RepositoryDockApp): DockApp =
    DockApp(
      id = dockApp.id,
      name = dockApp.data.name,
      dockType = DockType(dockApp.data.dockType),
      intent = jsonToNineCardIntent(dockApp.data.intent),
      imagePath = dockApp.data.imagePath,
      position = dockApp.data.position)

  def toRepositoryDockApp(dockApp: DockApp): RepositoryDockApp =
    RepositoryDockApp(
      id = dockApp.id,
      data = RepositoryDockAppData(
        name = dockApp.name,
        dockType = dockApp.dockType.name,
        intent = nineCardIntentToJson(dockApp.intent),
        imagePath = dockApp.imagePath,
        position = dockApp.position))

  def toRepositoryDockApp(id: Int, dockApp: DockAppData): RepositoryDockApp =
    RepositoryDockApp(id = id, data = toRepositoryDockAppData(dockApp))

  def toRepositoryDockAppData(dockApp: DockAppData): RepositoryDockAppData =
    RepositoryDockAppData(
      name = dockApp.name,
      dockType = dockApp.dockType.name,
      intent = nineCardIntentToJson(dockApp.intent),
      imagePath = dockApp.imagePath,
      position = dockApp.position)
}
