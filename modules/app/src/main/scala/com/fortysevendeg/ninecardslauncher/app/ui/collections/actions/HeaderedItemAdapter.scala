package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.view.View.{OnLongClickListener, OnClickListener}
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.HeaderedItemAdapter._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.ViewHolderCategoryLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerListener
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._

trait ItemHeadered[T] {
  
  val item: Option[T]
  
  val header: Option[String]
}

trait ItemHeaderedViewHolder[T] extends RecyclerView.ViewHolder {
  
  def bind(item: ItemHeadered[T], position: Int)(implicit uiContext: UiContext[_]): Ui[_]
}

trait HeaderedItemAdapter[T]
  extends RecyclerView.Adapter[ItemHeaderedViewHolder[T]]
  with FastScrollerListener {

  implicit val activityContext: ActivityContextWrapper

  implicit val uiContext: UiContext[_]

  val initialSeq: Seq[ItemHeadered[T]]

  val clickListener: (T) => Unit

  val longClickListener: Option[(T) => Unit]

  val heightHeader = resGetDimensionPixelSize(R.dimen.height_simple_category)

  val heightItem = resGetDimensionPixelSize(R.dimen.height_simple_item)

  var seq: Seq[ItemHeadered[T]] = initialSeq

  def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[T]

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHeaderedViewHolder[T] = viewType match {
    case `itemViewTypeHeader` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.simple_category, parent, false).asInstanceOf[ViewGroup]
      new ViewHolderCategoryLayoutAdapter(view)
    case `itemViewTypeContent` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.simple_item, parent, false).asInstanceOf[ViewGroup]
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
      createViewHolder(view)
  }

  override def getItemCount: Int = seq.size

  override def getItemViewType(position: Int): Int = if (seq(position).header.isDefined) itemViewTypeHeader else itemViewTypeContent

  override def onBindViewHolder(viewHolder: ItemHeaderedViewHolder[T], position: Int): Unit =
    runUi(viewHolder.bind(seq(position), position))

  def getLayoutManager: GridLayoutManager = {
    val manager = new GridLayoutManager(activityContext.application, numInLine)
    manager.setSpanSizeLookup(new SpanSizeLookup {
      override def getSpanSize(position: Int): Int = if (seq(position).header.isDefined) manager.getSpanCount else 1
    })
    manager
  }

  def loadItems(newSeq: Seq[ItemHeadered[T]]) = {
    seq = newSeq
    notifyDataSetChanged()
  }

  override def getHeight = {
    val heightHeaders = (seq count (_.header.isDefined)) * heightHeader
    // Calculate the number of column showing items
    val rowsWithItems = seq.foldLeft((0, 0))((counter, itemHeadered) =>
      (itemHeadered.header, counter._1, counter._2) match {
        case (Some(_), _, count) => (0, count)
        case (None, 0, count) => (1, count + 1)
        case (None, columns, count) if columns < numInLine =>
          val newColumn = if (columns == numInLine - 1) 0 else columns + 1
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

object HeaderedItemAdapter {
  val itemViewTypeHeader = 0
  val itemViewTypeContent = 1
}