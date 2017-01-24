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

package cards.nine.app.ui.commons.dialogs.shortcuts

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.Shortcut
import com.fortysevendeg.ninecardslauncher.R

class ShortcutDialogFragment
    extends BaseActionFragment
    with ShortcutDialogUiActions
    with ShortcutDialogDOM
    with ShortcutsUiListener
    with AppNineCardsIntentConversions { self =>

  lazy val shortcutJobs = new ShortcutDialogJobs(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    shortcutJobs.initialize().resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())
  }

  override def loadShortcuts(): Unit =
    shortcutJobs
      .loadShortcuts()
      .resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())

  def onConfigure(shortcut: Shortcut): Unit =
    shortcutJobs.configureShortcut(shortcut).resolveAsync()
}
