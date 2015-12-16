package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.support.v7.widget.SwitchCompat
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, Ui}

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
      vgAddView(getUi(
        w[SwitchCompat] <~
          wire(switch) <~
          scColor(colorPrimary) <~
          scChecked(checked = true) <~
          scCheckedChangeListener(onCheckedChange)
      ))
    }
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.applications) <~
      switchViewTweak <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(colorPrimary))
  }

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone) ~ (scrollerLayout <~ fslInvisible)

  def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible) ~ (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateAppsAdapter(
    apps: IterableApps,
    filter: AppsFilter,
    category: NineCardCategory,
    clickListener: (App) => Unit)(implicit uiContext: UiContext[_]) = {
    val categoryName = resGetString(category.getStringResource) getOrElse category.getStringResource
    val adapter = new AppsAdapter(
      apps = apps,
      clickListener = clickListener,
      longClickListener = None)
    showData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (scrollerLayout <~
        fslLinkRecycler <~
        (filter match {
          case AllApps => fslVisible
          case AppsByCategory => fslInvisible
        }))
  }

  def reloadAppsAdapter(
    apps: IterableApps,
    filter: AppsFilter,
    category: NineCardCategory)(implicit uiContext: UiContext[_]): Ui[_] = {
    val categoryName = resGetString(category.getStringResource) getOrElse category.getStringResource
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(apps)) ~
          (rootContent <~ uiSnackbarShort(filter match {
            case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
            case _ => resGetString(R.string.allApps)
          })) ~
          (scrollerLayout <~
            fslReset <~
            (filter match {
              case AllApps => fslVisible
              case AppsByCategory => fslInvisible
            })) ~
          (recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[AppsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: AppsAdapter) => Some(a)
      case _ => None
    }
  }

}