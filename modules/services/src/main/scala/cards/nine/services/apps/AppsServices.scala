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

package cards.nine.services.apps

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.ApplicationData

trait AppsServices {

  /**
   * Obtains a sequence with all the installed apps
   *
   * @return the Seq[cards.nine.models.ApplicationData] with the data of the apps
   * @throws AppsInstalledException if exist some problem obtaining the installed apps
   */
  def getInstalledApplications(implicit context: ContextSupport): TaskService[Seq[ApplicationData]]

  /**
   * Obtains an installed app by the package name
   *
   * @param packageName the package name of the app to get
   * @return the [cards.nine.models.ApplicationData] with the data of the app
   * @throws AppsInstalledException if exist some problem obtaining the installed app
   */
  def getApplication(packageName: String)(
      implicit context: ContextSupport): TaskService[ApplicationData]

  /**
   * Return a sequence with the default apps for ten predefined actions
   *
   * @return the Seq[cards.nine.models.ApplicationData] with the data of the apps
   * @throws AppsInstalledException if there was an error with trying to get the default apps
   */
  def getDefaultApps(implicit context: ContextSupport): TaskService[Seq[ApplicationData]]
}
