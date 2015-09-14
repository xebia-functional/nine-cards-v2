package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageCardsTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.HeaderUtils
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, ActivityContextWrapper, Ui}

import scala.annotation.tailrec
import scala.math.Ordering.Implicits._

trait AppsComposer
  extends Styles
  with HeaderUtils {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.applications) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (loading <~ vVisible) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(colorPrimary))

  def addApps(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)(implicit fragment: Fragment) = {
    val sortedApps = apps sortBy sortByName // We should sort the apps using queries when the database be ready
    val appsHeadered = generateAppsForList(sortedApps, Seq.empty)
    val adapter = new AppsAdapter(appsHeadered, clickListener)
    (recycler <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

  private[this] def sortByName(app: AppCategorized) = app.name map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

  @tailrec
  private[this] def generateAppsForList(apps: Seq[AppCategorized], acc: Seq[AppHeadered]): Seq[AppHeadered] = apps match {
    case Nil => acc
    case Seq(h, t @ _ *) =>
      val currentChar: String = getCurrentChar(h.name)
      val lastChar: Option[String] = for {
        appHeadered <- acc.lastOption
        appCategorized <- appHeadered.app
        appName <- Option(Option(appCategorized.name) getOrElse charUnnamed)
        c <- Option(generateChar(appName.substring(0, 1)))
      } yield c
      val skipChar = lastChar exists (_ equals currentChar)
      if (skipChar) {
        generateAppsForList(t, acc :+ AppHeadered(app = Option(h)))
      } else {
        generateAppsForList(t, acc ++ Seq(AppHeadered(header = Option(currentChar)), AppHeadered(app = Option(h))))
      }
  }

}

case class ViewHolderAppLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, fragment: Fragment)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: AppCategorized, position: Int)(implicit fragment: Fragment): Ui[_] =
    (icon <~ (app.imagePath map (ivUri(fragment, _, app.name)) getOrElse Tweak.blank)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vIntTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}