package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

import scala.math.Ordering.Implicits._

trait ShortcutsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.shortcuts) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (loading <~ vVisible) ~
      (recycler <~ recyclerStyle)

  def addShortcuts(shortcuts: Seq[Shortcut], clickListener: (Shortcut) => Unit)(implicit fragment: Fragment) = {
    val sortedShortcuts = shortcuts sortBy sortByTitle
    val adapter = new ShortcutsAdapter(sortedShortcuts, clickListener)
    (recycler <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  private[this] def sortByTitle(shortcut: Shortcut) = shortcut.title map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

}

case class ViewHolderShortcutLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, fragment: Fragment)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(shortcut: Shortcut, position: Int)(implicit fragment: Fragment): Ui[_] =
    (icon <~ (shortcut.icon map ivSrc getOrElse Tweak.blank)) ~
      (name <~ tvText(shortcut.title)) ~
      (content <~ vIntTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}