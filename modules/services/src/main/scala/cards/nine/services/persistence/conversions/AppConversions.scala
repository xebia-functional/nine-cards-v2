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

import cards.nine.models.types.NineCardsCategory
import cards.nine.models.{Application, ApplicationData}
import cards.nine.repository.model.{App => RepositoryApp, AppData => RepositoryAppData}

trait AppConversions {

  def toApp(app: RepositoryApp): Application =
    Application(
      id = app.id,
      name = app.data.name,
      packageName = app.data.packageName,
      className = app.data.className,
      category = NineCardsCategory(app.data.category),
      dateInstalled = app.data.dateInstalled,
      dateUpdated = app.data.dateUpdate,
      version = app.data.version,
      installedFromGooglePlay = app.data.installedFromGooglePlay)

  def toRepositoryApp(app: Application): RepositoryApp =
    RepositoryApp(
      id = app.id,
      data = RepositoryAppData(
        name = app.name,
        packageName = app.packageName,
        className = app.className,
        category = app.category.name,
        dateInstalled = app.dateInstalled,
        dateUpdate = app.dateUpdated,
        version = app.version,
        installedFromGooglePlay = app.installedFromGooglePlay))

  def toRepositoryAppData(app: ApplicationData): RepositoryAppData =
    RepositoryAppData(
      name = app.name,
      packageName = app.packageName,
      className = app.className,
      category = app.category.name,
      dateInstalled = app.dateInstalled,
      dateUpdate = app.dateUpdated,
      version = app.version,
      installedFromGooglePlay = app.installedFromGooglePlay)
}
