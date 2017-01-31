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

package cards.nine.app.ui.preferences

import android.content.Intent
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.dialogs.wizard.WizardInlinePreferences
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ActivityContextWrapper

class PreferencesJobs(ui: PreferencesUiActions)(implicit contextWrapper: ActivityContextWrapper)
    extends Jobs
    with ImplicitsUiExceptions {

  var statuses = PreferencesJobsStatuses()

  lazy val wizardInlinePreferences = new WizardInlinePreferences()

  def initialize(): TaskService[Unit] = ui.initialize()

  def initializeActionBarTitle(): TaskService[Unit] = ui.setActionBarTitle()

  def preferenceChanged(preferenceName: String): TaskService[Unit] = {
    statuses = statuses.copy(changedPreferences = statuses.changedPreferences + preferenceName)
    val data = new Intent()
    data.putExtra(ResultData.preferencesResultData, statuses.changedPreferences.toArray)
    ui.setActivityResult(ResultCodes.preferencesChanged, data)
  }

  def cleanWizardInlinePreferences(): TaskService[Unit] =
    for {
      _ <- TaskService.right(wizardInlinePreferences.clean())
      _ <- ui.showWizardInlineCleaned()
    } yield ()

}

case class PreferencesJobsStatuses(changedPreferences: Set[String] = Set.empty)
