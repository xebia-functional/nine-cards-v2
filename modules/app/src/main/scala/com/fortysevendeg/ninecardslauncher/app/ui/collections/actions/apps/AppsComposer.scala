package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{TabInfo, PullToTabsListener}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, App, IterableApps}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait AppsComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  val resistance = 2.4f

  lazy val recycler = Option(findView(TR.actions_recycler))

  lazy val scrollerLayout = Option(findView(TR.action_scroller_layout))

  lazy val pullToTabsView = Option(findView(TR.actions_pull_to_tabs))

  lazy val tabs = Option(findView(TR.actions_tabs))

  def initUi(onlyAllApps: Boolean, category: NineCardCategory, onChange: (AppsFilter) => Unit)
    (implicit contextWrapper: ContextWrapper): Ui[_] = {
    val pullToTabsTweaks = if (onlyAllApps) {
      pdvEnable(false)
    } else {
      ptvLinkTabs(
        tabs = tabs,
        start = Ui.nop,
        end = Ui.nop) +
        ptvAddTabsAndActivate(generateTabs(category), 0, Some(colorPrimary)) +
        pdvResistance(resistance) +
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            (Ui(onChange(if (pos == 0) AppsByCategory else AllApps)) ~
              (if (isTabsOpened) closeTabs else Ui.nop)).run
          }
        ))
    }
    val menuTweak = if (onlyAllApps) {
      Tweak.blank
    } else {
      dtvInflateMenu(R.menu.contact_dialog_menu) +
        dtvOnMenuItemClickListener(onItem = {
          case R.id.action_filter =>
            (if (isTabsOpened) closeTabs else openTabs).run
            true
          case _ => false
        })
    }
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.applications) <~
      menuTweak <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~ pullToTabsTweaks) ~
      (recycler <~ recyclerStyle) ~
      (tabs <~ tvClose) ~
      (scrollerLayout <~ fslColor(colorPrimary))
  }

  def showLoading: Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

  def generateAppsAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
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
      (toolbar <~ dtbChangeText(filter match {
        case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
        case _ => resGetString(R.string.allApps)
      })) ~
      (recycler map { rv =>
        scrollerLayout <~ fslLinkRecycler(rv) <~ fslCounters(counters)
      } getOrElse showGeneralError)
  }

  def reloadAppsAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
    filter: AppsFilter,
    category: NineCardCategory)(implicit uiContext: UiContext[_]): Ui[_] = {
    val categoryName = resGetString(category.getStringResource) getOrElse category.getStringResource
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(apps)) ~
          (toolbar <~ dtbChangeText(filter match {
            case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
            case _ => resGetString(R.string.allApps)
          })) ~
          (scrollerLayout <~ fslReset <~ fslCounters(counters)) ~
          (recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[AppsAdapter] = recycler flatMap { rv =>
    Option(rv.getAdapter) match {
      case Some(a: AppsAdapter) => Some(a)
      case _ => None
    }
  }

  private[this] def isTabsOpened: Boolean = (tabs ~> isOpened).get getOrElse false

  private[this] def generateTabs(category: NineCardCategory)(implicit contextWrapper: ContextWrapper) = Seq(
    TabInfo(
      iconCollectionDetail(category.getIconResource),
      resGetString(category.getStringResource) getOrElse getString(R.string.appsByCategory)),
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.all_apps))
  )

  protected def openTabs: Ui[_] =
    (tabs <~ tvOpen <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeTabs: Ui[_] =
    (tabs <~ tvClose <~ hideTabs) ~
      (recycler <~ showList)

}