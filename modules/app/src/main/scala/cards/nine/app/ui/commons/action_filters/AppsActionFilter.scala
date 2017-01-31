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

package cards.nine.app.ui.commons.action_filters

sealed trait AppsActionFilter {
  val action: String
}

case object AppInstalledActionFilter extends AppsActionFilter {
  override val action: String = "app-installed-action-filter"
}

case object AppUninstalledActionFilter extends AppsActionFilter {
  override val action: String = "app-uninstalled-action-filter"
}

case object AppUpdatedActionFilter extends AppsActionFilter {
  override val action: String = "app-updated-action-filter"
}

object AppsActionFilter {

  val cases = Seq(AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter)

  def apply(action: String): Option[AppsActionFilter] =
    cases find (_.action == action)

}
