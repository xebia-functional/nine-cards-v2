package com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.ViewHolderCategoryLayoutAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.HeaderedItemAdapter._
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
  extends RecyclerView.Adapter[ItemHeaderedViewHolder[T]] {

  implicit val activityContext: ActivityContextWrapper

  implicit val uiContext: UiContext[_]

  val initialSeq: Seq[ItemHeadered[T]]

  val clickListener: (T) => Unit

  val longClickListener: Option[(T) => Unit]

  val heightHeader = resGetDimensionPixelSize(R.dimen.height_simple_category)

  var seq: Seq[ItemHeadered[T]] = initialSeq

  def createViewHolder(parent: ViewGroup): ItemHeaderedViewHolder[T]

  def getLayoutManager: LinearLayoutManager

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHeaderedViewHolder[T] = viewType match {
    case `itemViewTypeHeader` =>
      val view = LayoutInflater.from(parent.getContext).inflate(R.layout.header_list_item, parent, false).asInstanceOf[ViewGroup]
      new ViewHolderCategoryLayoutAdapter(view)
    case `itemViewTypeContent` =>
      createViewHolder(parent)
  }

  override def getItemCount: Int = seq.size

  override def getItemViewType(position: Int): Int = if (seq(position).header.isDefined) itemViewTypeHeader else itemViewTypeContent

  override def onBindViewHolder(viewHolder: ItemHeaderedViewHolder[T], position: Int): Unit =
    runUi(viewHolder.bind(seq(position), position))

  def loadItems(newSeq: Seq[ItemHeadered[T]]) = {
    seq = newSeq
    notifyDataSetChanged()
  }

}

object HeaderedItemAdapter {
  val itemViewTypeHeader = 0
  val itemViewTypeContent = 1
}