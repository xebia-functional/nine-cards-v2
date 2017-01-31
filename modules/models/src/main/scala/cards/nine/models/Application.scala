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

import cards.nine.models.types.{AppCardType, NineCardsCategory}

case class Application(
    id: Int,
    name: String,
    packageName: String,
    className: String,
    category: NineCardsCategory,
    dateInstalled: Long,
    dateUpdated: Long,
    version: String,
    installedFromGooglePlay: Boolean)

case class ApplicationData(
    name: String,
    packageName: String,
    className: String,
    category: NineCardsCategory,
    dateInstalled: Long,
    dateUpdated: Long,
    version: String,
    installedFromGooglePlay: Boolean)

object Application extends NineCardsIntentConversions {

  implicit class ApplicationOps(app: Application) {

    def toData =
      ApplicationData(
        name = app.name,
        packageName = app.packageName,
        className = app.className,
        category = app.category,
        dateInstalled = app.dateInstalled,
        dateUpdated = app.dateUpdated,
        version = app.version,
        installedFromGooglePlay = app.installedFromGooglePlay)
  }

  implicit class ApplicationDataOps(app: ApplicationData) {

    def toApp(id: Int) =
      Application(
        id = id,
        name = app.name,
        packageName = app.packageName,
        className = app.className,
        category = app.category,
        dateInstalled = app.dateInstalled,
        dateUpdated = app.dateUpdated,
        version = app.version,
        installedFromGooglePlay = app.installedFromGooglePlay)

    def toCardData: CardData =
      CardData(
        term = app.name,
        packageName = Some(app.packageName),
        cardType = AppCardType,
        intent = toNineCardIntent(app),
        imagePath = None)
  }
}
