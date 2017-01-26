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

package cards.nine.app.ui.preferences.developers

import android.graphics.drawable.BitmapDrawable
import android.preference.Preference
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.models.ApplicationData
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.util.{Success, Try}

class AppsListUiActions(dom: AppsListDOM)(implicit contextWrapper: ContextWrapper) {

  def loadApps(apps: Seq[ApplicationData]): TaskService[Unit] =
    Ui {
      val packageManager = contextWrapper.bestAvailable.getPackageManager
      apps foreach { app =>
        val preference = new Preference(contextWrapper.bestAvailable)
        preference.setTitle(app.name)
        preference.setSummary(s"${app.category.getName} (${app.packageName})")
        Try {
          packageManager.getApplicationIcon(app.packageName).asInstanceOf[BitmapDrawable]
        } match {
          case Success(drawable) => preference.setIcon(drawable)
          case _                 => preference.setIcon(R.drawable.ic_launcher)
        }
        dom.appsListPreferenceCategory.addPreference(preference)
      }
    }.toService()

}
