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

package cards.nine.app.ui.commons.dialogs.apps

import android.support.v4.app.Fragment
import cards.nine.app.ui.commons.adapters.apps.AppsSelectionAdapter
import cards.nine.models.{ApplicationData, NotCategorizedPackage}
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import macroid.Contexts

trait AppsDOM { finder: TypedFindView with Contexts[Fragment] =>

  val searchingGooglePlayKey = "searching-google-play-key"

  lazy val recycler = findView(TR.apps_actions_recycler)

  lazy val selectedAppsContent = findView(TR.selected_apps_content)

  lazy val selectedApps = findView(TR.selected_apps)

  lazy val appsMessage = findView(TR.apps_action_message)

  def getAdapter: Option[AppsSelectionAdapter] =
    Option(recycler.getAdapter) match {
      case Some(a: AppsSelectionAdapter) => Some(a)
      case _                             => None
    }

}

trait AppsUiListener {

  def loadApps(): Unit

  def loadFilteredApps(keyword: String): Unit

  def loadSearch(query: String): Unit

  def launchGooglePlay(app: NotCategorizedPackage): Unit

  def updateSelectedApps(app: ApplicationData): Unit

  def updateCollectionApps(): Unit

}
