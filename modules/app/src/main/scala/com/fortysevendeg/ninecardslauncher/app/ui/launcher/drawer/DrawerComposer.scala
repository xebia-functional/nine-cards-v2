package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.util.Log
import android.view.View
import android.widget.{FrameLayout, ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
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
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{PrimaryColor, NineCardsTheme}
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

  val openedField = "opened"

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = Option(findView(TR.launcher_drawer_pagination_panel))

  var recycler = slot[DrawerRecyclerView]

  var tabs = slot[LinearLayout]

  var pullToTabsView = slot[PullToTabsView]

  var screenAnimation = slot[FrameLayout]

  lazy val searchBoxContentPanel = Option(findView(TR.launcher_search_box_content_panel))

  var searchBoxView: Option[SearchBoxesAnimatedView] = None

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

  override def onChangeBoxView(boxView: BoxView)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Unit =
    runUi(
      (searchBoxView <~ sbavClean) ~
        closeCursorAdapter ~
        (boxView match {
          case AppsView => loadAppsAlphabetical
          case ContactView => loadContactsAlphabetical
        }))

  override def onHeaderIconClick(implicit context: ActivityContextWrapper): Unit =
    runUi(if (isTabsOpened) closeTabs else openTabs)

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    addWidgetsDrawer ~ transformDrawerUi

  protected def openTabs(implicit context: ActivityContextWrapper): Ui[_] =
    (tabs <~ vAddField(openedField, true) <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeTabs(implicit context: ActivityContextWrapper): Ui[_] =
    (tabs <~ vAddField(openedField, false) <~ hideTabs) ~
      (recycler <~ showList)

  private[this] def closeCursorAdapter: Ui[_] =
    Ui(
      recycler foreach { _.getAdapter match {
        case a: AppsAdapter => a.close()
        case a: ContactsAdapter => a.close()
        case _ =>
      }})

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(option)) map (_.drawable)
    Ui(loadApps(option)) ~
      (searchBoxView <~ (maybeDrawable map sbavUpdateAppsIcon getOrElse Tweak.blank)) ~
      (recycler <~ vSetType(option.name))
  }

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(option)) map (_.drawable)
    Ui(loadContacts(option)) ~
      (searchBoxView <~ (maybeDrawable map sbavUpdateContactsIcon getOrElse Tweak.blank)) ~
      (recycler <~ vSetType(option.name))
  }

  private[this] def loadAppsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(ContactsAlphabetical)) map (_.drawable)
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (recycler <~ vSetType(AppsAlphabetical.name)) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0)) ~
      (searchBoxView <~ (maybeDrawable map sbavUpdateContactsIcon getOrElse Tweak.blank))
  }

  private[this] def loadContactsAlphabetical(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(AppsAlphabetical)) map (_.drawable)
    loadContactsAndSaveStatus(ContactsAlphabetical) ~
      (recycler <~ vSetType(ContactsAlphabetical.name)) ~
      (paginationDrawerPanel <~ reloadPager(1)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, 0)) ~
      (searchBoxView <~ (maybeDrawable map sbavUpdateAppsIcon getOrElse Tweak.blank))
  }

  private[this] def addWidgetsDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (searchBoxContentPanel <~
      vgAddView(getUi(l[SearchBoxesAnimatedView]() <~ wire(searchBoxView) <~ sbavChangeListener(self)))) ~
      (scrollerLayout <~
        vgAddView(getUi(
          l[LinearLayout]() <~
            vAddField(openedField, false) <~
            tabContentStyles(resGetDimensionPixelSize(R.dimen.fastscroller_bar_width)) <~
            wire(tabs))) <~
        vgAddViewByIndex(getUi(
          l[PullToTabsView](
            w[DrawerRecyclerView] <~
              recyclerStyle <~
              vSetType(AppsAlphabetical.name) <~
              wire(recycler)
          ) <~ wire(pullToTabsView)), 0)) ~
      createDrawerPagers

  private[this] def transformDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val colorPrimary = theme.get(PrimaryColor)
    (searchBoxView <~
      sbavChangeListener(self) <~
      sbavOnChangeText((text: String, boxView: BoxView) => {
        (boxView, text, getStatus, getTypeView) match {
          case (AppsView, "", Some(status), Some(AppsView)) =>
            AppsMenuOption(status) foreach loadApps
          case (ContactView, "", Some(status), Some(ContactView)) =>
            ContactsMenuOption(status) foreach loadContacts
          case (AppsView, t, _, _) => loadAppsByKeyword(t)
          case (ContactView, t, _, _) => loadContactsByKeyword(t)
          case _ =>
        }
      })) ~
      (appDrawerMain <~ appDrawerMainStyle <~ On.click {
        (if (getItemsCount == 0) {
          loadAppsAlphabetical
        } else {
          Ui.nop
        }) ~ revealInDrawer ~~ (searchPanel <~ vGone)
      }) ~
      (recycler <~
        drvListener(DrawerRecyclerViewListener(
          start = startMovementAppsContacts,
          move = moveMovementAppsContacts,
          end = endMovementAppsContacts
        )) <~
        (searchBoxView map drvAddController getOrElse Tweak.blank)) ~
      (scrollerLayout <~
        scrollableStyle(colorPrimary)) ~
      (pullToTabsView <~
        ptvLinkTabs(
          tabs = tabs,
          start = recycler <~ drvEnabled(false),
          end = recycler <~ drvEnabled(true)) <~
        ptvAddTabsAndActivate(appTabs, 0) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            runUi(
              (getTypeView match {
                case Some(AppsView) =>
                  AppsMenuOption.list lift pos map loadAppsAndSaveStatus getOrElse Ui.nop
                case Some(ContactView) =>
                  ContactsMenuOption.list lift pos map loadContactsAndSaveStatus getOrElse Ui.nop
                case _ => Ui.nop
              }) ~ (if (isTabsOpened) closeTabs else Ui.nop))
          }
        ))) ~
      (drawerContent <~ contentStyle) ~
      loadAppsAlphabetical
  }

  private[this] def startMovementAppsContacts(): Ui[_] =
    (pullToTabsView <~ pdvEnable(false)) ~
      (screenAnimation <~
        vVisible <~
        (getTypeView map {
          case AppsView => vTranslationX(getDrawerWidth)
          case ContactView => vTranslationX(-getDrawerWidth)
        } getOrElse Tweak.blank))

  private[this] def moveMovementAppsContacts(displacement: Float): Ui[_] = {
    screenAnimation <~ vTranslationX(displacement)
  }

  private[this] def endMovementAppsContacts(): Ui[_] =
    (pullToTabsView <~ pdvEnable(true)) ~
      (screenAnimation <~ vGone) ~
      (recycler <~ vTranslationX(0))

  private[this] def getDrawerWidth: Int = drawerContent map (_.getWidth) getOrElse 0

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[Future[_]] =
    (searchBoxView <~ sbavReset) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source)))

  def revealOutDrawer(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val searchIsEmpty = searchBoxView exists (_.isEmpty)
    (searchPanel <~ vVisible) ~
      (searchBoxView <~ sbavClean) ~
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

  protected def isTabsOpened: Boolean = tabs exists (rv => rv.getField[Boolean](openedField) getOrElse false)

  private[this] def getStatus: Option[String] = recycler flatMap (rv => rv.getType)

  private[this] def getTypeView: Option[BoxView] = searchBoxView map (_.statuses.currentItem)

  private[this] def getItemsCount: Int = (for {
    rv <- recycler
    adapter <- Option(rv.getAdapter)
  } yield adapter.getItemCount) getOrElse 0

  def paginationDrawer(position: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = getUi(
    w[ImageView] <~ paginationDrawerItemStyle <~ vSetPosition(position)
  )

  private[this] def createDrawerPagers(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = {
    val pagerViews = 0 until pages map paginationDrawer
    paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  private[this] def resetData(searchIsEmpty: Boolean)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    if (searchIsEmpty && isShowingAppsAlphabetical) {
      (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
    } else {
      closeCursorAdapter ~ loadAppsAlphabetical
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
    signalType: FastScrollerSignalType = FastScrollerText) =
    (recycler <~
      rvLayoutManager(layoutManager) <~
      rvAdapter(adapter) <~
      rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[_] =
    recycler map { rv =>
      scrollerLayout <~ fslEnabledScroller(true) <~ fslLinkRecycler(rv) <~ fslReset <~ fslCounters(counters) <~ fslSignalType(signalType)
    } getOrElse showGeneralError

}
