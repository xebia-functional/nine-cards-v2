package cards.nine.app.ui.components.layouts.tweaks

import android.appwidget.AppWidgetHostView
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.view.{MenuItem, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.components.layouts.AnimatedWorkSpaces._
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.models.{
  CollectionsWorkSpace,
  LauncherData,
  LauncherMoment,
  WorkSpaceType
}
import cards.nine.app.ui.components.widgets.ContentView
import cards.nine.app.ui.launcher.holders.{
  LauncherWorkSpaceCollectionsHolder,
  LauncherWorkSpaceMomentsHolder
}
import cards.nine.app.ui.launcher.jobs.{LauncherJobs, NavigationJobs, WidgetsJobs}
import cards.nine.models.types.{
  ConditionWeather,
  DialogToolbarTitle,
  DialogToolbarType,
  NineCardsMoment
}
import cards.nine.models.{NineCardsTheme, TermCounter, _}
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.ResourcesExtras._

import scala.concurrent.ExecutionContext.Implicits.global

object LauncherWorkSpacesTweaks {
  type W = LauncherWorkSpaces

  def lwsData(data: Seq[LauncherData], pageSelected: Int) = Tweak[W] { view =>
    view.init(data, pageSelected)
  }

  def lwsDataCollections(data: Seq[LauncherData], pageCollectionSelected: Option[Int]) = Tweak[W] {
    view =>
      view.data.headOption match {
        case Some(moment) =>
          val page = pageCollectionSelected map (_ + 1) getOrElse view.currentPage()
          view.init(moment +: data, page)
        case _ =>
      }
  }

  def lwsDataMoment(moment: LauncherData) = Tweak[W](_.reloadMoment(moment))

  def lwsReloadMomentCollection(collection: Option[Collection]) =
    Tweak[W](_.changeCollectionInMoment(collection))

  def lwsAddWidget(widgetView: AppWidgetHostView, cell: Cell, widget: Widget) =
    Tweak[W](_.addWidget(widgetView, cell, widget))

  def lwsAddNoConfiguredWidget(wCell: Int, hCell: Int, widget: Widget) =
    Tweak[W](_.addNoConfiguredWidget(wCell, hCell, widget))

  def lwsReplaceWidget(widgetView: AppWidgetHostView, wCell: Int, hCell: Int, widget: Widget) =
    Tweak[W](_.addReplaceWidget(widgetView, wCell, hCell, widget))

  def lwsStartEditWidgets() = Tweak[W](_.startEditWidget())

  def lwsCloseEditWidgets() = Tweak[W](_.closeEditWidget())

  def lwsReloadSelectedWidget() = Tweak[W](_.reloadSelectedWidget())

  def lwsClearWidgets() = Tweak[W](_.clearWidgets())

  def lwsUnhostWidget(id: Int) = Tweak[W](_.unhostWidget(id))

  def lwsClean = Tweak[W](_.clean())

  def lwsOpenMenu = Tweak[W](_.openMenu())

  def lwsListener(listener: LauncherWorkSpacesListener) =
    Tweak[W](_.workSpacesListener = listener)

  def lwsSelect(position: Int) = Tweak[W](_.selectPosition(position))

  def lwsCloseMenu = Snail[W](_.closeMenu().get map (_ => ()))

  def lwsPrepareItemsScreenInReorder(position: Int) =
    Tweak[W](_.prepareItemsScreenInReorder(position).run)

  def lwsDragReorderWidgetDispatcher(action: Int, x: Float, y: Float) =
    Tweak[W] {
      _.getCurrentView match {
        case Some(holder: LauncherWorkSpaceMomentsHolder) =>
          holder.dragReorderController(action, x, y)
        case _ =>
      }
    }

  def lwsDragAddItemDispatcher(action: Int, x: Float, y: Float) = Tweak[W] {
    _.getCurrentView match {
      case Some(holder: LauncherWorkSpaceCollectionsHolder) =>
        holder.dragAddItemController(action, x, y)
      case _ =>
    }
  }

  def lwsDragReorderCollectionDispatcher(action: Int, x: Float, y: Float) =
    Tweak[W] {
      _.getCurrentView match {
        case Some(holder: LauncherWorkSpaceCollectionsHolder) =>
          holder.dragReorderCollectionController(action, x, y)
        case _ =>
      }
    }

  def lwsAddMovementObserver(observer: ((LauncherData, LauncherData, Boolean, Float) => Unit)) =
    Tweak[W](_.addMovementObservers(observer))

  def lwsCurrentPage() = Excerpt[W, Int](_.currentPage())

  def lwsCountCollections() = Excerpt[W, Int](_.getCountCollections)

  def lwsGetCollections() = Excerpt[W, Seq[Collection]](_.getCollections)

  def lwsEmptyCollections() = Excerpt[W, Boolean](_.isEmptyCollections)

  def lwsCanMoveToNextScreen() = Excerpt[W, Boolean](_.nextScreen.isDefined)

  def lwsNextScreen() = Excerpt[W, Option[Int]](_.nextScreen)

  def lwsPreviousScreen() = Excerpt[W, Option[Int]](_.previousScreen)

  def lwsCanMoveToPreviousScreen() =
    Excerpt[W, Boolean](_.previousScreen.isDefined)

  def lwsIsCollectionWorkspace(page: Int) =
    Excerpt[W, Boolean](_.isCollectionWorkSpace(page))

  def lwsIsCollectionWorkspace = Excerpt[W, Boolean](_.isCollectionWorkSpace)

  def lwsCanMoveToPreviousScreenOnlyCollections() = Excerpt[W, Boolean] { view =>
    (for {
      previousScreen <- view.previousScreen
    } yield view.isCollectionWorkSpace(previousScreen)) getOrElse false
  }

  def lwsCanMoveToNextScreenOnlyCollections() = Excerpt[W, Boolean] { view =>
    (for {
      nextScreen <- view.nextScreen
    } yield view.isCollectionWorkSpace(nextScreen)) getOrElse false
  }

}

object AnimatedWorkSpacesTweaks {

  type W = AnimatedWorkSpaces[_, _]

  def awsListener(listener: AnimatedWorkSpacesListener) =
    Tweak[W](_.listener = listener)

  def awsDisabled() =
    Tweak[W](aws =>
      aws.animatedWorkspaceStatuses = aws.animatedWorkspaceStatuses.copy(enabled = false))

  def awsEnabled() =
    Tweak[W](aws =>
      aws.animatedWorkspaceStatuses = aws.animatedWorkspaceStatuses.copy(enabled = true))

  def awsAddPageChangedObserver(observer: PageChangedObserver) =
    Tweak[W](_.addPageChangedObservers(observer))

  def awsCurrentWorkSpace() =
    Excerpt[W, Int](_.animatedWorkspaceStatuses.currentItem)

  def awsCountWorkSpace() = Excerpt[W, Int](_.getWorksSpacesCount)

}

object FabItemMenuTweaks {
  type W = FabItemMenu

  def fimBackgroundColor(color: Int) = Tweak[W](_.changeBackground(color).run)

  def fimPopulate(backgroundColor: Int, resourceId: Int, text: Int) =
    Tweak[W](_.populate(backgroundColor, resourceId, text).run)

}

object WorkSpaceItemMenuTweaks {
  type W = WorkspaceItemMenu

  def wimPopulate(backgroundColor: Int, resourceId: Int, text: Int) =
    Tweak[W](_.populate(backgroundColor, resourceId, text).run)

}

object WorkSpaceButtonTweaks {
  type W = WorkSpaceButton

  def wbInit(t: WorkSpaceButtonType)(implicit theme: NineCardsTheme) =
    Tweak[W](_.init(t).run)

  def wbPopulateCollection(collection: Collection)(implicit theme: NineCardsTheme) =
    Tweak[W](_.populateCollection(collection).run)

  def wbPopulateCard(card: Card) = Tweak[W](_.populateCard(card).run)

  def wbPopulateIcon(resIcon: Int, resTitle: Int, resColor: Int) =
    Tweak[W](_.populateIcon(resIcon, resTitle, resColor).run)

}

object StepsWorkspacesTweaks {
  type W = StepsWorkspaces

  def swData(data: Seq[StepData]) = Tweak[W](_.init(data))

  def swAddMovementObserver(observer: ((StepData, StepData, Boolean, Float) => Unit)) =
    Tweak[W](_.addMovementObservers(observer))

}

object WizardInlineWorkspacesTweaks {
  type W = WizardInlineWorkspaces

  def wiwData(data: Seq[WizardInlineData]) = Tweak[W](_.init(data))

  def wiwAddMovementObserver(
      observer: ((WizardInlineData, WizardInlineData, Boolean, Float) => Unit)) =
    Tweak[W](_.addMovementObservers(observer))

}

object SearchBoxesViewTweaks {
  type W = SearchBoxView

  def sbvUpdateContentView(contentView: ContentView)(implicit theme: NineCardsTheme) =
    Tweak[W](_.updateContentView(contentView).run)

  def sbvChangeListener(listener: SearchBoxAnimatedListener) =
    Tweak[W](_.listener = Some(listener))

  def sbvUpdateHeaderIcon(icon: Int)(implicit theme: NineCardsTheme) =
    Tweak[W](_.updateHeaderIcon(icon).run)

  def sbvOnChangeText(onChangeText: (String) => Unit) =
    Tweak[W](_.addTextChangedListener(onChangeText))

  def sbvShowKeyboard = Tweak[W](_.showKeyboard.run)

  def sbvClean = Tweak[W](_.clean.run)

  def sbvEnableSearch = Tweak[W](_.enableSearch.run)

  def sbvDisableSearch = Tweak[W](_.disableSearch.run)
}

object TabsViewTweaks {

  val openedField = "opened"

  def tvOpen: Tweak[View] = vAddField(openedField, true)

  def tvClose: Tweak[View] = vAddField(openedField, false)

  def isOpened =
    Excerpt[LinearLayout, Boolean](_.getField[Boolean](openedField) getOrElse false)

}

object PullToTabsViewTweaks {

  def ptvAddTabsAndActivate(items: Seq[TabInfo], index: Int, colorPrimary: Option[Int])(
      implicit theme: NineCardsTheme) =
    Tweak[PullToTabsView](_.addTabs(items, colorPrimary, Some(index)))

  def ptvAddTabs(items: Seq[TabInfo], colorPrimary: Option[Int])(implicit theme: NineCardsTheme) =
    Tweak[PullToTabsView](_.addTabs(items, colorPrimary))

  def ptvLinkTabs(tabs: Option[LinearLayout], start: () => Ui[Any], end: () => Ui[Any]) =
    Tweak[PullToTabsView] { view =>
      view.linkTabsView(tabs, start, end).run
    }

  def ptvClearTabs() = Tweak[PullToTabsView](_.clear())

  def ptvActivate(item: Int) = Tweak[PullToTabsView](_.selectItem(item).run)

  def ptvListener(pullToTabsListener: PullToTabsListener) =
    Tweak[PullToTabsView](_.tabsListener = pullToTabsListener)

}

object PullToCloseViewTweaks {

  def pcvListener(pullToCloseListener: PullToCloseListener) =
    Tweak[PullToCloseView](_.closeListeners = pullToCloseListener)

}

object PullToDownViewTweaks {

  def pdvPullingListener(pullToDownListener: PullingListener) =
    Tweak[PullToDownView](_.pullingListeners = pullToDownListener)

  def pdvHorizontalListener(horizontalMovementListener: HorizontalMovementListener) =
    Tweak[PullToDownView](_.horizontalListener = horizontalMovementListener)

  def pdvEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(enabled = enabled)
    }

  def pdvIsEnabled() =
    Excerpt[PullToDownView, Boolean](_.pullToDownStatuses.enabled)

  def pdvHorizontalEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(scrollHorizontalEnabled = enabled)
    }

  def pdvResistance(resistance: Float) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(resistance = resistance)
    }

  def pdvIsPulling() =
    Excerpt[PullToDownView, Boolean](_.pullToDownStatuses.action == Pulling)

}

