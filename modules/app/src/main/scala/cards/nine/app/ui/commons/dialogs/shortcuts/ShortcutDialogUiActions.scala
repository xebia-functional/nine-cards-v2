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

import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Shortcut
import cards.nine.models.types.theme.DrawerBackgroundColor
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewTweaks._

import scala.math.Ordering.Implicits._

trait ShortcutDialogUiActions extends Styles {

  self: BaseActionFragment with ShortcutDialogDOM with ShortcutsUiListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.shortcuts) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~
        recyclerStyle <~
        vBackgroundColor(theme.get(DrawerBackgroundColor)))).toService()

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone)).toService()

  def close(): TaskService[Unit] = unreveal().toService()

  def configureShortcut(shortcut: Shortcut): TaskService[Unit] =
    goToConfigureShortcut(shortcut).toService()

  def showErrorLoadingShortcutsInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingShortcuts, error = true, loadShortcuts()).toService()

  def loadShortcuts(shortcuts: Seq[Shortcut]): TaskService[Unit] = {
    val sortedShortcuts = shortcuts sortBy sortByTitle
    val adapter         = ShortcutDialogAdapter(sortedShortcuts, onConfigure)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService()
  }

  private[this] def sortByTitle(shortcut: Shortcut) =
    shortcut.title map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

}
