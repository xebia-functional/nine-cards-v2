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

package cards.nine.app.ui.commons.adapters.search

import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import macroid.extras.ViewTweaks._
import cards.nine.commons.ops.ColorOps._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.commons.styles.CommonStyles
import cards.nine.app.ui.commons.ops.DrawableOps._
import cards.nine.app.ui.components.layouts.FastScrollerListener
import cards.nine.app.ui.components.widgets.ScrollingLinearLayoutManager
import cards.nine.app.ui.preferences.commons.{FontSize, IconsSize}
import cards.nine.models.{NineCardsTheme, NotCategorizedPackage}
import cards.nine.models.types.theme.{DrawerIconColor, DrawerTextColor}
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class SearchAdapter(
    apps: Seq[NotCategorizedPackage],
    clickListener: (NotCategorizedPackage) => Unit)(
    implicit val activityContext: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.Adapter[SearchViewHolder]
    with FastScrollerListener {

  val columnsLists = 1

  val heightItem = resGetDimensionPixelSize(R.dimen.height_search_item)

  override def getItemCount: Int = apps.length

  override def onBindViewHolder(vh: SearchViewHolder, position: Int): Unit =
    vh.bind(apps(position)).run

  override def onCreateViewHolder(parent: ViewGroup, i: Int): SearchViewHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.search_item, parent, false)
    new SearchViewHolder(view, clickListener)
  }

  def getLayoutManager: GridLayoutManager =
    new ScrollingLinearLayoutManager(columnsLists)

  override def getHeightAllRows = apps.length / columnsLists * getHeightItem

  override def getHeightItem: Int = heightItem

  override def getColumns: Int = columnsLists
}

class SearchViewHolder(content: ViewGroup, clickListener: (NotCategorizedPackage) => Unit)(
    implicit context: ActivityContextWrapper,
    uiContext: UiContext[_],
    theme: NineCardsTheme)
    extends RecyclerView.ViewHolder(content)
    with TypedFindView
    with CommonStyles {

  lazy val icon = findView(TR.search_item_icon)

  lazy val name = findView(TR.search_item_name)

  lazy val stars = findView(TR.search_item_stars)

  lazy val downloads = findView(TR.search_item_downloads)

  lazy val price = findView(TR.search_item_price)

  def bind(app: NotCategorizedPackage): Ui[_] = {
    val alphaColor = theme.get(DrawerTextColor).alpha(subtitleAlpha)
    val downloadIcon =
      resGetDrawable(R.drawable.icon_download).colorize(theme.get(DrawerIconColor))
    (icon <~
      vResize(IconsSize.getIconApp) <~
      (app.icon map ivUri getOrElse Tweak.blank)) ~
      (name <~
        tvSizeResource(FontSize.getTitleSizeResource) <~
        tvText(app.title) <~
        tvColor(theme.get(DrawerTextColor))) ~
      (stars <~
        ivSrc(getStarDrawable(app.stars)) <~
        tivColor(alphaColor)) ~
      (downloads <~
        tvSizeResource(FontSize.getSizeResource) <~
        tvCompoundDrawablesWithIntrinsicBounds(left = Option(downloadIcon)) <~
        tvText(app.downloads) <~
        tvColor(alphaColor)) ~
      (price <~
        tvText(if (app.free) resGetString(R.string.free) else "") <~
        tvSizeResource(FontSize.getSizeResource) <~
        tvColor(alphaColor)) ~
      (content <~
        On.click {
          Ui(clickListener(app))
        })
  }

  override def findViewById(id: Int): View = content.findViewById(id)
}
