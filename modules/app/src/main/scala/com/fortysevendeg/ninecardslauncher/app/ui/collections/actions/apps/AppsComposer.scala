package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageCardsTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._

import scala.annotation.tailrec

trait AppsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val toolbar = Option(findView(TR.actions_toolbar))

  lazy val loading = Option(findView(TR.action_loading))

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  def initUi: Ui[_] =
    (toolbar <~
      toolbarStyle <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (loading <~ vVisible) ~
      (recycler <~ recyclerStyle)

  def addApps(apps: Seq[AppCategorized])(implicit fragment: Fragment) = {
    val adapter = new AppsAdapter(generateAppsForList(apps.sortBy(_.name).toList, Seq.empty))
    (recycler <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  @tailrec
  private[this] def generateAppsForList(apps: List[AppCategorized], acc: Seq[AppHeadered]): Seq[AppHeadered] = apps match {
    case Nil => acc
    case h :: t =>
      val currentChar = h.name.substring(0, 1)
      val lastChar = acc.lastOption flatMap (_.app map (_.name.substring(0, 1)))
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateAppsForList(t, acc :+ AppHeadered(app = Option(h)))
      } else {
        generateAppsForList(t, acc ++ Seq(AppHeadered(header = Option(currentChar)), AppHeadered(app = Option(h))))
      }
  }

}

case class ViewHolderCategoryLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val name = Option(findView(TR.simple_category_name))

  def bind(category: String)(implicit fragment: Fragment): Ui[_] = name <~ tvText(category)

  override def findViewById(id: Int): View = content.findViewById(id)

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