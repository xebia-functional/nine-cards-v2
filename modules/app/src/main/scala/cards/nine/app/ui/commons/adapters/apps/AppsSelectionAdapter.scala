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

package cards.nine.app.ui.commons.adapters.apps

import java.io.Closeable

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.dialogs.apps.AppsFragment._
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.layouts.FastScrollerListener
import cards.nine.app.ui.components.widgets.ScrollingLinearLayoutManager
import cards.nine.app.ui.preferences.commons.{FontSize, IconsSize}
import cards.nine.models.types.theme.{
  DrawerBackgroundColor,
  DrawerTabsBackgroundColor,
  DrawerTextColor
}
import cards.nine.models.{
  ApplicationData,
  EmptyIterableApps,
  IterableApplicationData,
  NineCardsTheme
}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._

case class AppsSelectionAdapter(
    var apps: IterableApplicationData,
    clickListener: (ApplicationData) => Unit)(
    implicit val activityContext: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.Adapter[AppsSelectedIterableHolder]
    with FastScrollerListener
    with Closeable {

  val columnsLists = 4

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def getItemCount: Int = apps.count()

  override def onBindViewHolder(vh: AppsSelectedIterableHolder, position: Int): Unit =
    vh.bind(apps.moveToPosition(position)).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): AppsSelectedIterableHolder = {
    val view =
      LayoutInflater.from(parent.getContext).inflate(TR.layout.app_select_item, parent, false)
    AppsSelectedIterableHolder(view, clickListener)
  }

  def getLayoutManager: GridLayoutManager =
    new ScrollingLinearLayoutManager(columnsLists)

  def swapIterator(iter: IterableApplicationData) = {
    apps.close()
    apps = iter
    notifyDataSetChanged()
  }

  def clear() = {
    apps.close()
    apps = new EmptyIterableApps()
    notifyDataSetChanged()
  }

  override def close() = apps.close()

  override def getHeightAllRows = apps.count() / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

case class AppsSelectedIterableHolder(
    content: ViewGroup,
    clickListener: (ApplicationData) => Unit)(
    implicit context: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.ViewHolder(content)
    with TypedFindView {

  lazy val icon = findView(TR.simple_item_icon)

  lazy val name = findView(TR.simple_item_name)

  lazy val item = findView(TR.app_item_content)

  lazy val selectedIconContent = findView(TR.app_selected_content)

  lazy val selectedIcon = findView(TR.app_selected)

  lazy val selectedColor = resGetColor(R.color.checkbox_selected)

  def selectedDrawable(color: Int) = {
    val drawable = new ShapeDrawable(new OvalShape)
    drawable.getPaint.setColor(color)
    drawable
  }

  val iconSelectedDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.CHECK,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_thin),
    padding = resGetDimensionPixelSize(R.dimen.padding_select_icon))

  (selectedIcon <~ ivSrc(iconSelectedDrawable) <~ vBackground(selectedDrawable(selectedColor))).run

  def bind(app: ApplicationData): Ui[_] = {
    val appSelected = appStatuses.selectedPackages.contains(app.packageName)
    (icon <~ vResize(IconsSize.getIconApp) <~ ivSrcByPackageName(Some(app.packageName), app.name)) ~
      (name <~ tvSizeResource(FontSize.getSizeResource) <~ tvText(app.name) + tvColor(
        theme.get(DrawerTextColor))) ~
      (selectedIconContent <~
        (if (appSelected) vVisible else vGone) <~
        vBackground(selectedDrawable(theme.get(DrawerBackgroundColor)))) ~
      (item <~ (if (appSelected)
                  vBackgroundColor(theme.get(DrawerTabsBackgroundColor))
                else vBlankBackground)) ~
      (content <~ On.click(Ui(clickListener(app))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)
}
