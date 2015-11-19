package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.view.View.{OnLongClickListener, OnClickListener}
import android.view.{View, LayoutInflater, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.ViewHolderAppLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.{HeaderedItemAdapter, ItemHeaderedViewHolder}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerListener
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

case class AppsAdapter(
  initialSeq: Seq[AppHeadered],
  clickListener: (App) => Unit,
  longClickListener: Option[(App) => Unit])
  (implicit val activityContext: ActivityContextWrapper, implicit val uiContext: UiContext[_])
  extends HeaderedItemAdapter[App]
  with FastScrollerListener {

  val heightItem = resGetDimensionPixelSize(R.dimen.height_app_item)

  override def createViewHolder(parent: ViewGroup): ItemHeaderedViewHolder[App] = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.app_item, parent, false).asInstanceOf[ViewGroup]
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Option(v.getTag) foreach (tag => seq(Int.unbox(tag)).item foreach clickListener)
      }
    })
    longClickListener foreach { listener =>
      view.setOnLongClickListener(new OnLongClickListener {
        override def onLongClick(v: View): Boolean = {
          Option(v.getTag) foreach (tag => seq(Int.unbox(tag)).item foreach listener)
          true
        }
      })
    }
    new ViewHolderAppLayoutAdapter(view)
  }

  override def getLayoutManager: GridLayoutManager = {
    val manager = new GridLayoutManager(activityContext.application, columnsLists)
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

  val defaultElement: Option[String] = None

  override def getElement(position: Int): Option[String] = seq.foldLeft((defaultElement, false))((info, itemHeadered) =>
    if (itemHeadered == seq(position)) {
      (info._1, true)
    } else {
      (info._1, info._2) match {
        case (_, false) => itemHeadered.header map (header => (Option(header), info._2)) getOrElse info
        case _ => info
      }
    }
  )._1

}