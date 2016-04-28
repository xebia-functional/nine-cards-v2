package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.{KitKat, Lollipop}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection.CollectionsUiActions
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.DrawerUiActions
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact, _}
import com.fortysevendeg.ninecardslauncher.process.device.{GetAppOrder, GetByName}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.snails.LauncherSnails._
import ViewOps._
import UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

trait LauncherUiActionsImpl
  extends LauncherUiActions
  with CollectionsUiActions
  with DrawerUiActions {

  self: TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] =>

  implicit val uiContext: UiContext[Activity]

  implicit val presenter: LauncherPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  implicit val managerContext: FragmentManagerContext[Fragment, FragmentManager]

  override def initialize: Ui[Any] =
    Ui(initAllSystemBarsTint) ~
      prepareBars ~
      initCollectionsUi ~
      initDrawerUi

  override def addCollection(collection: Collection): Ui[Any] = uiActionCollection(Add, collection)

  override def removeCollection(collection: Collection): Ui[Any] = uiActionCollection(Remove, collection)

  override def showContactUsError(): Ui[Any] = showMessage(R.string.contactUsError)

  override def showMinimumOneCollectionMessage(): Ui[Any] = showMessage(R.string.minimumOneCollectionMessage)

  override def showLoading(): Ui[Any] = showCollectionsLoading

  override def goToPreviousScreenReordering(): Ui[Any] = {
    val canMoveToPreviousScreen = (workspaces ~> lwsCanMoveToPreviousScreen()).get getOrElse false
    (goToPreviousWorkspace() ~ (workspaces <~ lwsPrepareItemsScreenInReorder(numSpaces - 1))).ifUi(canMoveToPreviousScreen)
  }

  override def goToNextScreenReordering(): Ui[Any] = {
    val canMoveToNextScreen = (workspaces ~> lwsCanMoveToNextScreen()).get getOrElse false
    if (canMoveToNextScreen) {
      goToNextWorkspace() ~ (workspaces <~ lwsPrepareItemsScreenInReorder(0))
    } else {
      Ui.nop
    }
  }

  override def loadCollections(collections: Seq[Collection], apps: Seq[DockApp]): Ui[Any] =
    createCollections(collections, apps)

  def reloadCollectionsAfterReorder(from: Int, to: Int): Ui[Any] = reloadReorderedCollections(from, to)

  def reloadCollectionsFailed(): Ui[Any] = reloadCollections()

  override def showUserProfile(name: String, email: String, avatarUrl: Option[String]): Ui[Any] = userProfileMenu(name, email, avatarUrl)

  override def showPlusProfile(coverPhotoUrl: String): Ui[Any] = plusProfileMenu(coverPhotoUrl)

  override def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any] =
    addApps(
      apps = apps,
      clickListener = (app: App) => presenter.openApp(app),
      longClickListener = (app: App) => presenter.openSettings(app),
      getAppOrder = getAppOrder,
      counters = counters)

  override def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] =
    addContacts(
      contacts = contacts,
      clickListener = (contact: Contact) => presenter.openContact(contact),
      counters = counters)

  override def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any] =
    addLastCallContacts(contacts, (contact: LastCallsContact) => presenter.openLastCall(contact))

  override def back: Ui[Any] =
    if (isDrawerTabsOpened) {
      closeDrawerTabs
    } else if (isMenuVisible) {
      closeMenu()
    } else if (isDrawerVisible) {
      revealOutDrawer
    } else if (isActionShowed) {
      unrevealActionFragment
    } else if (isCollectionMenuVisible) {
      closeCollectionMenu()
    } else {
      Ui.nop
    }

  override def resetAction: Ui[Any] = turnOffFragmentContent

  override def logout: Ui[Any] = cleanWorkspaces() ~ Ui(presenter.goToWizard())

  override def closeTabs: Ui[Any] = closeDrawerTabs

  override def startReorder: Ui[Any] =
    (appDrawerPanel <~ fadeOut()) ~
      (paginationPanel <~ fadeOut()) ~
      (searchPanel <~ fadeOut()) ~
      (collectionActionsPanel <~ fadeIn())

  override def endReorder: Ui[Any] =
    (appDrawerPanel <~ fadeIn()) ~
      (paginationPanel <~ fadeIn()) ~
      (searchPanel <~ fadeIn()) ~
      (collectionActionsPanel <~~ fadeOut())

  private[this] def fadeIn() = vVisible + vAlpha(0) ++ applyAnimation(alpha = Some(1))

  private[this] def fadeOut() = applyAnimation(alpha = Some(0)) + vInvisible

  override def isTabsOpened: Boolean = isDrawerTabsOpened

  override def canRemoveCollections: Boolean = getCountCollections > 1

  override def isEmptyCollectionsInWorkspace: Boolean = isEmptyCollections

  def turnOffFragmentContent: Ui[_] =
    fragmentContent <~ vClickable(false)

  private[this] def prepareBars =
    KitKat.ifSupportedThen {
      val activity = activityContextWrapper.getOriginal
      val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
      val sbHeight = getStatusBarHeight
      val nbHeight = getNavigationBarHeight
      val elevation = resGetDimensionPixelSize(R.dimen.elevation_fab_button)
      Ui(activity.getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (content <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (menuCollectionRoot <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (drawerContent <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (actionFragmentContent <~
          vPadding(paddingDefault, paddingDefault + sbHeight, paddingDefault, paddingDefault + nbHeight)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace)) ~
        (Lollipop.ifSupportedThen {
          actionFragmentContent <~ vElevation(elevation)
        } getOrElse Ui.nop)
    } getOrElse Ui.nop

  def reloadPager(currentPage: Int) = Transformer {
    case imageView: ImageView if imageView.isPosition(currentPage) =>
      imageView <~ vActivated(true) <~~ pagerAppear
    case imageView: ImageView =>
      imageView <~ vActivated(false)
  }

}
