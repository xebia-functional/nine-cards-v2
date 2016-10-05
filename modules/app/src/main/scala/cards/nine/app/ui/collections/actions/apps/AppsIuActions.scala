package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.adapters.apps.AppsAdapter
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.commons.SelectedItemDecoration
import cards.nine.app.ui.components.layouts.snails.TabsSnails._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TabsViewTweaks._
import cards.nine.app.ui.components.layouts.{PullToTabsListener, TabInfo}
import cards.nine.app.ui.preferences.commons.{AppDrawerSelectItemsInScroller, NineCardsPreferencesValue}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.ApplicationData
import cards.nine.models.types.NineCardCategory
import cards.nine.process.device.models.{IterableApps, TermCounter}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

trait AppsIuActions
  extends AppNineCardIntentConversions
  with Styles {

  self: BaseActionFragment with AppsDOM with AppsUiListener =>

  val resistance = 2.4f

  lazy val preferences = new NineCardsPreferencesValue

  def initialize(onlyAllApps: Boolean, category: NineCardCategory): TaskService[Unit] = {
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
            loadApps(filter)
          }
        ))
    }
    val menuTweak = if (onlyAllApps) {
      Tweak.blank
    } else {
      dtvInflateMenu(R.menu.contact_dialog_menu) +
        dtvOnMenuItemClickListener(onItem = {
          case R.id.action_filter =>
            swapFilter()
            true
          case _ => false
        })
    }
    ((scrollerLayout <~ scrollableStyle(colorPrimary)) ~
      (toolbar <~
        dtbInit(colorPrimary) <~
        dtbChangeText(R.string.applications) <~
        menuTweak <~
        dtbNavigationOnClickListener((_) => unreveal())) ~
      (pullToTabsView <~ pullToTabsTweaks) ~
      (recycler <~ recyclerStyle <~ (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (tabs <~ tvClose)).toService
  }

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showError(): TaskService[Unit] = showGeneralError.toService

  def openTabs(): TaskService[Unit] = ((tabs <~ tvOpen <~ showTabs) ~ (recycler <~ hideList)).toService

  def closeTabs(): TaskService[Unit] = ((tabs <~ tvClose <~ hideTabs) ~ (recycler <~ showList)).toService

  def destroy(): TaskService[Unit] = Ui {
    getAdapter foreach(_.close())
  }.toService

  def showErrorLoadingAppsInScreen(filter: AppsFilter): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingApps, error = true, loadApps(filter)).toService

  def showApps(
    category: NineCardCategory,
    filter: AppsFilter,
    apps: IterableApps,
    counters: Seq[TermCounter],
    reload: Boolean
  ): TaskService[Unit] = (if (reload) {
    reloadAppsAdapter(apps, counters, filter, category)
  } else {
    generateAppsAdapter(apps, counters, filter, category, addApp)
  }).toService

  def close(): TaskService[Unit] = unreveal().toService

  def isTabsOpened: TaskService[Boolean] = TaskService.right((tabs ~> isOpened).get)

  private[this] def showData: Ui[_] = (loading <~ vGone) ~ (recycler <~ vVisible)

  private[this] def showGeneralError: Ui[_] = rootContent <~ vSnackbarShort(R.string.contactUsError)

  private[this] def generateAppsAdapter(
    apps: IterableApps,
    counters: Seq[TermCounter],
    filter: AppsFilter,
    category: NineCardCategory,
    clickListener: (ApplicationData) => Unit) = {
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

  private[this] def generateTabs(category: NineCardCategory) = Seq(
    TabInfo(
      category.getIconCollectionDetail,
      resGetString(category.getStringResource) getOrElse getString(R.string.appsByCategory)),
    TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.all_apps))
  )

}