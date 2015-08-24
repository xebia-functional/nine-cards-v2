package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageCardsTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

trait AppsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val toolbar = Option(findView(TR.actions_toolbar))

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      toolbarStyle <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  def addApps(apps: Seq[AppCategorized])(implicit fragment: Fragment) = {
    val adapter = new AppsAdapter(apps.sortBy(_.name))
    recycler <~
      rvAdapter(adapter)
  }

}

case class ViewHolderAppLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, fragment: Fragment)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: AppCategorized, position: Int)(implicit fragment: Fragment): Ui[_] =
    (icon <~ ivUri(fragment, app.imagePath.get, app.name)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vIntTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}