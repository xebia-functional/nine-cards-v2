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

package cards.nine.app.ui.collections.jobs

import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.jobs.uiactions.ToolbarUiActions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import macroid.ActivityContextWrapper

class ToolbarJobs(actions: ToolbarUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions
    with AppNineCardsIntentConversions { self =>

  def pullToClose(scroll: Int, close: Boolean): TaskService[Unit] =
    actions.pullCloseScrollY(scroll, close)

}
