package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v7.widget.Toolbar.LayoutParams
import android.support.v7.widget.{SwitchCompat, RecyclerView}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
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

  var switch = slot[SwitchCompat]

  def initUi(allApps: Boolean, onCheckedChange: (Boolean) => Unit): Ui[_] = {
    val switchViewTweak = if (allApps) {
      Tweak.blank
    } else {
      val padding = resGetDimensionPixelSize(R.dimen.padding_default)
      val switchParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL)
      switchParams.setMarginStart(padding)
      switchParams.setMarginEnd(padding)
      vgAddView(getUi(
        w[SwitchCompat] <~
          wire(switch) <~
          scColor(colorPrimary) <~
          scChecked(checked = true) <~
          scCheckedChangeListener(onCheckedChange)
      ), switchParams)
    }
    (toolbar <~
      tbTitle(R.string.applications) <~
      toolbarStyle(colorPrimary) <~
      switchViewTweak <~
      tbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(colorPrimary))
  }

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateAppsAdapter(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)(implicit uiContext: UiContext[_]) = {
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

  def reloadAppsAdapter(apps: Seq[AppCategorized], filter: AppsFilter, category: String)(implicit uiContext: UiContext[_]): Ui[_] = {
    val contactsHeadered = generateAppHeaderedList(apps)
    val categoryName = resGetString(category.toLowerCase()) getOrElse category.toLowerCase()
    (recycler <~ vVisible) ~
      (loading <~ vGone) ~
      (getAdapter map { adapter =>
        Ui(adapter.loadApps(contactsHeadered)) ~
          (rootContent <~ uiSnackbarShort(filter match {
            case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
            case _ => resGetString(R.string.allApps)
          }))
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[AppsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: AppsAdapter) => Some(a)
      case _ => None
    }
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