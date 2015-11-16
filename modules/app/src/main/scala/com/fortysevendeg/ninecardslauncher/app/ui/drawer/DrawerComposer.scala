package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.HeaderedItemAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.header.HeaderGenerator
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.DrawerTab._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device.{GetByInstallDate, GetAppOrder}
import com.fortysevendeg.ninecardslauncher.process.device.models.{App, Contact}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider
  with HeaderGenerator {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  lazy val emptyAdapter = new RecyclerView.Adapter[RecyclerView.ViewHolder]() {

    override def getItemCount: Int = 0

    override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {}

    override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder = new RecyclerView.ViewHolder(viewGroup) {}
  }

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val drawerTabApp = Option(findView(TR.launcher_drawer_tab_app))

  lazy val drawerTabContacts = Option(findView(TR.launcher_drawer_tab_contact))

  lazy val loadingDrawer = Option(findView(TR.launcher_drawer_loading))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  lazy val drawerFabButton = Option(findView(TR.launcher_drawer_button))

  def showDrawerLoading: Ui[_] = (loadingDrawer <~ vVisible) ~
    (recycler <~ vGone) ~
    (scrollerLayout <~ fslInvisible)

  def showDrawerData: Ui[_] = (loadingDrawer <~ vGone) ~
    (recycler <~ vVisible) ~
    (scrollerLayout <~ fslVisible)

  def showGeneralError: Ui[_] = drawerContent <~ uiSnackbarShort(R.string.contactUsError)

  def initDrawerUi(
    launchStore: () => Unit,
    launchDial: () => Unit,
    onAppMenuClickListener: (AppsMenuOption) => Unit,
    onContactMenuClickListener: (ContactsMenuOption) => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (appDrawerMain <~ drawerAppStyle <~ On.click {
      revealInDrawer ~ tryToCallListener(onAppMenuClickListener, onContactMenuClickListener)
    }) ~
      (loadingDrawer <~ pbColor(resGetColor(R.color.drawer_toolbar))) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(resGetColor(R.color.drawer_toolbar))) ~
      (drawerContent <~ vGone) ~
      (drawerFabButton <~
        ivSrc(R.drawable.app_drawer_fab_button_play) <~
        On.click(fabButtonClicked(launchStore, launchDial))) ~
      (drawerTabApp <~
        dtInitTab(
          drawableOn = R.drawable.app_drawer_icon_applications,
          drawableOff = R.drawable.app_drawer_icon_applications_inactive,
          menuResource = R.menu.drawer_apps_menu,
          menuItemId = R.id.sort_apps_alphabetical,
          menuListener = callAppsListener(onAppMenuClickListener, _)) <~
        On.click(appsTabClicked(onAppMenuClickListener))) ~
      (drawerTabContacts <~
        dtInitTab(
          drawableOn = R.drawable.app_drawer_icon_contacts,
          drawableOff = R.drawable.app_drawer_icon_contacts_inactive,
          selected = false,
          menuItemId = R.id.sort_contacts_alphabetical,
          menuResource = R.menu.drawer_contacts_menu,
          menuListener = callContactsListener(onContactMenuClickListener, _)) <~
        On.click(contactsTabClicked(onContactMenuClickListener)))

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateNavigationToBlack ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source))) ~~
      updateStatusColor(resGetColor(R.color.drawer_status_bar))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    ((recycler <~
      rvAdapter(emptyAdapter)) ~
      updateStatusToTransparent ~
      updateNavigationToTransparent) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealOutAppDrawer(source)))

  def addApps(apps: Seq[App], getAppOrder: GetAppOrder, clickListener: (App) => Unit, longClickListener: (App) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new AppsAdapter(
      initialSeq = generateHeaderList(apps, getAppOrder),
      clickListener = clickListener,
      longClickListener = Option(longClickListener)),
      fastScrollerVisible = isScrollerLayoutVisible(getAppOrder))

  private[this] def isScrollerLayoutVisible(getAppOrder: GetAppOrder) = getAppOrder match {
    case v: GetByInstallDate => false
    case _ => true
  }

  def addContacts(contacts: Seq[Contact], clickListener: (Contact) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] =
    swipeAdapter(new ContactsAdapter(
      initialSeq = generateContactsForList(contacts),
      clickListener = clickListener,
      longClickListener = None))

  private[this] def swipeAdapter(
    adapter: HeaderedItemAdapter[_],
    fastScrollerVisible: Boolean = true) =
    showDrawerData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(fastScrollerVisible)

  def scrollerLayoutUi(fastScrollerVisible: Boolean): Ui[_] =
    if (fastScrollerVisible) {
      scrollerLayout <~ fslVisible <~ fslLinkRecycler <~ fslReset
    } else {
      scrollerLayout <~ fslInvisible
    }


  private[this] def appsTabClicked(listener: (AppsMenuOption) => Unit): Ui[_] =
    drawerTabApp map (t => (t.isSelected, t.getSelectedMenuItem)) match {
      case Some((false, menuItemId)) =>
        (drawerTabApp <~ dtSelect) ~
          (drawerTabContacts <~ dtUnselect) ~
          (drawerFabButton <~ ivSrc(R.drawable.app_drawer_fab_button_play)) ~
          Ui(callAppsListener(listener, menuItemId))
      case Some((true, _)) => drawerTabApp <~ dtOpenMenu
      case _ => Ui.nop
    }

  private[this] def toAppsMenuOption(menuItemId: Int): Option[AppsMenuOption] =
    menuItemId match {
      case R.id.sort_apps_alphabetical => Some(AppsAlphabetical)
      case R.id.sort_apps_categories => Some(AppsByCategories)
      case R.id.sort_apps_date => Some(AppsByLastInstall)
      case _ => None
    }

  private[this] def callAppsListener(
    listener: (AppsMenuOption) => Unit,
    menuItemId: Int): Unit =
    toAppsMenuOption(menuItemId) foreach listener

  private[this] def contactsTabClicked(listener: (ContactsMenuOption) => Unit): Ui[_] =
    drawerTabContacts map (t => (t.isSelected, t.getSelectedMenuItem)) match {
      case Some((false, menuItemId)) =>
        (drawerTabContacts <~ dtSelect) ~
          (drawerTabApp <~ dtUnselect) ~
          (drawerFabButton <~ ivSrc(R.drawable.app_drawer_fab_button_contact)) ~
          Ui(toContactsMenuOption(menuItemId) foreach listener)
      case Some((true, _)) => drawerTabContacts <~ dtOpenMenu
      case _ => Ui.nop
    }

  private[this] def toContactsMenuOption(menuItemId: Int): Option[ContactsMenuOption] =
    menuItemId match {
      case R.id.sort_contacts_alphabetical => Some(ContactsAlphabetical)
      case R.id.sort_contacts_favorites => Some(ContactsFavorites)
      case R.id.sort_contacts_last => Some(ContactsByLastCall)
      case _ => None
    }

  private[this] def callContactsListener(
    listener: (ContactsMenuOption) => Unit,
    menuItemId: Int): Unit =
    toContactsMenuOption(menuItemId) foreach listener

  private[this] def tryToCallListener(
    onAppMenuClickListener: (AppsMenuOption) => Unit,
    onContactMenuClickListener: (ContactsMenuOption) => Unit): Ui[Unit] = (drawerTabApp, drawerTabContacts) match {
    case (Some(app), Some(contacts)) if app.isSelected =>
      Ui(callAppsListener(onAppMenuClickListener, app.getSelectedMenuItem))
    case (Some(app), Some(contacts)) if contacts.isSelected =>
      Ui(callContactsListener(onContactMenuClickListener, contacts.getSelectedMenuItem))
    case _ => Ui.nop
  }

  private[this] def fabButtonClicked(launchStore: () => Unit, launchDial: () => Unit): Ui[_] =
    (drawerTabApp, drawerTabContacts) match {
      case (Some(appTab), Some(contactTab)) if appTab.isSelected => Ui(launchStore())
      case (Some(appTab), Some(contactTab)) if contactTab.isSelected => Ui(launchDial())
      case _ => Ui.nop
    }

}
