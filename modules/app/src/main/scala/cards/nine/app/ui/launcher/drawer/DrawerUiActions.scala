package cards.nine.app.ui.launcher.drawer

import java.io.Closeable

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.View
import android.widget.ImageView
import cards.nine.app.commons._
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.adapters.apps.AppsAdapter
import cards.nine.app.ui.commons.adapters.contacts.{ContactsAdapter, LastCallsAdapter}
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.commons.SelectedItemDecoration
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.layouts.snails.TabsSnails._
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.SearchBoxesViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.SwipeAnimatedDrawerViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.TabsViewTweaks._
import cards.nine.app.ui.components.widgets._
import cards.nine.app.ui.components.widgets.tweaks.DrawerRecyclerViewTweaks._
import cards.nine.app.ui.launcher.drawer.DrawerSnails._
import cards.nine.app.ui.launcher.{LauncherUiActions, LauncherUiActionsImpl}
import cards.nine.app.ui.preferences.commons._
import cards.nine.models.types.{GetByCategory, GetByInstallDate, GetByName, GetAppOrder}
import cards.nine.models.{TermCounter, ApplicationData, Contact}
import cards.nine.process.device.models._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

trait DrawerUiActions
  extends LauncherUiActions
  with DrawerStyles
  with ContextSupportProvider
  with PullToTabsViewStyles {

  self: TypedFindView with Contexts[AppCompatActivity] with LauncherUiActionsImpl =>

  val pages = 2

  val resistance = 2.4f

  lazy val appDrawerMain = findView(TR.launcher_app_drawer)

  lazy val drawerContent = findView(TR.launcher_drawer_content)

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = findView(TR.launcher_drawer_pagination_panel)

  lazy val recycler = findView(TR.launcher_drawer_recycler)

  lazy val tabs = findView(TR.launcher_drawer_tabs)

  lazy val pullToTabsView = findView(TR.launcher_drawer_pull_to_tabs)

  lazy val screenAnimation = findView(TR.launcher_drawer_swipe_animated)

  lazy val searchBoxView = findView(TR.launcher_search_box_content)

  lazy val appTabs = AppsMenuOption.list map {
    case AppsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.apps_alphabetical))
    case AppsByCategories => TabInfo(R.drawable.app_drawer_filter_categories, resGetString(R.string.apps_categories))
    case AppsByLastInstall => TabInfo(R.drawable.app_drawer_filter_installation_date, resGetString(R.string.apps_date))
  }

  lazy val contactsTabs = ContactsMenuOption.list map {
    case ContactsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.contacts_alphabetical))
    case ContactsFavorites => TabInfo(R.drawable.app_drawer_filter_favorites, resGetString(R.string.contacts_favorites))
    case ContactsByLastCall => TabInfo(R.drawable.app_drawer_filter_last_call, resGetString(R.string.contacts_last))
  }

  protected def initDrawerUi: Ui[_] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue(preferenceValues)
    (searchBoxView <~
      sbvUpdateContentView(AppsView) <~
      sbvChangeListener(SearchBoxAnimatedListener(
        onHeaderIconClick = () => {
          (((pullToTabsView ~> pdvIsEnabled()).get, isDrawerTabsOpened) match {
            case (false, _) => Ui.nop
            case (true, true) => closeDrawerTabs
            case (true, false) => openTabs
          }).run
        },
        onAppStoreIconClick = () => presenter.launchPlayStore(),
        onContactsIconClick = () => presenter.launchDial()
      )) <~
      sbvOnChangeText((text: String) => {
        (text, getStatus, getTypeView) match {
          case ("", Some(status), Some(AppsView)) =>
            AppsMenuOption(status) foreach (option => presenter.loadApps(option))
          case ("", Some(status), Some(ContactView)) =>
            ContactsMenuOption(status) foreach (option => presenter.loadContacts(option))
          case (t, _, Some(AppsView)) => presenter.loadAppsByKeyword(t)
          case (t, _, Some(ContactView)) => presenter.loadContactsByKeyword(t)
          case _ =>
        }
      })) ~
      (tabs <~ tvClose) ~
      (appDrawerMain <~
        appDrawerMainStyle <~
        On.click (openDrawer(longClick = false)) <~
        On.longClick (openDrawer(longClick = true) ~ Ui(true))) ~
      (recycler <~
        recyclerStyle <~
        drvListener(DrawerRecyclerViewListener(
          start = startMovementAppsContacts,
          move = moveMovementAppsContacts,
          end = endMovementAppsContacts,
          changeContentView = changeContentView
        )) <~
        (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (scrollerLayout <~ scrollableStyle) ~
      (pullToTabsView <~
        pdvHorizontalEnable(true) <~
        pdvHorizontalListener(recycler.horizontalMovementListener) <~
        ptvLinkTabs(
          tabs = Some(tabs),
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
            }) ~ (if (isDrawerTabsOpened) closeDrawerTabs else Ui.nop) ~ (searchBoxView <~ sbvClean)).run
          }
        ))) ~
      (drawerContent <~ contentStyle) ~
      loadAppsAlphabetical ~
      createDrawerPagers
  }

  private[this] def openDrawer(longClick: Boolean) = {

    def revealInDrawer(longClick: Boolean): Ui[Future[_]] = {
      val showKeyboard = AppDrawerLongPressAction.readValue(preferenceValues) == AppDrawerLongPressActionOpenKeyboard && longClick
      (drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
        (paginationDrawerPanel <~ reloadPager(0)) ~
        ((drawerContent <~~
          openAppDrawer(AppDrawerAnimation.readValue(preferenceValues), appDrawerMain)) ~~
          (searchBoxView <~
            sbvEnableSearch <~
            (if (showKeyboard) sbvShowKeyboard else Tweak.blank)))
    }

    val loadContacts = AppDrawerLongPressAction.readValue(preferenceValues) == AppDrawerLongPressActionOpenContacts && longClick
    (if (loadContacts) {
      Ui(
        recycler.getAdapter match {
          case a: AppsAdapter => a.clear()
          case _ =>
        }) ~ loadContactsAlphabetical
    } else if (getItemsCount == 0) {
      loadAppsAlphabetical
    } else {
      Ui.nop
    }) ~ revealInDrawer(longClick) ~~ (topBarPanel <~ vGone)
  }

  protected def openTabs: Ui[_] =
    (tabs <~ tvOpen <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeDrawerTabs: Ui[_] =
    (tabs <~ tvClose <~ hideTabs) ~
      (recycler <~ showList)

  private[this] def closeCursorAdapter: Ui[_] = {

    def safeClose(closeable: Closeable): Unit = Try(closeable.close()) match {
      case Failure(ex) => printErrorMessage(ex)
      case _ =>
    }

    Ui {
      recycler.getAdapter match {
        case a: Closeable => safeClose(a)
        case _ =>
      }
    }
  }

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(option)) map (_.drawable)
    presenter.loadApps(option)
    (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  protected def reloadContacts: Ui[_] = {
    val option = getStatus match {
      case Some(status) => ContactsMenuOption(status)
      case _ => None
    }
    loadContactsAndSaveStatus(option getOrElse ContactsAlphabetical)
  }

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(option)) map (_.drawable)
    presenter.loadContacts(option)
    (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  protected def loadAppsAlphabetical: Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(ContactsAlphabetical)) map (_.drawable)
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0, None)) ~
      (searchBoxView <~ sbvUpdateContentView(AppsView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def loadContactsAlphabetical: Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(AppsAlphabetical)) map (_.drawable)
    val favoriteContactsFirst = AppDrawerFavoriteContactsFirst.readValue(preferenceValues)
    loadContactsAndSaveStatus(if (favoriteContactsFirst) ContactsFavorites else ContactsAlphabetical) ~
      (paginationDrawerPanel <~ reloadPager(1)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, if (favoriteContactsFirst) 1 else 0, None)) ~
      (searchBoxView <~ sbvUpdateContentView(ContactView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def startMovementAppsContacts(): Ui[_] =
    (pullToTabsView <~ pdvEnable(false)) ~
      (screenAnimation <~
        (getTypeView map (cv => sadvInitAnimation(cv, getDrawerWidth)) getOrElse Tweak.blank))

  private[this] def moveMovementAppsContacts(displacement: Float): Ui[_] =
    screenAnimation <~
      (getTypeView map (cv => sadvMoveAnimation(cv, getDrawerWidth, displacement)) getOrElse Tweak.blank)

  private[this] def endMovementAppsContacts(duration: Int): Ui[_] =
    (pullToTabsView <~ pdvEnable(true)) ~
      (screenAnimation <~ sadvEndAnimation(duration))

  private[this] def changeContentView(contentView: ContentView): Ui[_] =
    (searchBoxView <~ sbvClean) ~
      closeCursorAdapter ~
      (contentView match {
        case AppsView => loadAppsAlphabetical
        case ContactView => loadContactsAlphabetical
      })

  private[this] def getDrawerWidth: Int = drawerContent.getWidth

  protected def isDrawerVisible = drawerContent.getVisibility == View.VISIBLE

  protected def revealOutDrawer: Ui[_] = {

    def isShowingAppsAlphabetical = recycler.isType(AppsAlphabetical.name)

    def resetData(searchIsEmpty: Boolean) =
      if (searchIsEmpty && isShowingAppsAlphabetical) {
        (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
      } else {
        closeCursorAdapter ~ loadAppsAlphabetical ~ (searchBoxView <~ sbvUpdateContentView(AppsView))
      }

    val collectionMoment = getData.headOption flatMap (_.moment) flatMap (_.collection)
    val searchIsEmpty = searchBoxView.isEmpty
    (drawerLayout <~ dlUnlockedStart <~ (if (collectionMoment.isDefined) dlUnlockedEnd else Tweak.blank)) ~
      (topBarPanel <~ vVisible) ~
      (searchBoxView <~ sbvClean <~ sbvDisableSearch) ~
      ((drawerContent <~~ closeAppDrawer(AppDrawerAnimation.readValue(preferenceValues), appDrawerMain)) ~~ resetData(searchIsEmpty))
  }

  protected def addApps(
    apps: IterableApps,
    clickListener: (ApplicationData) => Unit,
    longClickListener: (View, ApplicationData) => Unit,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] = {
    val appsAdapter = AppsAdapter(
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

  protected def isDrawerTabsOpened: Boolean = (tabs ~> isOpened).get

  private[this] def getStatus: Option[String] = recycler.getType

  private[this] def getTypeView: Option[ContentView] = Option(recycler.statuses.contentView)

  private[this] def getItemsCount: Int =
    Option(recycler.getAdapter) map (_.getItemCount) getOrElse 0

  private[this] def createDrawerPagers = {

    def paginationDrawer(position: Int) =
      (w[ImageView] <~ paginationDrawerItemStyle <~ vSetPosition(position)).get

    val pagerViews = 0 until pages map paginationDrawer
    paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  protected def addContacts(
    contacts: IterableContacts,
    clickListener: (Contact) => Unit,
    longClickListener: (View, Contact) => Unit,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] = {
    val contactAdapter = ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = Some(longClickListener))
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      counters)
  }

  protected def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit): Ui[_] = {
    val contactAdapter = LastCallsAdapter(
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
    signalType: FastScrollerSignalType = FastScrollerText) = {
    val searchIsEmpty = searchBoxView.isEmpty
    val lastTimeContentViewWasChanged = recycler.statuses.lastTimeContentViewWasChanged
    val addFieldTweaks = getTypeView map {
      case AppsView => vAddField(SelectedItemDecoration.showLine, true)
      case ContactView => vAddField(SelectedItemDecoration.showLine, false)
    } getOrElse Tweak.blank
    closeCursorAdapter ~
      (pullToTabsView <~ pdvEnable(true)) ~
      (recycler <~
        vVisible <~
        rvLayoutManager(layoutManager) <~
        (if (searchIsEmpty && !lastTimeContentViewWasChanged) rvLayoutAnimation(R.anim.list_slide_in_bottom_animation) else Tweak.blank) <~
        addFieldTweaks <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)
  }

  protected def showBottomDrawerError(message: Int, action: () => Unit): Ui[Any] =
    drawerContent <~ vSnackbarLongAction(message, R.string.buttonTryAgain, action)

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[_] =
    scrollerLayout <~
      fslEnabledScroller(true) <~
      fslLinkRecycler(recycler) <~
      fslReset <~
      fslCounters(counters) <~
      fslSignalType(signalType)

}
