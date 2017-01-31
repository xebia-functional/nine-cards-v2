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

package cards.nine.app.ui.commons.dialogs.widgets

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.dialogs.widgets.WidgetsFragment._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService._
import cards.nine.models.AppWidget
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import cats.implicits._

class WidgetsFragment(implicit widgetsJobs: WidgetsJobs)
    extends BaseActionFragment
    with WidgetsDialogUiActions
    with WidgetsDialogDOM
    with WidgetsDialogListener
    with AppNineCardsIntentConversions {

  lazy val widgetContentWidth =
    getString(Seq(getArguments), WidgetsFragment.widgetContentWidth, "0").toInt

  lazy val widgetContentHeight =
    getString(Seq(getArguments), WidgetsFragment.widgetContentHeight, "0").toInt

  override def getLayoutId: Int = R.layout.widgets_action_fragment

  override protected lazy val backgroundColor: Int = resGetColor(R.color.widgets_background)

  lazy val widgetsDialogJobs = new WidgetsDialogJobs(this)

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    statuses = statuses
      .copy(widgetContentWidth = widgetContentWidth, widgetContentHeight = widgetContentHeight)
    widgetsDialogJobs.initialize().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())
  }

  override def loadWidgets(): Unit =
    widgetsDialogJobs.loadWidgets().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())

  override def hostWidget(widget: AppWidget): Unit =
    (widgetsJobs.hostWidget(widget) *> widgetsDialogJobs.close()).resolveAsync()
}

object WidgetsFragment {

  var statuses = WidgetStatuses()

  val widgetContentWidth = "widget-content-width"

  val widgetContentHeight = "widget-content-height"

}

case class WidgetStatuses(widgetContentWidth: Int = 0, widgetContentHeight: Int = 0)