object FastScrollerLayoutTweak {
  // We should launch this tweak when the adapter has been added
  def fslLinkRecycler(recyclerView: RecyclerView) =
    Tweak[FastScrollerLayout](_.linkRecycler(recyclerView))

  def fslColor(color: Int, backgroundColor: Int) =
    Tweak[FastScrollerLayout](_.setColor(color, backgroundColor))

  def fslMarginRightBarContent(pixels: Int) =
    Tweak[FastScrollerLayout](_.setMarginRightBarContent(pixels))

  def fslEnabledScroller(enabled: Boolean) =
    Tweak[FastScrollerLayout](_.setEnabledScroller(enabled))

  def fslReset = Tweak[FastScrollerLayout](_.reset)

  def fslCounters(counters: Seq[TermCounter]) =
    Tweak[FastScrollerLayout](_.setCounters(counters))

  def fslSignalType(signalType: FastScrollerSignalType) =
    Tweak[FastScrollerLayout](_.setSignalType(signalType))

}

object SlidingTabLayoutTweaks {
  type W = SlidingTabLayout

  def stlViewPager(viewPager: ViewPager): Tweak[W] =
    Tweak[W](_.setViewPager(viewPager))

  def stlDefaultTextColor(color: Int): Tweak[W] =
    Tweak[W](_.setDefaultTextColor(color))

