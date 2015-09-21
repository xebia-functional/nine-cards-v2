package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v7.widget.RecyclerView
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, ActivityContextWrapper, Ui}

trait AppsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  def initUi: Ui[_] =
    (toolbar <~
      tbTitle(R.string.applications) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(colorPrimary))

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def addApps(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)(implicit uiContext: UiContext[_]) = {
    val adapter = new AppsAdapter(
      apps = generateAppHeaderedList(apps),
      clickListener = clickListener)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }

}

case class ViewHolderAppLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_])
  extends RecyclerView.ViewHolder(content)
  with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  def bind(app: AppCategorized, position: Int)(implicit uiContext: UiContext[_]): Ui[_] =
    (icon <~ (app.imagePath map (ivCardUri(_, app.name)) getOrElse Tweak.blank)) ~
      (name <~ tvText(app.name)) ~
      (content <~ vIntTag(position))

  override def findViewById(id: Int): View = content.findViewById(id)

}