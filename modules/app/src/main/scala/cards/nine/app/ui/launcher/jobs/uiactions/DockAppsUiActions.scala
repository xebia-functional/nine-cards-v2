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

package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DockAppsPanelLayoutTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.DockAppData
import cards.nine.models.NineCardsTheme
import macroid.{ActivityContextWrapper, FragmentManagerContext}

class DockAppsUiActions(val dom: LauncherDOM)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit def theme: NineCardsTheme = statuses.theme

  def loadDockApps(apps: Seq[DockAppData]): TaskService[Unit] =
    (dom.dockAppsPanel <~ daplInit(apps)).toService(Option("loadDockApps"))

  def reloadDockApps(dockApp: DockAppData): TaskService[Unit] =
    (dom.dockAppsPanel <~ daplReload(dockApp)).toService()

  def reset(): TaskService[Unit] =
    (dom.dockAppsPanel <~ daplReset()).toService()

}