  def stlSelectedTextColor(color: Int): Tweak[W] =
    Tweak[W](_.setSelectedTextColor(color))

  def stlTabStripColor(color: Int): Tweak[W] =
    Tweak[W](_.setTabStripColor(color))

  def stlOnPageChangeListener(listener: ViewPager.OnPageChangeListener): Tweak[W] =
    Tweak[W](_.setOnPageChangeListener(listener))
}

object DialogToolbarTweaks {

  type W = DialogToolbar

  def dtbInit(color: Int, dialogToolbarType: DialogToolbarType = DialogToolbarTitle)(
      implicit contextWrapper: ContextWrapper) =
    Tweak[W](_.init(color, dialogToolbarType).run)

  def dtbExtended(implicit contextWrapper: ContextWrapper) = Tweak[W] {
    _.changeToolbarHeight(resGetDimensionPixelSize(R.dimen.height_extended_toolbar_dialog)).run
  }

  def dtbAddExtendedView(viewToAdd: View)(implicit contextWrapper: ContextWrapper) = Tweak[W] {
    _.addExtendedView(viewToAdd).run
  }

  def dtbSetIcon(icon: Int) = Tweak[W](_.changeIcon(icon).run)

  def dtbChangeText(resourceId: Int) = Tweak[W](_.changeText(resourceId).run)

