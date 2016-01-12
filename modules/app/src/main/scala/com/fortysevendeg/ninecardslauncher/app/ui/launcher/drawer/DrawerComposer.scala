package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.View
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.ContactsAdapter
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
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact, IterableApps, IterableContacts}
import com.fortysevendeg.ninecardslauncher.process.device.{GetAppOrder, GetByInstallDate}
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

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = Option(findView(TR.launcher_drawer_pagination_panel))

  var recycler = slot[DrawerRecyclerView]

  var tabs = slot[LinearLayout]

  var pullToTabsView = slot[PullToTabsView]

  lazy val searchBoxContentPanel = Option(findView(TR.launcher_search_box_content_panel))

  var searchBoxView: Option[SearchBoxesAnimatedView] = None

  var isShowingAppsAlphabetical = true

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
    boxView match {
      case AppsView =>
        isShowingAppsAlphabetical = true
        runUi(
          Ui(loadApps(AppsAlphabetical)) ~
            (paginationDrawerPanel <~ reloadPager(0)) ~
            (pullToTabsView <~
              ptvClearTabs() <~
              ptvAddTabsAndActivate(appTabs, 0)))
      case ContactView =>
        isShowingAppsAlphabetical = false
        runUi(
          Ui(loadContacts(ContactsAlphabetical)) ~
            (paginationDrawerPanel <~ reloadPager(1)) ~
            (pullToTabsView <~
              ptvClearTabs() <~
              ptvAddTabsAndActivate(contactsTabs, 0)))
    }

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val colorPrimary = resGetColor(R.color.primary)
    (searchBoxContentPanel <~
      vgAddView(getUi(l[SearchBoxesAnimatedView]() <~ wire(searchBoxView) <~ sbavChangeListener(self)))) ~
      (appDrawerMain <~ appDrawerMainStyle <~ On.click {
        (if (getItemsCount == 0) {
          Ui(loadApps(AppsAlphabetical))
        } else {
          Ui.nop
        }) ~ revealInDrawer ~~ (searchPanel <~ vGone)
      }) ~
      (scrollerLayout <~
        drawerContentStyle <~
        vgAddView(getUi(l[LinearLayout]() <~ tabContentStyles <~ wire(tabs))) <~
        vgAddViewByIndex(getUi(
          l[PullToTabsView](
            w[DrawerRecyclerView] <~
              recyclerStyle <~
              wire(recycler) <~
              drvListener(DrawerRecyclerViewListener(
                start = () => pullToTabsView <~ pdvEnable(false),
                end = () => pullToTabsView <~ pdvEnable(true)
              )) <~
              (searchBoxView map drvAddController getOrElse Tweak.blank)
          ) <~ wire(pullToTabsView)), 0) <~
        fslColor(colorPrimary)) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = tabs,
          start = recycler <~ drvEnabled(false),
          end = recycler <~ drvEnabled(true)) <~
        ptvAddTabsAndActivate(appTabs, 0) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => (pos, getTypeView()) match {
            case (0, Some(AppsView)) => loadApps(AppsAlphabetical)
            case (1, Some(AppsView)) => loadApps(AppsByCategories)
            case (2, Some(AppsView)) => loadApps(AppsByLastInstall)
            case (0, Some(ContactView)) => loadContacts(ContactsAlphabetical)
            case (1, Some(ContactView)) => loadContacts(ContactsFavorites)
            case (2, Some(ContactView)) => loadContacts(ContactsByLastCall)
            case _ =>
          }
        ))) ~
      (drawerContent <~ vGone) ~
      Ui(loadApps(AppsAlphabetical)) ~
      createDrawerPagers
  }

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[Future[_]] =
    (searchBoxView <~ sbavReset) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source)))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
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

  private[this] def resetData() = if (isShowingAppsAlphabetical) {
    (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
  } else {
    isShowingAppsAlphabetical = true
    Ui(loadApps(AppsAlphabetical))
  }

  private[this] def isScrollerLayoutVisible(getAppOrder: GetAppOrder) = getAppOrder match {
    case v: GetByInstallDate => false
    case _ => true
  }

  def addContacts(contacts: IterableContacts, clickListener: (Contact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactAdapter = new ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = None)
    swipeAdapter(contactAdapter, contactAdapter.getLayoutManager)
  }

  private[this] def swipeAdapter(
    adapter: RecyclerView.Adapter[_],
    layoutManager: LayoutManager,
    fastScrollerVisible: Boolean = true
  ) =
    (recycler <~
      rvLayoutManager(layoutManager) <~
      rvAdapter(adapter) <~
      rvScrollToTop) ~
      scrollerLayoutUi(fastScrollerVisible)

  def scrollerLayoutUi(fastScrollerVisible: Boolean): Ui[_] = if (fastScrollerVisible) {
    recycler map { rv =>
      scrollerLayout <~ fslVisible <~ fslLinkRecycler(rv) <~ fslReset
    } getOrElse showGeneralError
  } else {
    scrollerLayout <~ fslInvisible
  }

}
