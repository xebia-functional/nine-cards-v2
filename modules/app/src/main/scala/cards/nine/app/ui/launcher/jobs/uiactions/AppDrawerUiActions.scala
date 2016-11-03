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
import cards.nine.app.ui.commons.adapters.search.SearchAdapter
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.app.ui.components.commons.SelectedItemDecoration
import cards.nine.app.ui.components.drawables.IconTypes
import cards.nine.app.ui.components.layouts._
import cards.nine.app.ui.components.layouts.snails.TabsSnails._
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import cards.nine.app.ui.components.layouts.tweaks.SearchBoxesViewTweaks._
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
import cards.nine.models.types.theme._
import cards.nine.models.types.{GetAppOrder, GetByCategory, GetByInstallDate, GetByName}
import cards.nine.models.{ApplicationData, Contact, LastCallsContact, TermCounter, _}
import cards.nine.process.device._
import cards.nine.process.device.models.{IterableApps, IterableContacts}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

class AppDrawerUiActions(val dom: LauncherDOM)
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

  lazy val appDrawerTabs: Seq[TabInfo] = Seq(
    TabInfo(R.drawable.app_drawer_filter_categories, resGetString(R.string.apps)),
    TabInfo(R.drawable.app_drawer_filter_favorites, resGetString(R.string.contacts)))

  lazy val appMenu = Seq(
    (R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.apps_alphabetical)),
    (R.drawable.app_drawer_filter_categories, resGetString(R.string.apps_categories)),
    (R.drawable.app_drawer_filter_installation_date, resGetString(R.string.apps_date)))

  lazy val contactsMenu = Seq(
    (R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.contacts_alphabetical)),
    (R.drawable.app_drawer_filter_favorites, resGetString(R.string.contacts_favorites)),
    (R.drawable.app_drawer_filter_last_call, resGetString(R.string.contacts_last)))

  def initialize(): TaskService[Unit] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue
    ((dom.searchBoxView <~
      sbvUpdateContentView(AppsView) <~
      sbvChangeListener(SearchBoxAnimatedListener(
        onHeaderIconClick = () => {
          (((dom.pullToTabsView ~> pdvIsEnabled()).get, dom.isSearchingInGooglePlay, dom.isDrawerTabsOpened) match {
            case (_, true, _) => backFromGooglePlaySearch()
            case (false, _, _) => Ui.nop
            case (true, _, true) => closeDrawerTabs()
            case (true, _, false) => openTabs()
          }).run
        },
        onOptionsClick = () => {
          val (icons, names) = dom.getTypeView match {
            case Some(AppsView) => (appMenu.map(_._1), appMenu.map(_._2))
            case Some(ContactView) => (contactsMenu.map(_._1), contactsMenu.map(_._2))
            case _ => (Seq.empty, Seq.empty)
          }
          val width = resGetDimensionPixelSize(R.dimen.width_popup_app_drawer)
          val horizontalOffset = resGetDimensionPixelSize(R.dimen.size_icon_app_large) - width
          (dom.searchBoxView.icon <~
            vListThemedPopupWindowShow(
              icons = icons,
              values = names,
              onItemClickListener = (position) => {
                (dom.getTypeView, position) match {
                  case (Some(AppsView), 0) => loadAppsAndSaveStatus(AppsAlphabetical)
                  case (Some(AppsView), 1) => loadAppsAndSaveStatus(AppsByCategories)
                  case (Some(AppsView), 2) => loadAppsAndSaveStatus(AppsByLastInstall)
                  case (Some(ContactView), 0) => loadContactsAndSaveStatus(ContactsAlphabetical)
                  case (Some(ContactView), 1) => loadContactsAndSaveStatus(ContactsFavorites)
                  case (Some(ContactView), 2) => loadContactsAndSaveStatus(ContactsByLastCall)
                  case _ => Ui.nop
                }
              },
              width = Option(width),
              horizontalOffset = Option(horizontalOffset))).run
        })) <~
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
      (dom.drawerMessage <~ tvSizeResource(FontSize.getSizeResource) <~ tvColor(theme.get(DrawerTextColor))) ~
      (dom.appDrawerMain <~
        appDrawerMainStyle <~
        On.click (openDrawer(longClick = false)) <~
        On.longClick (openDrawer(longClick = true) ~ Ui(true))) ~
      (dom.recycler <~
        recyclerStyle <~
        (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (dom.scrollerLayout <~ scrollableStyle) ~
      (dom.pullToTabsView <~
        ptvLinkTabs(
          tabs = Some(dom.tabs),
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(appDrawerTabs, 0, None) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            ((pos match {
              case 0 => loadAppsAlphabetical
              case 1 => loadContactsAlphabetical
              case _ => Ui.nop
            }) ~ (if (dom.isDrawerTabsOpened) closeDrawerTabs() else Ui.nop) ~ (dom.searchBoxView <~ sbvClean)).run
          }
        ))) ~
      loadAppsAlphabetical).toService
  }

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): TaskService[Unit] =
    if (apps.count() == 0) {
      showSearchGooglePlayMessage().toService
    } else {
      (hideMessage() ~
        addApps(
          apps = apps,
          clickListener = (app: ApplicationData) => navigationJobs.openApp(app).resolveAsyncServiceOr(manageException),
          longClickListener = (view: View, app: ApplicationData) => {
            dragJobs.startAddItemToCollection(app).resolveAsync()
            (view <~ vStartDrag(AddItemToCollection, new AppDrawerIconShadowBuilder(view))).run
          },
          getAppOrder = getAppOrder,
          counters = counters)).toService
    }

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): TaskService[Unit] =
    if (contacts.count() == 0) {
      showNoContactMessage().toService
    } else {
      (hideMessage() ~
        addContacts(
          contacts = contacts,
          clickListener = (contact: Contact) => navigationJobs.openContact(contact).resolveAsyncServiceOr(manageException),
          longClickListener = (view: View, contact: Contact) => {
            dragJobs.startAddItemToCollection(contact).resolveAsync()
            (view <~ vStartDrag(AddItemToCollection, new AppDrawerIconShadowBuilder(view))).run
          },
          counters = counters)).toService
    }

  def reloadSearchInDrawer(
    apps: Seq[NotCategorizedPackage]): TaskService[Unit] =
    if (apps.isEmpty) {
      showAppsNotFoundInGooglePlay().toService
    } else {
      (hideMessage() ~
        (dom.searchBoxView <~ vAddField(dom.searchingGooglePlayKey, true)) ~
        addSearch(
          apps = apps,
          clickListener = (app: NotCategorizedPackage) => {
            navigationJobs.launchGooglePlay(app.packageName).resolveAsyncServiceOr(_ =>
              navigationJobs.navigationUiActions.showContactUsError())
          }) ~
        (dom.searchBoxView <~ sbvUpdateHeaderIcon(IconTypes.BACK))).toService
    }

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): TaskService[Unit] =
    addLastCallContacts(contacts, (contact: LastCallsContact) =>
      navigationJobs.openLastCall(contact.number).resolveAsyncServiceOr(manageException)).toService

  def closeTabs(): TaskService[Unit] = closeDrawerTabs().toService

  def close(): TaskService[Unit] = {

    def resetData() =
      (if (dom.isEmptySearchBox && dom.isShowingAppsAlphabetical) {
        (dom.recycler <~ rvScrollToTop) ~ (dom.scrollerLayout <~ fslReset)
      } else {
        closeCursorAdapter ~
          loadAppsAlphabetical ~
          (dom.searchBoxView <~ sbvUpdateContentView(AppsView)) ~
          (dom.pullToTabsView <~ ptvActivate(0))
      }) ~ (dom.searchBoxView <~ sbvUpdateHeaderIcon(IconTypes.BURGER))

    ((dom.searchBoxView <~ vAddField(dom.searchingGooglePlayKey, false)) ~
      (dom.drawerLayout <~ dlUnlockedStart <~ (if (dom.hasCurrentMomentAssociatedCollection) dlUnlockedEnd else Tweak.blank)) ~
      (dom.topBarPanel <~ vVisible) ~
      (dom.searchBoxView <~ sbvClean <~ sbvDisableSearch) ~
      ((dom.drawerContent <~~
        closeAppDrawer(AppDrawerAnimation.readValue, dom.appDrawerMain)) ~~
        resetData())).toService
  }

  def reloadContacts(): TaskService[Unit] = {
    val option = dom.getStatus match {
      case Some(status) => ContactsMenuOption(status)
      case _ => None
    }
    loadContactsAndSaveStatus(option getOrElse ContactsAlphabetical).toService
  }

  def reloadApps(): TaskService[Unit] = loadAppsAlphabetical.toService

  def showLoadingInGooglePlay(): TaskService[Unit] = showSearchingInGooglePlay().toService

  private[this] def manageException(throwable: Throwable) = throwable match {
    case e: CallPermissionException => appDrawerJobs.requestReadCallLog()
    case e: ContactPermissionException => appDrawerJobs.requestReadContacts()
    case _ => showGeneralError().toService
  }

  private[this] def showSearchGooglePlayMessage(): Ui[Any] =
    (dom.drawerMessage <~ tvText(R.string.apps_not_found) <~ vVisible) ~
      (dom.recycler <~ vGone)

  private[this] def showNoContactMessage(): Ui[Any] =
    (dom.drawerMessage <~ tvText(R.string.contacts_not_found) <~ vVisible) ~
      (dom.recycler <~ vGone)

  private[this] def showSearchingInGooglePlay(): Ui[Any] =
    (dom.drawerMessage <~ tvText(R.string.searching_in_google_play) <~ vVisible) ~
      (dom.recycler <~ vGone)

  private[this] def showAppsNotFoundInGooglePlay(): Ui[Any] =
    (dom.drawerMessage <~ tvText(R.string.apps_not_found_in_google_play) <~ vVisible) ~
      (dom.recycler <~ vGone)

  private[this] def hideMessage(): Ui[Any] =
    (dom.drawerMessage <~ vGone) ~ (dom.recycler <~ vVisible)

  private[this] def showGeneralError(): Ui[Any] = dom.workspaces <~ vLauncherSnackbar(R.string.contactUsError)

  private[this] def openDrawer(longClick: Boolean) = {

    def revealInDrawer(longClick: Boolean): Ui[Future[_]] = {
      val showKeyboard = AppDrawerLongPressAction.readValue == AppDrawerLongPressActionOpenKeyboard && longClick
      (dom.drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
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

  private[this] def openTabs(): Ui[Any] =
    (dom.tabs <~ tvOpen <~ showTabs) ~
      (dom.recycler <~ hideList) ~
      (dom.searchBoxView <~ sbvUpdateHeaderIcon(IconTypes.UP))

  private[this] def closeDrawerTabs(): Ui[Any] =
    (dom.tabs <~ tvClose <~ hideTabs) ~
      (dom.recycler <~ showList) ~
      (dom.searchBoxView <~ sbvUpdateHeaderIcon(IconTypes.BURGER))

  private[this] def backFromGooglePlaySearch(): Ui[Any] =
    loadAppsAlphabetical ~
      (dom.searchBoxView <~ sbvUpdateHeaderIcon(IconTypes.BURGER) <~ sbvClean) ~
      (dom.searchBoxView <~ vAddField(dom.searchingGooglePlayKey, false))

  private[this] def closeCursorAdapter: Ui[Any] = {

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

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[Any] = {
    appDrawerJobs.loadContacts(option).resolveAsyncServiceOr(manageException)
    dom.recycler <~ drvSetType(option)
  }

  private[this] def loadAppsAlphabetical: Ui[Any] = {
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (dom.searchBoxView <~ sbvUpdateContentView(AppsView))
  }

  private[this] def loadContactsAlphabetical: Ui[Any] = {
    val favoriteContactsFirst = AppDrawerFavoriteContactsFirst.readValue
    loadContactsAndSaveStatus(if (favoriteContactsFirst) ContactsFavorites else ContactsAlphabetical) ~
      (dom.searchBoxView <~ sbvUpdateContentView(ContactView))
  }

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[Any] = {
    appDrawerJobs.loadApps(option).resolveAsync()
    dom.recycler <~ drvSetType(option)
  }

  private[this] def addApps(
    apps: IterableApps,
    clickListener: (ApplicationData) => Unit,
    longClickListener: (View, ApplicationData) => Unit,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] = {
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
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] = {
    val contactAdapter = ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = Some(longClickListener))
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      counters)
  }

  private[this] def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit): Ui[Any] = {
    val contactAdapter = LastCallsAdapter(
      contacts = contacts,
      clickListener = clickListener)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      Seq.empty)
  }

  private[this] def addSearch(
    apps: Seq[NotCategorizedPackage],
    clickListener: (NotCategorizedPackage) => Unit): Ui[Any] = {
    val appsAdapter = new SearchAdapter(apps, clickListener)
    swipeAdapter(
      adapter = appsAdapter,
      layoutManager = appsAdapter.getLayoutManager,
      counters = Seq.empty)
  }

  private[this] def swipeAdapter(
    adapter: RecyclerView.Adapter[_],
    layoutManager: LayoutManager,
    counters: Seq[TermCounter],
    signalType: FastScrollerSignalType = FastScrollerText) = {
    val addFieldTweaks = dom.getTypeView map {
      case AppsView => vAddField(SelectedItemDecoration.showLine, true)
      case ContactView => vAddField(SelectedItemDecoration.showLine, false)
    } getOrElse Tweak.blank
    closeCursorAdapter ~
      (dom.pullToTabsView <~ pdvEnable(true)) ~
      (dom.recycler <~
        vVisible <~
        rvLayoutManager(layoutManager) <~
        (if (dom.isEmptySearchBox) rvLayoutAnimation(R.anim.list_slide_in_bottom_animation) else Tweak.blank) <~
        addFieldTweaks <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)
  }

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[Any] =
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
