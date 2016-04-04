package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.{ContactsAdapter, LastCallsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.SelectedItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SearchBoxesAnimatedViewTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SwipeAnimatedDrawerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.DrawerRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherPresenter, LauncherComposer}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider
  with LauncherExecutor
  with PullToTabsViewStyles {

  self: AppCompatActivity with TypedFindView with SystemBarsTint with LauncherComposer =>

  val pages = 2

  val resistance = 2.4f

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = Option(findView(TR.launcher_drawer_pagination_panel))

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  lazy val tabs = Option(findView(TR.launcher_drawer_tabs))

  lazy val pullToTabsView = Option(findView(TR.launcher_drawer_pull_to_tabs))

  lazy val screenAnimation = Option(findView(TR.launcher_drawer_swipe_animated))

  lazy val searchBoxView = Option(findView(TR.launcher_search_box_content))

  lazy val appTabs = AppsMenuOption.list map {
    case AppsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.apps_alphabetical))
    case AppsByCategories => TabInfo(R.drawable.app_drawer_filter_categories, getString(R.string.apps_categories))
    case AppsByLastInstall => TabInfo(R.drawable.app_drawer_filter_installation_date, getString(R.string.apps_date))
  }

  lazy val contactsTabs = ContactsMenuOption.list map {
    case ContactsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, getString(R.string.contacts_alphabetical))
    case ContactsFavorites => TabInfo(R.drawable.app_drawer_filter_favorites, getString(R.string.contacts_favorites))
    case ContactsByLastCall => TabInfo(R.drawable.app_drawer_filter_last_call, getString(R.string.contacts_last))
  }

  def showGeneralError: Ui[_] = drawerContent <~ vSnackbarShort(R.string.contactUsError)

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val colorPrimary = theme.get(PrimaryColor)
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    (searchBoxView <~
      sbvUpdateContentView(AppsView) <~
      sbvChangeListener(SearchBoxAnimatedListener(
        onHeaderIconClick = () => (if (isTabsOpened) closeTabs else openTabs).run,
        onAppStoreIconClick = () => launchPlayStore,
        onContactsIconClick = () => launchDial()
      )) <~
      sbvOnChangeText((text: String) => {
        (text, getStatus, getTypeView) match {
          case ("", Some(status), Some(AppsView)) =>
            AppsMenuOption(status) foreach (option => presenter.loadApps(option).run)
          case ("", Some(status), Some(ContactView)) =>
            ContactsMenuOption(status) foreach (option => presenter.loadContacts(option).run)
          case (t, _, Some(AppsView)) => presenter.loadAppsByKeyword(t).run
          case (t, _, Some(ContactView)) => presenter.loadContactsByKeyword(t).run
          case _ =>
        }
      })) ~
      (tabs <~ tvClose) ~
      (appDrawerMain <~ appDrawerMainStyle <~ On.click {
        (if (getItemsCount == 0) {
          loadAppsAlphabetical
        } else {
          Ui.nop
        }) ~ revealInDrawer ~~ (searchPanel <~ vGone)
      }) ~
      (recycler <~
        recyclerStyle <~
        drvListener(DrawerRecyclerViewListener(
          start = startMovementAppsContacts,
          move = moveMovementAppsContacts,
          end = endMovementAppsContacts,
          changeContentView = changeContentView
        )) <~
        rvAddItemDecoration(new SelectedItemDecoration)) ~
      (scrollerLayout <~
        fslMarginRightBarContent(padding) <~
        scrollableStyle(colorPrimary)) ~
      (pullToTabsView <~
        pdvHorizontalEnable(true) <~
        (recycler map (rv => pdvHorizontalListener(rv.horizontalMovementListener)) getOrElse Tweak.blank) <~
        ptvLinkTabs(
          tabs = tabs,
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(appTabs, 0, None) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            ((getTypeView match {
              case Some(AppsView) =>
                AppsMenuOption.list lift pos map loadAppsAndSaveStatus getOrElse Ui.nop
              case Some(ContactView) =>
                ContactsMenuOption.list lift pos map loadContactsAndSaveStatus getOrElse Ui.nop
              case _ => Ui.nop
            }) ~ (if (isTabsOpened) closeTabs else Ui.nop) ~ (searchBoxView <~ sbvClean)).run
          }
        ))) ~
      (drawerContent <~ contentStyle) ~
      loadAppsAlphabetical ~
      createDrawerPagers
  }

  protected def openTabs(implicit context: ActivityContextWrapper): Ui[_] =
    (tabs <~ tvOpen <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeTabs(implicit context: ActivityContextWrapper): Ui[_] =
    (tabs <~ tvClose <~ hideTabs) ~
      (recycler <~ showList)

  private[this] def closeCursorAdapter: Ui[_] =
    Ui(
      recycler foreach { _.getAdapter match {
        case a: AppsAdapter => a.close()
        case a: ContactsAdapter => a.close()
        case _ =>
      }})

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption)(implicit theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(option)) map (_.drawable)
    presenter.loadApps(option) ~
      (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption)(implicit theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(option)) map (_.drawable)
    presenter.loadContacts(option) ~
      (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  private[this] def loadAppsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(ContactsAlphabetical)) map (_.drawable)
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (recycler <~ drvSetType(AppsAlphabetical)) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0, None)) ~
      (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def loadContactsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(AppsAlphabetical)) map (_.drawable)
    loadContactsAndSaveStatus(ContactsAlphabetical) ~
      (recycler <~ drvSetType(ContactsAlphabetical)) ~
      (paginationDrawerPanel <~ reloadPager(1)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, 0, None)) ~
      (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def startMovementAppsContacts()(implicit theme: NineCardsTheme): Ui[_] =
    (pullToTabsView <~ pdvEnable(false)) ~
      (screenAnimation <~
        (getTypeView map (cv => sadvInitAnimation(cv, getDrawerWidth)) getOrElse Tweak.blank))

  private[this] def moveMovementAppsContacts(displacement: Float): Ui[_] =
    screenAnimation <~
      (getTypeView map (cv => sadvMoveAnimation(cv, getDrawerWidth, displacement)) getOrElse Tweak.blank)

  private[this] def endMovementAppsContacts(duration: Int)(implicit contextWrapper: ContextWrapper): Ui[_] =
    (pullToTabsView <~ pdvEnable(true)) ~
      (screenAnimation <~ sadvEndAnimation(duration))

  private[this] def changeContentView(contentView: ContentView)
    (implicit activityContextWrapper: ActivityContextWrapper, nineCardsTheme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] =
    (searchBoxView <~ sbvUpdateContentView(contentView) <~ sbvClean) ~
      closeCursorAdapter ~
      (contentView match {
        case AppsView => loadAppsAlphabetical
        case ContactView => loadContactsAlphabetical
      })

  private[this] def getDrawerWidth: Int = drawerContent map (dc => dc.getWidth) getOrElse 0

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[Future[_]] =
    (paginationDrawerPanel <~ reloadPager(0)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source)))

  def revealOutDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter): Ui[_] = {
    val searchIsEmpty = searchBoxView exists (_.isEmpty)
    (searchPanel <~ vVisible) ~
      (searchBoxView <~ sbvClean) ~
      (appDrawerMain mapUiF (source => (drawerContent <~~ revealOutAppDrawer(source)) ~~ resetData(searchIsEmpty)))
  }

  def addApps(
    apps: IterableApps,
    clickListener: (App) => Unit,
    longClickListener: (App) => Unit,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val appsAdapter = new AppsAdapter(
      apps = apps,
      clickListener = clickListener,
      longClickListener = Option(longClickListener))
    swipeAdapter(
      adapter = appsAdapter,
      layoutManager = appsAdapter.getLayoutManager,
      counters = counters,
      signalType = getAppOrder match {
        case GetByInstallDate => FastScrollerInstallationDate
        case GetByCategory => FastScrollerCategory
        case _ => FastScrollerText
      })
  }

  protected def isTabsOpened: Boolean = (tabs ~> isOpened).get getOrElse false

  private[this] def getStatus: Option[String] = recycler flatMap (rv => rv.getType)

  private[this] def getTypeView: Option[ContentView] = recycler map (_.statuses.contentView)

  private[this] def getItemsCount: Int = (for {
    rv <- recycler
    adapter <- Option(rv.getAdapter)
  } yield adapter.getItemCount) getOrElse 0

  def paginationDrawer(position: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    (w[ImageView] <~ paginationDrawerItemStyle <~ vSetPosition(position)).get

  private[this] def createDrawerPagers(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = {
    val pagerViews = 0 until pages map paginationDrawer
    paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  private[this] def resetData(searchIsEmpty: Boolean)(implicit context: ActivityContextWrapper, theme: NineCardsTheme, presenter: LauncherPresenter) =
    if (searchIsEmpty && isShowingAppsAlphabetical) {
      (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
    } else {
      closeCursorAdapter ~ loadAppsAlphabetical ~ (searchBoxView <~ sbvUpdateContentView(AppsView))
    }

  private[this] def isShowingAppsAlphabetical = recycler exists (_.isType(AppsAlphabetical.name))

  def addContacts(
    contacts: IterableContacts,
    clickListener: (Contact) => Unit,
    counters: Seq[TermCounter] = Seq.empty)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactAdapter = new ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = None)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      counters)
  }

  def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val contactAdapter = new LastCallsAdapter(
      contacts = contacts,
      clickListener = clickListener)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      Seq.empty)
  }

  private[this] def swipeAdapter(
    adapter: RecyclerView.Adapter[_],
    layoutManager: LayoutManager,
    counters: Seq[TermCounter],
    signalType: FastScrollerSignalType = FastScrollerText)(implicit contextWrapper: ContextWrapper) = {
    val searchIsEmpty = searchBoxView exists (_.isEmpty)
    val animationTweaks = getTypeView map {
      case AppsView =>
        (if (searchIsEmpty) rvLayoutAnimation(R.anim.appdrawer_apps_layout_animation) else Tweak.blank) +
          vAddField(SelectedItemDecoration.showLine, true)
      case ContactView =>
        (if (searchIsEmpty) rvLayoutAnimation(R.anim.appdrawer_contacts_layout_animation) else Tweak.blank) +
          vAddField(SelectedItemDecoration.showLine, false)
    } getOrElse Tweak.blank
    (recycler <~
      rvLayoutManager(layoutManager) <~
      animationTweaks <~
      rvAdapter(adapter) <~
      rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)
  }

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[_] =
    recycler map { rv =>
      scrollerLayout <~ fslEnabledScroller(true) <~ fslLinkRecycler(rv) <~ fslReset <~ fslCounters(counters) <~ fslSignalType(signalType)
    } getOrElse showGeneralError

}
