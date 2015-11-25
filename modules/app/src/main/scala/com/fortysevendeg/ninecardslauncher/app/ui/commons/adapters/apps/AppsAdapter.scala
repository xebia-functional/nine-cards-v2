package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.ViewHolderAppLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.{ScrollableManager, HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher2.{TR, R}
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.ActivityContextWrapper

case class AppsAdapter(
  initialSeq: Seq[AppHeadered],
  clickListener: (App) => Unit,
  longClickListener: Option[(App) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[App] {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def inflateView(parent: ViewGroup): ViewGroup =
    LayoutInflater.from(parent.getContext).inflate(TR.layout.app_item, parent, false)

  override def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[App] =
    new ViewHolderAppLayoutAdapter(view)

  override def getLayoutManager: GridLayoutManager = {
    val manager = new GridLayoutManager(activityContext.application, columnsLists) with ScrollableManager {
      override def canScrollVertically: Boolean = if (blockScroll) false else super.canScrollVertically
    }
    manager.setSpanSizeLookup(new SpanSizeLookup {
      override def getSpanSize(position: Int): Int = if (seq(position).header.isDefined) manager.getSpanCount else 1
    })
    manager
  }

  override def getHeight = {
    val heightHeaders = (seq count (_.header.isDefined)) * heightHeader
    // Calculate the number of column showing items
    val rowsWithItems = seq.foldLeft((0, 0))((counter, itemHeadered) =>
      (itemHeadered.header, counter._1, counter._2) match {
        case (Some(_), _, count) => (0, count)
        case (None, 0, count) => (1, count + 1)
        case (None, columns, count) if columns < columnsLists =>
          val newColumn = if (columns == columnsLists - 1) 0 else columns + 1
          (newColumn, count)
        case (None, columns, count) => (0, count)
      })
    val heightItems = rowsWithItems._2 * heightItem
    heightHeaders + heightItems
  }
}