package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.{ItemHeaderedViewHolder, ItemHeadered}
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._

case class ViewHolderCategoryLayoutAdapter[T](content: ViewGroup)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
  with ItemHeaderedViewHolder[T]
  with TypedFindView {

  lazy val name = Option(findView(TR.simple_category_name))

  override def findViewById(id: Int): View = content.findViewById(id)

  override def bind(item: ItemHeadered[T], position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    item.header match {
      case Some(h) => name <~ tvText(h)
      case _ => Ui.nop
    }
}