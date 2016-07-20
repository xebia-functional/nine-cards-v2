package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks

import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.view.{MenuItem, View}
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, WorkSpaceType}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ContentView
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders.LauncherWorkSpaceCollectionsHolder
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.device.models.{DockApp, TermCounter}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

object LauncherWorkSpacesTweaks {
  type W = LauncherWorkSpaces

  def lwsInitialize(presenter: LauncherPresenter, theme: NineCardsTheme) = Tweak[W] { view =>
    view.presenter = Some(presenter)
    view.theme = Some(theme)
  }

  def lwsData(data: Seq[LauncherData], pageSelected: Int) = Tweak[W] { view =>
    view.init(data, pageSelected)
  }

  def lwsDataCollections(data: Seq[LauncherData], pageCollectionSelected: Option[Int]) = Tweak[W] { view =>
    view.data.headOption match {
      case Some(moment) =>
        val page = pageCollectionSelected map (_ + 1) getOrElse view.currentPage()
        view.init(moment +: data, page)
      case _ =>
    }
  }

  def lwsDataMoment(moment: LauncherData) = Tweak[W] { view =>
    val data = view.data.filter(_.workSpaceType == CollectionsWorkSpace)
    view.init(moment +: data, view.currentPage())
  }

  def lwsDataForceReloadMoment() = Tweak[W] { view =>
    view.init(newData = view.data, position = view.currentPage(), forcePopulatePosition = Some(0))
  }

  def lwsAddWidget(widgetView: View) = Tweak[W] (_.addWidget(widgetView))

  def lwsClean = Tweak[W] (_.clean())

  def lwsOpenMenu = Tweak[W] (_.openMenu())

  def lwsListener(listener: LauncherWorkSpacesListener) = Tweak[W] (_.workSpacesListener = listener)

  def lwsSelect(position: Int) = Tweak[W](_.selectPosition(position))

  def lwsCloseMenu = Tweak[W] (_.closeMenu().run)

  def lwsPrepareItemsScreenInReorder(position: Int) = Tweak[W] (_.prepareItemsScreenInReorder(position).run)

  def lwsDragAddItemDispatcher(action: Int, x: Float, y: Float) = Tweak[W] {
    _.getCurrentView match {
      case Some(holder: LauncherWorkSpaceCollectionsHolder) =>
        holder.dragAddItemController(action, x, y)
      case _ =>
    }
  }

  def lwsDragReorderCollectionDispatcher(action: Int, x: Float, y: Float) = Tweak[W] {
    _.getCurrentView match {
      case Some(holder: LauncherWorkSpaceCollectionsHolder) =>
        holder.dragReorderCollectionController(action, x, y)
      case _ =>
    }
  }

  def lwsCurrentPage() = Excerpt[W, Int] (_.currentPage())

  def lwsCountCollections() = Excerpt[W, Int] (_.getCountCollections)

  def lwsGetCollections() = Excerpt[W, Seq[Collection]] (_.getCollections)

  def lwsEmptyCollections() = Excerpt[W, Boolean] (_.isEmptyCollections)

  def lwsCanMoveToNextScreen() = Excerpt[W, Boolean] (_.nextScreen.isDefined)

  def lwsNextScreen() = Excerpt[W, Option[Int]] (_.nextScreen)

  def lwsPreviousScreen() = Excerpt[W, Option[Int]] (_.previousScreen)

  def lwsCanMoveToPreviousScreen() = Excerpt[W, Boolean] (_.previousScreen.isDefined)

  def lwsIsCollectionWorkspace(page: Int) = Excerpt[W, Boolean] (_.isCollectionWorkSpace(page))

  def lwsIsCollectionWorkspace = Excerpt[W, Boolean] (_.isCollectionWorkSpace)

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

  def awsListener(listener: AnimatedWorkSpacesListener) = Tweak[W] (_.listener = listener)

  def awsAddPageChangedObserver(observer: (Int => Unit)) = Tweak[W](_.addPageChangedObservers(observer))

  def awsCurrentWorkSpace() = Excerpt[W, Int] (_.statuses.currentItem)

  def awsCountWorkSpace() = Excerpt[W, Int] (_.getWorksSpacesCount)

}

object FabItemMenuTweaks {
  type W = FabItemMenu

  def fimBackgroundColor(color: Int) = Tweak[W](_.changeBackground(color).run)

  def fimPopulate(backgroundColor: Int, resourceId: Int, text: Int) = Tweak[W](_.populate(backgroundColor, resourceId, text).run)

}

