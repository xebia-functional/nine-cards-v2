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

package cards.nine.models

import cards.nine.models.types.DockType

case class DockApp(
    id: Int,
    name: String,
    dockType: DockType,
    intent: NineCardsIntent,
    imagePath: String,
    position: Int)

case class DockAppData(
    name: String,
    dockType: DockType,
    intent: NineCardsIntent,
    imagePath: String,
    position: Int)

object DockApp {

  implicit class DockAppOps(dockApp: DockApp) {

    def toData =
      DockAppData(
        name = dockApp.name,
        dockType = dockApp.dockType,
        intent = dockApp.intent,
        imagePath = dockApp.imagePath,
        position = dockApp.position)

  }
}
