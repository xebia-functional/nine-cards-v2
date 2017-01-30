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
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.commons.services.TaskService._
import macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.SafeUi._
import com.apptentive.android.sdk.Apptentive
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ActivityContextWrapper, Ui}

class PreferencesUiActions(dom: PreferencesDOM)(implicit contextWrapper: ActivityContextWrapper) {

  def initialize(): TaskService[Unit] =
    Ui {
      dom.actionBar foreach { ab =>
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowHomeEnabled(false)
        ab.setDisplayShowTitleEnabled(true)
        ab.setDisplayUseLogoEnabled(false)
      }
    }.toService()

  def setActionBarTitle(): TaskService[Unit] =
    Ui(dom.actionBar foreach (_.setTitle(R.string.nineCardsSettingsTitle))).toService()

  def setActivityResult(resultCode: Int, data: Intent): TaskService[Unit] =
    Ui(contextWrapper.original.get foreach (_.setResult(resultCode, data))).toService()

  def showContactUsError(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService()

  def showWizardInlineCleaned(): TaskService[Unit] =
    uiShortToast(R.string.wizardInlineCleaned).toService()

  def goToHelp(): TaskService[Unit] =
    uiOpenUrlIntent(resGetString(R.string.ninecards_help)).toService()

  def goToFeedback(): TaskService[Unit] =
    Ui(Apptentive.showMessageCenter(contextWrapper.bestAvailable)).toService()

}
