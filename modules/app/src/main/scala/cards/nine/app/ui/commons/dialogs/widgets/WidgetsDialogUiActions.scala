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

import android.view.{Gravity, ViewGroup}
import android.widget.{ImageView, LinearLayout, TextView}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.commons.dialogs.widgets.WidgetsFragment._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.{AppWidget, AppsWithWidgets}
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.LinearLayoutTweaks._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

trait WidgetsDialogUiActions extends Styles {

  self: BaseActionFragment with WidgetsDialogDOM with WidgetsDialogListener =>

  val unselectedAlpha = .3f

  val selectedAlpha = 1

  lazy val padding = resGetDimensionPixelSize(R.dimen.padding_default)

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.widgetsTitle) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)).toService()

  def loadWidgets(appsWithWidgets: Seq[AppsWithWidgets]): TaskService[Unit] = {
    val (tag, widgets) = appsWithWidgets.headOption map (app =>
                                                           (app.packageName, app.widgets)) getOrElse ("", Seq.empty)
    val adapter = WidgetsAdapter(
      Seq.empty,
      statuses.widgetContentWidth,
      statuses.widgetContentHeight,
      hostWidget)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      loadMenuApps(appsWithWidgets) ~
      showWidgets(tag, widgets)).toService()
  }

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone)).toService()

  def showErrorLoadingWidgetsInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.widgetsErrorMessage, error = true, action = loadWidgets())
      .toService()

  def close(): TaskService[Unit] = unreveal().toService()

  private[this] def loadMenuApps(appsWithWidgets: Seq[AppsWithWidgets]): Ui[Any] = {

    val views = appsWithWidgets map { app =>
      (l[LinearLayout](
        w[ImageView] <~ iconMenuItemStyle(app.packageName, app.name) <~ vTag(app.packageName),
        w[TextView] <~ textMenuItemStyle(app.name) <~ vTag(app.packageName)) <~
        contentMenuItemStyle <~
        On.click(showWidgets(app.packageName, app.widgets))).get
    }
    menu <~ vgAddViews(views)
  }

  private[this] def showWidgets(tag: String, widgets: Seq[AppWidget]) =
    recycler.getAdapter match {
      case adapter: WidgetsAdapter =>
        (recycler <~ rvSwapAdapter(adapter.copy(widgets))) ~
          (menu <~ Transformer {
            case content: ImageView if content.getTag == tag =>
              content <~ vAlpha(selectedAlpha)
            case content: ImageView =>
              content <~ vAlpha(unselectedAlpha)
            case content: TextView if content.getTag == tag =>
              content <~ vAlpha(selectedAlpha)
            case content: TextView =>
              content <~ vAlpha(unselectedAlpha)
          })
      case _ => Ui.nop
    }

  // Styles

  private[this] def contentMenuItemStyle: Tweak[LinearLayout] =
    vWrapContent +
      llHorizontal +
      vPaddings(padding) +
      llGravity(Gravity.CENTER_VERTICAL) +
      vBackgroundColorResource(R.color.widgets_background_content)

  private[this] def iconMenuItemStyle(packageName: String, name: String)(
      implicit contextWrapper: ContextWrapper,
      uiContext: UiContext[_]): Tweak[ImageView] = {
    val size    = resGetDimensionPixelSize(R.dimen.size_widget_icon)
    val padding = resGetDimensionPixelSize(R.dimen.padding_small)
    lp[ViewGroup](size, size) +
      vPaddings(padding) +
      ivSrcByPackageName(Option(packageName), name)
  }

  private[this] def textMenuItemStyle(name: String)(
      implicit contextWrapper: ContextWrapper): Tweak[TextView] =
    vWrapContent +
      tvColorResource(R.color.widgets_text) +
      vPadding(paddingLeft = padding) +
      vAlpha(unselectedAlpha) +
      tvText(name) +
      tvSizeResource(R.dimen.text_default)

}
