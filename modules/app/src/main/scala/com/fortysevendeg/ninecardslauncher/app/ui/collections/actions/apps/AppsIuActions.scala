package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.NineCardsCategoryOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.SelectedItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToTabsListener, TabInfo}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{AppDrawerSelectItemsInScroller, NineCardsPreferencesValue}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps, TermCounter}
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

trait AppsIuActions
  extends NineCardIntentConversions
  with Styles {

  dom: BaseActionFragment with AppsDOM with AppsUiListener =>

  val resistance = 2.4f

  lazy val preferences = new NineCardsPreferencesValue

  def initialize(onlyAllApps: Boolean, category: NineCardCategory): TaskService[Unit] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue(preferences)
    val pullToTabsTweaks = if (onlyAllApps) {
      pdvEnable(false)
    } else {
      ptvLinkTabs(
        tabs = Some(dom.tabs),
        start = Ui.nop,
        end = Ui.nop) +
        ptvAddTabsAndActivate(generateTabs(category), 0, Some(colorPrimary)) +
        pdvResistance(resistance) +
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            val filter = if (pos == 0) AppsByCategory else AllApps
            dom.loadApps(filter)
          }
        ))
    }
    val menuTweak = if (onlyAllApps) {
      Tweak.blank
    } else {
      dtvInflateMenu(R.menu.contact_dialog_menu) +
        dtvOnMenuItemClickListener(onItem = {
          case R.id.action_filter =>
            dom.swapFilter()
            true
          case _ => false
        })
    }
    ((dom.scrollerLayout <~ scrollableStyle(colorPrimary)) ~
      (toolbar <~
        dtbInit(colorPrimary) <~
        dtbChangeText(R.string.applications) <~
        menuTweak <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (dom.pullToTabsView <~ pullToTabsTweaks) ~
      (dom.recycler <~ recyclerStyle <~ (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (dom.tabs <~ tvClose)).toService
  }

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (dom.recycler <~ vGone)).toService

  def openTabs(): TaskService[Unit] = ((dom.tabs <~ tvOpen <~ showTabs) ~ (dom.recycler <~ hideList)).toService

  def closeTabs(): TaskService[Unit] = ((dom.tabs <~ tvClose <~ hideTabs) ~ (dom.recycler <~ showList)).toService

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService

  def showErrorLoadingAppsInScreen(filter: AppsFilter): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingApps, error = true, dom.loadApps(filter)).toService

  def showApps(
    category: NineCardCategory,
    filter: AppsFilter,
    apps: IterableApps,
    counters: Seq[TermCounter],
    reload: Boolean
  ): TaskService[Unit] = (if (reload) {
    reloadAppsAdapter(apps, counters, filter, category)
  } else {
    generateAppsAdapter(apps, counters, filter, category, dom.addApp)
  }).toService

  def close(): TaskService[Unit] = unreveal().toService

  def isTabsOpened: TaskService[Boolean] = TaskService.right((dom.tabs ~> isOpened).get)

  private[this] def showData: Ui[_] = (loading <~ vGone) ~ (dom.recycler <~ vVisible)

  private[this] def showGeneralError: Ui[_] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def generateAppsAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
    filter: AppsFilter,
    category: NineCardCategory,
    clickListener: (App) => Unit) = {
    val categoryName = resGetString(category.getStringResource) getOrElse category.getStringResource
    val adapter = AppsAdapter(
      apps = apps,
      clickListener = clickListener,
      longClickListener = None)
    showData ~
      (dom.recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (toolbar <~ dtbChangeText(filter match {
        case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
        case _ => resGetString(R.string.allApps)
      })) ~
      (dom.scrollerLayout <~ fslLinkRecycler(dom.recycler) <~ fslCounters(counters))
  }

  private[this] def reloadAppsAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
    filter: AppsFilter,
    category: NineCardCategory): Ui[_] = {
    val categoryName = resGetString(category.getStringResource) getOrElse category.getStringResource
    showData ~
      (getAdapter map { adapter =>
        Ui(adapter.swapIterator(apps)) ~
          (toolbar <~ dtbChangeText(filter match {
            case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
            case _ => resGetString(R.string.allApps)
          })) ~
          (dom.scrollerLayout <~ fslReset <~ fslCounters(counters)) ~
          (dom.recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[AppsAdapter] = Option(dom.recycler.getAdapter) match {
    case Some(a: AppsAdapter) => Some(a)
    case _ => None
  }

  private[this] def generateTabs(category: NineCardCategory) = Seq(
    TabInfo(
      category.getIconCollectionDetail,
      resGetString(category.getStringResource) getOrElse getString(R.string.appsByCategory)),
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.all_apps))
  )

}