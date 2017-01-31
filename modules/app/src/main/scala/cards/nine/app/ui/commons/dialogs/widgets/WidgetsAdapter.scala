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

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.WidgetsOps._
import cards.nine.models.AppWidget
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.TypedResource._
import macroid.FullDsl._
import macroid._

case class WidgetsAdapter(
    widgets: Seq[AppWidget],
    widgetContentWidth: Int,
    widgetContentHeight: Int,
    onClick: (AppWidget => Unit))(
    implicit activityContext: ActivityContextWrapper,
    uiContext: UiContext[_])
    extends RecyclerView.Adapter[ViewHolderWidgetsLayoutAdapter] {

  override def onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int): ViewHolderWidgetsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.widget_item, parent, false)
    ViewHolderWidgetsLayoutAdapter(view)
  }

  override def getItemCount: Int = widgets.size

  override def onBindViewHolder(viewHolder: ViewHolderWidgetsLayoutAdapter, position: Int): Unit = {
    val publicCollection = widgets(position)
    viewHolder.bind(publicCollection, widgetContentWidth, widgetContentHeight, onClick).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

case class ViewHolderWidgetsLayoutAdapter(
    content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
    extends RecyclerView.ViewHolder(content)
    with TypedFindView {

  override protected def findViewById(id: Int): View = content.findViewById(id)

  lazy val preview = findView(TR.widget_item_preview)

  lazy val title = findView(TR.widget_item_title)

  lazy val cells = findView(TR.widget_item_cells)

  def bind(
      widget: AppWidget,
      widgetContentWidth: Int,
      widgetContentHeight: Int,
      onClick: (AppWidget => Unit)): Ui[Any] = {
    val cell = widget.getCell(widgetContentWidth, widgetContentHeight)
    val size = s"${cell.spanX}x${cell.spanY}"
    val iconTweak =
      if (widget.preview > 0)
        ivSrcIconFromPackage(widget.packageName, widget.preview, widget.label)
      else ivSrcByPackageName(Some(widget.packageName), widget.label)
    (content <~
      On.click(Ui(onClick(widget)))) ~
      (preview <~ iconTweak) ~
      (title <~ tvText(widget.label)) ~
      (cells <~ tvText(size))
  }

}
