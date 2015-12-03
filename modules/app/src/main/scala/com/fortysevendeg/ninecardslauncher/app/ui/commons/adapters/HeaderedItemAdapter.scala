package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

import android.content.Context
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.{OnLongClickListener, OnClickListener}
import android.view.{View, LayoutInflater, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.ViewHolderCategoryLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.HeaderedItemAdapter._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FastScrollerListener
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

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

  var seq: Seq[ItemHeadered[T]] = initialSeq

  def inflateView(parent: ViewGroup): ViewGroup

  def createViewHolder(view: ViewGroup): ItemHeaderedViewHolder[T]

  def getLayoutManager: LinearLayoutManager

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHeaderedViewHolder[T] = viewType match {
    case `itemViewTypeHeader` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.header_list_item, parent, false).asInstanceOf[ViewGroup]
      new ViewHolderCategoryLayoutAdapter(view)
    case `itemViewTypeContent` =>
      val view = inflateView(parent)
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

  def loadItems(newSeq: Seq[ItemHeadered[T]]) = {
    seq = newSeq
    notifyDataSetChanged()
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