  def dtbChangeText(text: String) = Tweak[W](_.changeText(text).run)

  def dtbChangeSearchText(resourceId: Int) =
    Tweak[W](_.changeSearchText(resourceId).run)

  def dtbChangeSearchText(text: String) =
    Tweak[W](_.changeSearchText(text).run)

  def dtbResetText() = Tweak[W](_.changeSearchText().run)

  def dtbOnSearchTextChangedListener(onChanged: (String, Int, Int, Int) => Unit) =
    Tweak[W](_.onSearchTextChangedListener(onChanged).run)

  def dtbClickActionSearch(performSearch: (String) => Unit) =
    Tweak[W](_.clickActionSearch(performSearch).run)

  def dtbHideKeyboardSearchText() = Tweak[W](_.hideKeyboardSearchText().run)

  def dtbNavigationOnClickListener(click: (View) => Ui[_]) =
    Tweak[W](_.navigationClickListener(click).run)

  def dtvInflateMenu(res: Int) = Tweak[W](_.toolbar.inflateMenu(res))

  def dtvOnMenuItemClickListener(onItem: (Int) => Boolean) = Tweak[W] { view =>
    view.toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener {
      override def onMenuItemClick(menuItem: MenuItem): Boolean =
        onItem(menuItem.getItemId)
    })
  }

}