object WorkSpaceItemMenuTweaks {
  type W = WorkspaceItemMenu

  def wimPopulate(backgroundColor: Int, resourceId: Int, text: Int) = Tweak[W](_.populate(backgroundColor, resourceId, text).run)

}

object WorkSpaceMomentMenuTweaks {
  type W = WorkSpaceMomentIcon

  def wmmPopulateCollection(collection: Collection) = Tweak[W](_.populateCollection(collection).run)

  def wmmPopulateCard(card: Card) = Tweak[W](_.populateCard(card).run)

}

object StepsWorkspacesTweaks {
  type W = StepsWorkspaces

  def swData(data: Seq[StepData]) = Tweak[W] (_.init(data))

}

object SearchBoxesViewTweaks {
  type W = SearchBoxView

  def sbvUpdateContentView(contentView: ContentView)(implicit theme: NineCardsTheme) =
    Tweak[W] (_.updateContentView(contentView).run)

  def sbvChangeListener(listener: SearchBoxAnimatedListener) = Tweak[W] (_.listener = Some(listener))

  def sbvUpdateHeaderIcon(resourceId: Int)(implicit theme: NineCardsTheme) =
    Tweak[W](_.updateHeaderIcon(resourceId).run)

  def sbvOnChangeText(onChangeText: (String) => Unit) = Tweak[W] (_.addTextChangedListener(onChangeText))

  def sbvShowKeyboard = Tweak[W] (_.showKeyboard.run)

  def sbvClean = Tweak[W] (_.clean.run)

  def sbvEnableSearch = Tweak[W] (_.enableSearch.run)

  def sbvDisableSearch = Tweak[W] (_.disableSearch.run)
}

object TabsViewTweaks {

  val openedField = "opened"

  def tvOpen = vAddField(openedField, true)

  def tvClose = vAddField(openedField, false)

  def isOpened = Excerpt[LinearLayout, Boolean] (_.getField[Boolean](openedField) getOrElse false)

}

object PullToTabsViewTweaks {

  def ptvAddTabsAndActivate(items: Seq[TabInfo], index: Int, colorPrimary: Option[Int])(implicit theme: NineCardsTheme) =
    Tweak[PullToTabsView](_.addTabs(items, colorPrimary, Some(index)))

  def ptvAddTabs(items: Seq[TabInfo], colorPrimary: Option[Int])(implicit theme: NineCardsTheme) = Tweak[PullToTabsView](_.addTabs(items, colorPrimary))

  def ptvLinkTabs(tabs: Option[LinearLayout], start: Ui[_], end: Ui[_]) = Tweak[PullToTabsView] { view =>
    view.linkTabsView(tabs, start, end).run
  }

  def ptvClearTabs() = Tweak[PullToTabsView](_.clear())

  def ptvActivate(item: Int) = Tweak[PullToTabsView](_.activateItem(item))

  def ptvListener(pullToTabsListener: PullToTabsListener) =
    Tweak[PullToTabsView] (_.tabsListener = pullToTabsListener)

}

object PullToCloseViewTweaks {

  def pcvListener(pullToCloseListener: PullToCloseListener) =
    Tweak[PullToCloseView] (_.closeListeners = pullToCloseListener)

}

object PullToDownViewTweaks {

  def pdvPullingListener(pullToDownListener: PullingListener) =
    Tweak[PullToDownView] (_.pullingListeners = pullToDownListener)

  def pdvHorizontalListener(horizontalMovementListener: HorizontalMovementListener) =
    Tweak[PullToDownView] (_.horizontalListener = horizontalMovementListener)

  def pdvEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(enabled = enabled)
    }

  def pdvHorizontalEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(scrollHorizontalEnabled = enabled)
    }

  def pdvResistance(resistance: Float) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(resistance = resistance)
    }

  def pdvIsPulling() = Excerpt[PullToDownView, Boolean] (_.pullToDownStatuses.action == Pulling)

}

object FastScrollerLayoutTweak {
  // We should launch this tweak when the adapter has been added
  def fslLinkRecycler(recyclerView: RecyclerView) = Tweak[FastScrollerLayout](_.linkRecycler(recyclerView))

  def fslColor(color: Int) = Tweak[FastScrollerLayout](_.setColor(color))

  def fslMarginRightBarContent(pixels: Int) = Tweak[FastScrollerLayout](_.setMarginRightBarContent(pixels))

  def fslEnabledScroller(enabled: Boolean) = Tweak[FastScrollerLayout](_.setEnabledScroller(enabled))

