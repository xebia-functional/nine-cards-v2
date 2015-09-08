package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._

case class ViewHolderCategoryLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val name = Option(findView(TR.simple_category_name))

  def bind(category: String)(implicit fragment: Fragment): Ui[_] = name <~ tvText(category)

  override def findViewById(id: Int): View = content.findViewById(id)

}