object DockAppsPanelLayoutTweaks {
  type W = DockAppsPanelLayout

  def daplInit(
      dockApps: Seq[DockAppData])(implicit theme: NineCardsTheme, uiContext: UiContext[_]) =
    Tweak[W](_.init(dockApps).run)

  def daplDragDispatcher(action: Int, x: Float, y: Float) =
    Tweak[W](_.dragAddItemController(action, x, y))

  def daplReload(dockApp: DockAppData)(implicit theme: NineCardsTheme, uiContext: UiContext[_]) =
    Tweak[W](_.reload(dockApp).run)

  def daplReset() = Tweak[W](_.reset().run)

}

object CollectionActionsPanelLayoutTweaks {
  type W = CollectionActionsPanelLayout

  def caplLoad(actions: Seq[CollectionActionItem])(implicit theme: NineCardsTheme) =
    Tweak[W](_.load(actions).run)

  def caplDragDispatcher(action: Int, x: Float, y: Float)(implicit theme: NineCardsTheme) =
    Tweak[W](_.dragController(action, x, y))

}

object TopBarLayoutTweaks {

  type W = TopBarLayout

  def tblInit(workSpaceType: WorkSpaceType)(
      implicit theme: NineCardsTheme,
      navigationJobs: NavigationJobs) =
    Tweak[W](_.init(workSpaceType).run)

  def tblReload(implicit theme: NineCardsTheme, navigationJobs: NavigationJobs) =
    Tweak[W](_.populate.run)

  def tblReloadMoment(moment: NineCardsMoment)(
      implicit theme: NineCardsTheme,
      launcherJobs: LauncherJobs,
      navigationJobs: NavigationJobs) =
    Tweak[W](_.reloadMoment(moment).run)

  def tblReloadByType(workSpaceType: WorkSpaceType)(implicit contextWrapper: ContextWrapper) =
    Tweak[W](_.reloadByType(workSpaceType).run)

  def tblWeather(condition: ConditionWeather) =
    Tweak[W](_.setWeather(condition).run)

}

object AppsMomentLayoutTweaks {

  type W = AppsMomentLayout

  def amlPopulate(
      moment: LauncherMoment)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    Tweak[W](_.populate(moment).run)

  def amlPaddingTopAndBottom(paddingTop: Int, paddingBottom: Int) =
    Tweak[W](_.setPaddingTopAndBottom(paddingTop, paddingBottom).run)

}

object EditHourMomentLayoutTweaks {
  type W = EditHourMomentLayout

  def ehmPopulate(
      timeSlot: MomentTimeSlot,
      position: Int,
      onRemoveHour: (Int) => Unit,
      onChangeFromHour: (Int, String) => Unit,
      onChangeToHour: (Int, String) => Unit,
      onSwapDays: (Int, Int) => Unit)(implicit theme: NineCardsTheme) =
    Tweak[W](_.populate(
      timeSlot,
      position,
      onRemoveHour,
      onChangeFromHour,
      onChangeToHour,
      onSwapDays).run)

}

object EditWifiMomentLayoutTweaks {
  type W = EditWifiMomentLayout

  def ewmPopulate(wifi: String, position: Int, onRemoveWifi: (Int) => Unit)(
      implicit theme: NineCardsTheme) =
    Tweak[W](_.populate(wifi, position, onRemoveWifi).run)

}