  def fslReset = Tweak[FastScrollerLayout](_.reset)

  def fslCounters(counters: Seq[TermCounter]) = Tweak[FastScrollerLayout](_.setCounters(counters))

  def fslSignalType(signalType: FastScrollerSignalType) = Tweak[FastScrollerLayout](_.setSignalType(signalType))

}

object SlidingTabLayoutTweaks {
  type W = SlidingTabLayout

  def stlViewPager(viewPager: Option[ViewPager]): Tweak[W] = Tweak[W](viewPager foreach _.setViewPager)

  def stlDefaultTextColor(color: Int): Tweak[W] = Tweak[W](_.setDefaultTextColor(color))

  def stlSelectedTextColor(color: Int): Tweak[W] = Tweak[W](_.setSelectedTextColor(color))

  def stlTabStripColor(color: Int): Tweak[W] = Tweak[W](_.setTabStripColor(color))

  def stlOnPageChangeListener(listener: ViewPager.OnPageChangeListener): Tweak[W] = Tweak[W](_.setOnPageChangeListener(listener))
}

object DialogToolbarTweaks {

  type W = DialogToolbar

  def dtbInit(color: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] (_.init(color).run)

  def dtbExtended(implicit contextWrapper: ContextWrapper) = Tweak[W] {
    _.changeToolbarHeight(resGetDimensionPixelSize(R.dimen.height_extended_toolbar_dialog)).run
  }

  def dtbAddExtendedView(viewToAdd: View)(implicit contextWrapper: ContextWrapper) = Tweak[W] {
    _.addExtendedView(viewToAdd).run
  }

  def dtbChangeText(resourceId: Int) = Tweak[W] (_.changeText(resourceId).run)

  def dtbChangeText(text: String) = Tweak[W] (_.changeText(text).run)

  def dtbNavigationOnClickListener(click: (View) => Ui[_]) = Tweak[W] (_.navigationClickListener(click).run)

  def dtvInflateMenu(res: Int) = Tweak[W](_.toolbar foreach(_.inflateMenu(res)))

  def dtvOnMenuItemClickListener(onItem: (Int) => Boolean) = Tweak[W]{ view =>
    view.toolbar foreach(_.setOnMenuItemClickListener(new OnMenuItemClickListener {
      override def onMenuItemClick(menuItem: MenuItem): Boolean = onItem(menuItem.getItemId)
    }))
  }

}

object SwipeAnimatedDrawerViewTweaks {

  type W = SwipeAnimatedDrawerView

  def sadvInitAnimation(contentView: ContentView, widthContainer: Int)(implicit theme: NineCardsTheme) = Tweak[W] { view =>
    view.initAnimation(contentView, widthContainer).run
  }

  def sadvMoveAnimation(contentView: ContentView, widthContainer: Int, displacement: Float) = Tweak[W] { view =>
    view.moveAnimation(contentView, widthContainer, displacement).run
  }

  def sadvEndAnimation(duration: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    view.endAnimation(duration).run
  }

}

object DockAppsPanelLayoutTweaks {
  type W = DockAppsPanelLayout

  def daplInit(dockApps: Seq[DockApp])(implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.init(dockApps).run)

  def daplDragDispatcher(action: Int, x: Float, y: Float)(implicit presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper) = Tweak[W] (_.dragAddItemController(action, x, y))

  def daplReload(dockApp: DockApp)(implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.reload(dockApp).run)

}

object CollectionActionsPanelLayoutTweaks {
  type W = CollectionActionsPanelLayout

  def caplLoad(actions: Seq[CollectionActionItem])(implicit theme: NineCardsTheme, presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.load(actions).run)

  def caplDragDispatcher(action: Int, x: Float, y: Float)(implicit presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.dragController(action, x, y))

}

object TopBarLayoutTweaks {

  type W = TopBarLayout

  def tblInit(implicit theme: NineCardsTheme, presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.init.run)

  def tblReloadMoment(collection: Collection)(implicit theme: NineCardsTheme, presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper) =
    Tweak[W] (_.reloadMoment(collection).run)

  def tblReloadByType(workSpaceType: WorkSpaceType)(implicit contextWrapper: ContextWrapper) =
    Tweak[W] (_.reloadByType(workSpaceType).run)

}

object AppsMomentLayoutTweaks {

  type W = AppsMomentLayout

  def amlPopulate(moment: LauncherMoment)(implicit theme: NineCardsTheme, presenter: LauncherPresenter) =
    Tweak[W] (_.populate(moment).run)

}