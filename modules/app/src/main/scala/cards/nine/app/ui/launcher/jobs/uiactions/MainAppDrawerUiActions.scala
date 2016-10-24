package cards.nine.app.ui.launcher.jobs.uiactions

import java.io.Closeable

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.{View, ViewGroup}
import android.widget.ImageView
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.adapters.apps.AppsAdapter
import cards.nine.app.ui.commons.adapters.contacts.{ContactsAdapter, LastCallsAdapter}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
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
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.{AppDrawerJobs, DragJobs, NavigationJobs}
import cards.nine.app.ui.launcher.snails.DrawerSnails._
import cards.nine.app.ui.launcher.types.{AppDrawerIconShadowBuilder, _}
import cards.nine.app.ui.preferences.commons._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{GetAppOrder, GetByCategory, GetByInstallDate, GetByName}
import cards.nine.models.{ApplicationData, Contact, LastCallsContact, TermCounter}
import cards.nine.process.device._
import cards.nine.process.device.models.{IterableApps, IterableContacts}
import cards.nine.process.theme.models._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

class MainAppDrawerUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  implicit def theme: NineCardsTheme = statuses.theme

  lazy val appDrawerJobs: AppDrawerJobs = createAppDrawerJobs

  lazy val navigationJobs: NavigationJobs = createNavigationJobs

  lazy val dragJobs: DragJobs = createDragJobs

  val pages = 2

  val resistance = 2.4f

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

  def initialize(): TaskService[Unit] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue
    ((dom.searchBoxView <~
      sbvUpdateContentView(AppsView) <~
      sbvChangeListener(SearchBoxAnimatedListener(
        onHeaderIconClick = () => {
          (((dom.pullToTabsView ~> pdvIsEnabled()).get, dom.isDrawerTabsOpened) match {
            case (false, _) => Ui.nop
            case (true, true) => closeDrawerTabs
            case (true, false) => openTabs
          }).run
        },
        onAppStoreIconClick = () => navigationJobs.launchPlayStore().resolveAsyncServiceOr(manageException),
        onContactsIconClick = () => navigationJobs.launchDial().resolveAsyncServiceOr(manageException)
      )) <~
      sbvOnChangeText((text: String) => {
        (text, dom.getStatus, dom.getTypeView) match {
          case ("", Some(status), Some(AppsView)) =>
            AppsMenuOption(status) foreach (option => appDrawerJobs.loadApps(option).resolveAsync())
          case ("", Some(status), Some(ContactView)) =>
            ContactsMenuOption(status) foreach (option => appDrawerJobs.loadContacts(option).resolveAsyncServiceOr(manageException))
          case (t, _, Some(AppsView)) => appDrawerJobs.loadAppsByKeyword(t).resolveAsync()
          case (t, _, Some(ContactView)) => appDrawerJobs.loadContactsByKeyword(t).resolveAsyncServiceOr(manageException)
          case _ =>
        }
      })) ~
      (dom.tabs <~ tvClose) ~
      (dom.appDrawerMain <~
        appDrawerMainStyle <~
        On.click (openDrawer(longClick = false)) <~
        On.longClick (openDrawer(longClick = true) ~ Ui(true))) ~
      (dom.recycler <~
        recyclerStyle <~
        drvListener(DrawerRecyclerViewListener(
          start = startMovementAppsContacts,
          move = moveMovementAppsContacts,
          end = endMovementAppsContacts,
          changeContentView = changeContentView
        )) <~
        (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (dom.scrollerLayout <~ scrollableStyle) ~
      (dom.pullToTabsView <~
        pdvHorizontalEnable(true) <~
        pdvHorizontalListener(dom.recycler.horizontalMovementListener) <~
        ptvLinkTabs(
          tabs = Some(dom.tabs),
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(appTabs, 0, None) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            ((dom.getTypeView match {
              case Some(AppsView) =>
                AppsMenuOption.list lift pos map loadAppsAndSaveStatus getOrElse Ui.nop
              case Some(ContactView) =>
                ContactsMenuOption.list lift pos map loadContactsAndSaveStatus getOrElse Ui.nop
              case _ => Ui.nop
            }) ~ (if (dom.isDrawerTabsOpened) closeDrawerTabs else Ui.nop) ~ (dom.searchBoxView <~ sbvClean)).run
          }
        ))) ~
      loadAppsAlphabetical ~
      createDrawerPagers).toService
  }

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): TaskService[Unit] =
    addApps(
      apps = apps,
      clickListener = (app: ApplicationData) => navigationJobs.openApp(app).resolveAsyncServiceOr(manageException),
      longClickListener = (view: View, app: ApplicationData) => {
        dragJobs.startAddItemToCollection(app).resolveAsync()
        (view <~ vStartDrag(AddItemToCollection, new AppDrawerIconShadowBuilder(view))).run
      },
      getAppOrder = getAppOrder,
      counters = counters).toService

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): TaskService[Unit] =
    addContacts(
      contacts = contacts,
      clickListener = (contact: Contact) => navigationJobs.openContact(contact).resolveAsyncServiceOr(manageException),
      longClickListener = (view: View, contact: Contact) => {
        dragJobs.startAddItemToCollection(contact).resolveAsync()
        (view <~ vStartDrag(AddItemToCollection, new AppDrawerIconShadowBuilder(view))).run
      },
      counters = counters).toService

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): TaskService[Unit] =
    addLastCallContacts(contacts, (contact: LastCallsContact) =>
      navigationJobs.openLastCall(contact.number).resolveAsyncServiceOr(manageException)).toService

  def closeTabs(): TaskService[Unit] = closeDrawerTabs.toService

  def close(): TaskService[Unit] = {

    def isShowingAppsAlphabetical = dom.recycler.isType(AppsAlphabetical.name)

    def resetData(searchIsEmpty: Boolean) =
      if (searchIsEmpty && isShowingAppsAlphabetical) {
        (dom.recycler <~ rvScrollToTop) ~ (dom.scrollerLayout <~ fslReset)
      } else {
        closeCursorAdapter ~ loadAppsAlphabetical ~ (dom.searchBoxView <~ sbvUpdateContentView(AppsView))
      }

    val collectionMoment = dom.getData.headOption flatMap (_.moment) flatMap (_.collection)
    val searchIsEmpty = dom.searchBoxView.isEmpty
    ((dom.drawerLayout <~ dlUnlockedStart <~ (if (collectionMoment.isDefined) dlUnlockedEnd else Tweak.blank)) ~
      (dom.topBarPanel <~ vVisible) ~
      (dom.searchBoxView <~ sbvClean <~ sbvDisableSearch) ~
      ((dom.drawerContent <~~
        closeAppDrawer(AppDrawerAnimation.readValue, dom.appDrawerMain)) ~~
        resetData(searchIsEmpty))).toService
  }

  def reloadContacts(): TaskService[Unit] = {
    val option = dom.getStatus match {
      case Some(status) => ContactsMenuOption(status)
      case _ => None
    }
    loadContactsAndSaveStatus(option getOrElse ContactsAlphabetical).toService
  }

  def reloadApps(): TaskService[Unit] = loadAppsAlphabetical.toService

  private[this] def manageException(throwable: Throwable) = throwable match {
    case e: CallPermissionException => appDrawerJobs.requestReadCallLog()
    case e: ContactPermissionException => appDrawerJobs.requestReadContacts()
    case _ => showGeneralError().toService
  }

  private[this] def showGeneralError(): Ui[Any] = dom.workspaces <~ vLauncherSnackbar(R.string.contactUsError)

  private[this] def openDrawer(longClick: Boolean) = {

    def revealInDrawer(longClick: Boolean): Ui[Future[_]] = {
      val showKeyboard = AppDrawerLongPressAction.readValue == AppDrawerLongPressActionOpenKeyboard && longClick
      (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
        (dom.paginationDrawerPanel <~ ivReloadPager(0)) ~
        ((dom.drawerContent <~~
          openAppDrawer(AppDrawerAnimation.readValue, dom.appDrawerMain)) ~~
          (dom.searchBoxView <~
            sbvEnableSearch <~
            (if (showKeyboard) sbvShowKeyboard else Tweak.blank)))
    }

    val loadContacts = AppDrawerLongPressAction.readValue == AppDrawerLongPressActionOpenContacts && longClick
    (if (loadContacts) {
      Ui(
        dom.recycler.getAdapter match {
          case a: AppsAdapter => a.clear()
          case _ =>
        }) ~ loadContactsAlphabetical
    } else if (dom.getItemsCount == 0) {
      loadAppsAlphabetical
    } else {
      Ui.nop
    }) ~ revealInDrawer(longClick) ~~ (dom.topBarPanel <~ vGone)
  }

  private[this] def openTabs: Ui[_] =
    (dom.tabs <~ tvOpen <~ showTabs) ~
      (dom.recycler <~ hideList)

  private[this] def closeDrawerTabs: Ui[_] =
    (dom.tabs <~ tvClose <~ hideTabs) ~
      (dom.recycler <~ showList)

  private[this] def changeContentView(contentView: ContentView): Ui[_] =
    (dom.searchBoxView <~ sbvClean) ~
      closeCursorAdapter ~
      (contentView match {
        case AppsView => loadAppsAlphabetical
        case ContactView => loadContactsAlphabetical
      })

  private[this] def closeCursorAdapter: Ui[_] = {

    def safeClose(closeable: Closeable): Unit = Try(closeable.close()) match {
      case Failure(ex) => printErrorMessage(ex)
      case _ =>
    }

    Ui {
      dom.recycler.getAdapter match {
        case a: Closeable => safeClose(a)
        case _ =>
      }
    }
  }

  private[this] def startMovementAppsContacts(): Ui[_] =
    (dom.pullToTabsView <~ pdvEnable(false)) ~
      (dom.screenAnimation <~
        (dom.getTypeView map (cv => sadvInitAnimation(cv, dom.getDrawerWidth)) getOrElse Tweak.blank))

  private[this] def moveMovementAppsContacts(displacement: Float): Ui[_] =
    dom.screenAnimation <~
      (dom.getTypeView map (cv => sadvMoveAnimation(cv, dom.getDrawerWidth, displacement)) getOrElse Tweak.blank)

  private[this] def endMovementAppsContacts(duration: Int): Ui[_] =
    (dom.pullToTabsView <~ pdvEnable(true)) ~
      (dom.screenAnimation <~ sadvEndAnimation(duration))

  private[this] def createDrawerPagers = {

    def paginationDrawer(position: Int) = (w[ImageView] <~ paginationDrawerItemStyle <~ vSetPosition(position)).get

    val pagerViews = 0 until pages map paginationDrawer
    dom.paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(option)) map (_.drawable)
    appDrawerJobs.loadContacts(option).resolveAsyncServiceOr(manageException)
    (dom.searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (dom.recycler <~ drvSetType(option))
  }

  private[this] def loadAppsAlphabetical: Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(ContactsAlphabetical)) map (_.drawable)
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (dom.paginationDrawerPanel <~ ivReloadPager(0)) ~
      (dom.pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0, None)) ~
      (dom.searchBoxView <~ sbvUpdateContentView(AppsView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def loadContactsAlphabetical: Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(AppsAlphabetical)) map (_.drawable)
    val favoriteContactsFirst = AppDrawerFavoriteContactsFirst.readValue
    loadContactsAndSaveStatus(if (favoriteContactsFirst) ContactsFavorites else ContactsAlphabetical) ~
      (dom.paginationDrawerPanel <~ ivReloadPager(1)) ~
      (dom.pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, if (favoriteContactsFirst) 1 else 0, None)) ~
      (dom.searchBoxView <~ sbvUpdateContentView(ContactView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(option)) map (_.drawable)
    appDrawerJobs.loadApps(option).resolveAsync()
    (dom.searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (dom.recycler <~ drvSetType(option))
  }

  private[this] def addApps(
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

  private[this] def addContacts(
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

  private[this] def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit): Ui[_] = {
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
    val searchIsEmpty = dom.searchBoxView.isEmpty
    val lastTimeContentViewWasChanged = dom.recycler.statuses.lastTimeContentViewWasChanged
    val addFieldTweaks = dom.getTypeView map {
      case AppsView => vAddField(SelectedItemDecoration.showLine, true)
      case ContactView => vAddField(SelectedItemDecoration.showLine, false)
    } getOrElse Tweak.blank
    closeCursorAdapter ~
      (dom.pullToTabsView <~ pdvEnable(true)) ~
      (dom.recycler <~
        vVisible <~
        rvLayoutManager(layoutManager) <~
        (if (searchIsEmpty && !lastTimeContentViewWasChanged) rvLayoutAnimation(R.anim.list_slide_in_bottom_animation) else Tweak.blank) <~
        addFieldTweaks <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)
  }

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[_] =
    dom.scrollerLayout <~
      fslEnabledScroller(true) <~
      fslLinkRecycler(dom.recycler) <~
      fslReset <~
      fslCounters(counters) <~
      fslSignalType(signalType)

  // Styles

  def scrollableStyle(implicit context: ContextWrapper, theme: NineCardsTheme) = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    vBackgroundBoxWorkspace(color = theme.get(DrawerBackgroundColor), horizontalPadding = padding) +
      fslColor(theme.get(PrimaryColor), theme.get(DrawerTabsBackgroundColor)) +
      fslMarginRightBarContent(padding)
  }

  def appDrawerMainStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    Lollipop ifSupportedThen {
      vStateListAnimator(R.anim.elevation_transition) +
        vPaddings(elevation) +
        vCircleOutlineProvider(elevation)
    } getOrElse tivPressedColor(theme.get(DockPressedColor))
  }

  def recyclerStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[RecyclerView] = rvFixedSize

  def paginationDrawerItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val margin = resGetDimensionPixelSize(R.dimen.margin_pager_drawer)
    val size = resGetDimensionPixelSize(R.dimen.drawer_size_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin) +
      ivSrc(R.drawable.drawer_pager)
  }

}
