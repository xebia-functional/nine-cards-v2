package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.{ViewHolder, Adapter, LayoutManager}
import android.view.View
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.{ContactsAdapter, LastCallsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SearchBoxesAnimatedViewTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.DrawerRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.{DrawerRecyclerView, DrawerRecyclerViewListener}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherComposer
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider
  with SearchBoxAnimatedListener
  with PullToTabsViewStyles {

  self: AppCompatActivity with TypedFindView with SystemBarsTint with LauncherComposer with DrawerListeners =>

  val pages = 2

  val resistance = 2.4f

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = Option(findView(TR.launcher_drawer_pagination_panel))

  var recycler = slot[DrawerRecyclerView]

  var tabs = slot[LinearLayout]

  var pullToTabsView = slot[PullToTabsView]

  lazy val searchBoxContentPanel = Option(findView(TR.launcher_search_box_content_panel))

  var searchBoxView: Option[SearchBoxesAnimatedView] = None

  def appTabs(implicit context: ActivityContextWrapper) = Seq(
    TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.apps_alphabetical)),
    TabInfo(R.drawable.app_drawer_filter_categories, resGetString(R.string.apps_categories)),
    TabInfo(R.drawable.app_drawer_filter_installation_date, resGetString(R.string.apps_date))
  )

  def contactsTabs(implicit context: ActivityContextWrapper) = Seq(
    TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.contacts_alphabetical)),
    TabInfo(R.drawable.app_drawer_filter_favorites, resGetString(R.string.contacts_favorites)),
    TabInfo(R.drawable.app_drawer_filter_last_call, resGetString(R.string.contacts_last))
  )

  override def onChangeBoxView(boxView: BoxView)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Unit =
    runUi(
      closeCursorAdapter ~ (boxView match {
        case AppsView => loadAppsAlphabetical
        case ContactView => loadContactsAlphabetical
      }))

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    addWidgetsDrawer ~ transformDrawerUi

  private[this] def closeCursorAdapter: Ui[_] =
    Ui(
      recycler foreach { _.getAdapter match {
        case a: AppsAdapter => a.close()
        case a: ContactsAdapter => a.close()
        case _ =>
      }})

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[_] =
    Ui(loadApps(option)) ~ (recycler <~ vSetType(option.toString))

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[_] =
    Ui(loadContacts(option)) ~ (recycler <~ vSetType(option.toString))

  private[this] def loadAppsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (recycler <~ vSetType(AppsAlphabetical.toString)) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0))

  private[this] def loadContactsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    loadContactsAndSaveStatus(ContactsAlphabetical) ~
      (recycler <~ vSetType(ContactsAlphabetical.toString)) ~
      (paginationDrawerPanel <~ reloadPager(1)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, 0))

  private[this] def addWidgetsDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (searchBoxContentPanel <~
      vgAddView(getUi(l[SearchBoxesAnimatedView]() <~ wire(searchBoxView) <~ sbavChangeListener(self)))) ~
      (scrollerLayout <~
        vgAddView(getUi(
          l[LinearLayout]() <~
            tabContentStyles(resGetDimensionPixelSize(R.dimen.fastscroller_bar_width)) <~
            wire(tabs))) <~
        vgAddViewByIndex(getUi(
          l[PullToTabsView](
            w[DrawerRecyclerView] <~
              recyclerStyle <~
              vSetType(AppsAlphabetical.toString) <~
              wire(recycler)
          ) <~ wire(pullToTabsView)), 0)) ~
      createDrawerPagers

  private[this] def transformDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val colorPrimary = resGetColor(R.color.primary)
    (searchBoxView <~ sbavChangeListener(self)) ~
      (appDrawerMain <~ appDrawerMainStyle <~ On.click {
        (if (getItemsCount == 0) {
          Ui(loadApps(AppsAlphabetical))
        } else {
          Ui.nop
        }) ~ revealInDrawer ~~ (searchPanel <~ vGone)
      }) ~
      (recycler <~
        drvListener(DrawerRecyclerViewListener(
          start = () => pullToTabsView <~ pdvEnable(false),
          end = () => pullToTabsView <~ pdvEnable(true)
        )) <~
        (searchBoxView map drvAddController getOrElse Tweak.blank)) ~
      (scrollerLayout <~
        drawerContentStyle <~
        fslColor(colorPrimary)) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = tabs,
          start = recycler <~ drvEnabled(false),
          end = recycler <~ drvEnabled(true)) <~
        ptvAddTabsAndActivate(appTabs, 0) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) =>
            runUi((pos, getTypeView()) match {
              case (0, Some(AppsView)) => loadAppsAndSaveStatus(AppsAlphabetical)
              case (1, Some(AppsView)) => loadAppsAndSaveStatus(AppsByCategories)
              case (2, Some(AppsView)) => loadAppsAndSaveStatus(AppsByLastInstall)
              case (0, Some(ContactView)) => loadContactsAndSaveStatus(ContactsAlphabetical)
              case (1, Some(ContactView)) => loadContactsAndSaveStatus(ContactsFavorites)
              case (2, Some(ContactView)) => loadContactsAndSaveStatus(ContactsByLastCall)
              case _ => Ui.nop
            }
            )))) ~
      (drawerContent <~ vGone) ~
      Ui(loadApps(AppsAlphabetical))
  }

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[Future[_]] =
    (searchBoxView <~ sbavReset) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source)))

  def revealOutDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (searchPanel <~ vVisible) ~
      (appDrawerMain mapUiF (source => (drawerContent <~~ revealOutAppDrawer(source)) ~~ resetData))

  def addApps(apps: IterableApps, getAppOrder: GetAppOrder, clickListener: (App) => Unit, longClickListener: (App) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val appsAdapter = new AppsAdapter(
      apps = apps,
      clickListener = clickListener,
      longClickListener = Option(longClickListener))
    swipeAdapter(
      adapter = appsAdapter,
      layoutManager = appsAdapter.getLayoutManager,
      fastScrollerVisible = isScrollerLayoutVisible(getAppOrder))
  }

  private[this] def getTypeView(): Option[BoxView] = searchBoxView map (_.statuses.currentItem)

  private[this] def getItemsCount: Int = (for {
    rv <- recycler
    adapter <- Option(rv.getAdapter)
  } yield adapter.getItemCount) getOrElse 0

  def paginationDrawer(position: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = getUi(
    w[ImageView] <~ paginationDrawerItemStyle <~ vTag(position.toString)
  )

  private[this] def createDrawerPagers(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = {
    val pagerViews = 0 until pages map paginationDrawer
    paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  private[this] def resetData(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = if (isShowingAppsAlphabetical) {
    (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
  } else {
    loadAppsAlphabetical
  }

  private[this] def isShowingAppsAlphabetical = recycler exists (_.isType(AppsAlphabetical.toString))

  private[this] def isScrollerLayoutVisible(getAppOrder: GetAppOrder) = getAppOrder match {
    case v: GetByInstallDate => false
    case _ => true
  }

  private[this] def isScrollerLayoutVisible(filter: ContactsFilter) = filter match {
    case FavoriteContacts => false
    case _ => true
  }

  def addContacts(contacts: IterableContacts, filter: ContactsFilter, clickListener: (Contact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactAdapter = new ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = None)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      isScrollerLayoutVisible(filter))
  }

  def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactAdapter = new LastCallsAdapter(
      contacts = contacts,
      clickListener = clickListener)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      fastScrollerVisible = false)
  }

  private[this] def swipeAdapter(
    adapter: RecyclerView.Adapter[_],
    layoutManager: LayoutManager,
    fastScrollerVisible: Boolean
  ) =
    (recycler <~
      rvLayoutManager(layoutManager) <~
      rvAdapter(adapter) <~
      rvScrollToTop) ~
      scrollerLayoutUi(fastScrollerVisible)

  def scrollerLayoutUi(fastScrollerVisible: Boolean): Ui[_] = if (fastScrollerVisible) {
    recycler map { rv =>
      scrollerLayout <~ fslEnabledScroller(true) <~ fslLinkRecycler(rv) <~ fslReset
    } getOrElse showGeneralError
  } else {
    scrollerLayout <~ fslEnabledScroller(false)
  }

}
