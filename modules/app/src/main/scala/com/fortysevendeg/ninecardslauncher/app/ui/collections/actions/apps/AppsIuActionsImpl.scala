package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsPagerPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.NineCardsCategoryOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.SelectedItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{PullToTabsListener, TabInfo}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{AppDrawerSelectItemsInScroller, NineCardsPreferencesValue}
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, IterableApps, TermCounter}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait AppsIuActionsImpl
  extends AppsIuActions
  with NineCardIntentConversions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  implicit val presenter: AppsPresenter

  val collectionsPresenter: CollectionsPagerPresenter

  val resistance = 2.4f

  lazy val recycler = findView(TR.actions_recycler)

  lazy val scrollerLayout = findView(TR.action_scroller_layout)

  lazy val pullToTabsView = findView(TR.actions_pull_to_tabs)

  lazy val tabs = findView(TR.actions_tabs)

  lazy val preferences = new NineCardsPreferencesValue

  override def initialize(onlyAllApps: Boolean, category: NineCardCategory): Ui[_] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue(preferences)
    val pullToTabsTweaks = if (onlyAllApps) {
      pdvEnable(false)
    } else {
      ptvLinkTabs(
        tabs = Some(tabs),
        start = Ui.nop,
        end = Ui.nop) +
        ptvAddTabsAndActivate(generateTabs(category), 0, Some(colorPrimary)) +
        pdvResistance(resistance) +
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            val filter = if (pos == 0) AppsByCategory else AllApps
            presenter.loadApps(filter)
          }
        ))
    }
    val menuTweak = if (onlyAllApps) {
      Tweak.blank
    } else {
      dtvInflateMenu(R.menu.contact_dialog_menu) +
        dtvOnMenuItemClickListener(onItem = {
          case R.id.action_filter =>
            (if (isTabsOpened) closeTabs() else openTabs()).run
            true
          case _ => false
        })
    }
    (scrollerLayout <~ scrollableStyle(colorPrimary)) ~
      (toolbar <~
        dtbInit(colorPrimary) <~
        dtbChangeText(R.string.applications) <~
        menuTweak <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~ pullToTabsTweaks) ~
      (recycler <~ recyclerStyle <~ (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (tabs <~ tvClose)
  }

  override def showLoading(): Ui[_] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def closeTabs(): Ui[_] = (tabs <~ tvClose <~ hideTabs) ~ (recycler <~ showList)

  override def destroy(): Ui[Any] = Ui {
    getAdapter foreach(_.close())
  }

  override def showErrorLoadingAppsInScreen(filter: AppsFilter): Ui[Any] =
    showMessageInScreen(R.string.errorLoadingApps, error = true, presenter.loadApps(filter))

  override def showApps(
    category: NineCardCategory,
    filter: AppsFilter,
    apps: IterableApps,
    counters: Seq[TermCounter],
    reload: Boolean
  ): Ui[Any] = if (reload) {
    reloadAppsAdapter(apps, counters, filter, category)
  } else {
    generateAppsAdapter(apps, counters, filter, category, presenter.addApp)
  }

  override def appAdded(card: AddCardRequest): Ui[Any] = {
    collectionsPresenter.addCards(Seq(card))
    unreveal()
  }

  override def isTabsOpened: Boolean = (tabs ~> isOpened).get

  private[this] def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

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
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (toolbar <~ dtbChangeText(filter match {
        case AppsByCategory => resGetString(R.string.appsByCategory, categoryName)
        case _ => resGetString(R.string.allApps)
      })) ~
      (scrollerLayout <~ fslLinkRecycler(recycler) <~ fslCounters(counters))
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
          (scrollerLayout <~ fslReset <~ fslCounters(counters)) ~
          (recycler <~ rvScrollToTop)
      } getOrElse showGeneralError)
  }

  private[this] def getAdapter: Option[AppsAdapter] = Option(recycler.getAdapter) match {
    case Some(a: AppsAdapter) => Some(a)
    case _ => None
  }

  private[this] def generateTabs(category: NineCardCategory) = Seq(
    TabInfo(
      category.getIconCollectionDetail,
      resGetString(category.getStringResource) getOrElse getString(R.string.appsByCategory)),
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.all_apps))
  )

  private[this] def openTabs(): Ui[_] = (tabs <~ tvOpen <~ showTabs) ~ (recycler <~ hideList)

}