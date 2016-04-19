package com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.NavigationViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{AnimatedWorkSpacesListener, LauncherWorkSpacesListener, WorkSpaceItemMenu}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection.NewCollectionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections.PublicCollectionsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.snails.LauncherSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherTags, LauncherUiActionsImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.NineCardsPreferencesActivity
import com.fortysevendeg.ninecardslauncher.app.ui.profile.ProfileActivity
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CollectionsUiActions
  extends Styles
  with ActionsBehaviours {

  self: TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] with LauncherUiActionsImpl =>

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  val maxBackgroundPercent: Float = 0.4f

  val pageWidgets = 0

  val pageCollections = 1

  lazy val drawerLayout = Option(findView(TR.launcher_drawer_layout))

  lazy val navigationView = Option(findView(TR.launcher_navigation_view))

  lazy val menuName = Option(findView(TR.menu_name))

  lazy val menuEmail = Option(findView(TR.menu_email))

  lazy val menuAvatar = Option(findView(TR.menu_avatar))

  lazy val menuCover = Option(findView(TR.menu_cover))

  lazy val loading = Option(findView(TR.launcher_loading))

  lazy val content = Option(findView(TR.launcher_content))

  lazy val workspaces = Option(findView(TR.launcher_work_spaces))

  lazy val appDrawerPanel = Option(findView(TR.launcher_drawer_panel))

  lazy val appDrawer1 = Option(findView(TR.launcher_page_1))

  lazy val appDrawer2 = Option(findView(TR.launcher_page_2))

  lazy val appDrawer3 = Option(findView(TR.launcher_page_3))

  lazy val appDrawer4 = Option(findView(TR.launcher_page_4))

  lazy val paginationPanel = Option(findView(TR.launcher_pagination_panel))

  lazy val searchPanel = Option(findView(TR.launcher_search_panel))

  lazy val burgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val googleIcon = Option(findView(TR.launcher_google_icon))

  lazy val micIcon = Option(findView(TR.launcher_mic_icon))

  lazy val actionFragmentContent = Option(findView(TR.action_fragment_content))

  lazy val menuCollectionRoot = Option(findView(TR.menu_collection_root))

  lazy val menuCollectionContent = Option(findView(TR.menu_collection_content))

  var dockApps: Seq[DockApp] = Seq.empty

  def initCollectionsUi: Ui[_] =
    (drawerLayout <~ dlStatusBarBackground(android.R.color.transparent)) ~
      (navigationView <~ nvNavigationItemSelectedListener(itemId => {
        (goToMenuOption(itemId) ~ closeMenu()).run
        true
      })) ~
      (menuCollectionRoot <~ vGone) ~
      (workspaces <~
        lwsPresenter(presenter) <~
        lwsListener(
          LauncherWorkSpacesListener(
            onStartOpenMenu = startOpenCollectionMenu,
            onUpdateOpenMenu = updateOpenCollectionMenu,
            onEndOpenMenu = closeCollectionMenu
          )
        ) <~
        awsListener(AnimatedWorkSpacesListener(
          onLongClick = () => (drawerLayout <~ dlOpenDrawer).run)
        )) ~
      (searchPanel <~ searchContentStyle) ~
      (menuCollectionContent <~ vgAddViews(getItemsForFabMenu)) ~
      (burgerIcon <~ burgerButtonStyle <~ On.click(
        drawerLayout <~ dlOpenDrawer
      )) ~
      (googleIcon <~ googleButtonStyle <~ On.click(Ui(presenter.launchSearch))) ~
      (micIcon <~ micButtonStyle <~ On.click(Ui(presenter.launchVoiceSearch))) ~
      (appDrawer1 <~ drawerItemStyle <~ vSetPosition(0) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer2 <~ drawerItemStyle <~ vSetPosition(1) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer3 <~ drawerItemStyle <~ vSetPosition(2) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer4 <~ drawerItemStyle <~ vSetPosition(3) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      })

  def showMessage(message: Int): Ui[_] = drawerLayout <~ vSnackbarShort(message)

  def showCollectionsLoading: Ui[_] = loading <~ vVisible

  def createCollections(
    collections: Seq[Collection],
    apps: Seq[DockApp]): Ui[_] = {
    dockApps = apps
    (loading <~ vGone) ~
      (workspaces <~
        lwsData(collections, selectedPageDefault) <~
        awsAddPageChangedObserver(currentPage => {
          (paginationPanel <~ reloadPager(currentPage)).run
        }
        )) ~
      (appDrawerPanel <~ fillAppDrawer) ~
      createPager(selectedPageDefault)
  }

  def reloadReorderedCollections(from: Int, to: Int): Ui[Any] = workspaces <~ lwsReloadReorderedCollections(from, to)

  def reloadCollections(): Ui[Any] = workspaces <~ lwsReloadCollections()

  def userProfileMenu(name: String, email: String, avatarUrl: Option[String]): Ui[_] =
    (menuName <~ tvText(name)) ~
      (menuEmail <~ tvText(email)) ~
      (menuAvatar <~
        (avatarUrl map ivUri getOrElse {
          val drawable = new CharDrawable(name.substring(0, 1).toUpperCase)
          ivSrc(drawable)
        }) <~
        menuAvatarStyle)

  def plusProfileMenu(coverPhotoUrl: String): Ui[_] =
    menuCover <~ ivUri(coverPhotoUrl)

  def uiActionCollection(action: UiAction, collection: Collection): Ui[_] =
    action match {
      case Add => (workspaces <~ lwsAddCollection(collection)) ~ reloadPagerAndActiveLast
      case Remove => (workspaces <~ lwsRemoveCollection(collection)) ~ reloadPagerAndActiveLast
    }

  def closeMenu(): Ui[_] = drawerLayout <~ dlCloseDrawer

  def closeCollectionMenu(): Ui[_] = workspaces <~ lwsCloseMenu

  def cleanWorkspaces(): Ui[_] = workspaces <~ lwsClean

  def isMenuVisible: Boolean = drawerLayout exists (_.isDrawerOpen(GravityCompat.START))

  def isCollectionMenuVisible: Boolean = workspaces exists (_.workSpacesStatuses.openedMenu)

  def goToWorkspace(page: Int): Ui[_] =
    (workspaces <~ lwsSelect(page)) ~
      (paginationPanel <~ reloadPager(page))

  protected def goToMenuOption(itemId: Int): Ui[_] = {
    (itemId, activityContextWrapper.original.get) match {
      case (R.id.menu_collections, _) => goToWorkspace(pageCollections)
      case (R.id.menu_moments, _) => goToWorkspace(pageWidgets)
      case (R.id.menu_profile, Some(activity)) => uiStartIntentForResult(new Intent(activity, classOf[ProfileActivity]), RequestCodes.goToProfile)
      case (R.id.menu_wallpapers, _) => uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER))
      case (R.id.menu_android_settings, _) => uiStartIntent(new Intent(android.provider.Settings.ACTION_SETTINGS))
      case (R.id.menu_9cards_settings, Some(activity)) => uiStartIntent(new Intent(activity, classOf[NineCardsPreferencesActivity]))
      case (R.id.menu_widgets, _) => showMessage(R.string.todo)
      case _ => Ui.nop
    }
  }

  protected def clickAppDrawerItem(view: View): Ui[_] = Ui {
    view.getPosition flatMap dockApps.lift foreach { app =>
      presenter.execute(app.intent)
    }
  }

  def getCountCollections: Int = workspaces map (_.getCountCollections) getOrElse 0

  protected def isEmptyCollections = workspaces exists (_.isEmptyCollections)

  protected def getItemsForFabMenu = Seq(
    (w[WorkSpaceItemMenu] <~ workspaceButtonCreateCollectionStyle <~ FuncOn.click { view: View =>
      showAction(f[NewCollectionFragment], view, resGetColor(R.color.collection_fab_button_item_create_new_collection))
    }).get,
    (w[WorkSpaceItemMenu] <~ workspaceButtonMyCollectionsStyle <~ FuncOn.click { view: View =>
      showAction(f[PrivateCollectionsFragment], view, resGetColor(R.color.collection_fab_button_item_my_collections))
    }).get,
    (w[WorkSpaceItemMenu] <~ workspaceButtonPublicCollectionStyle <~ FuncOn.click { view: View =>
      showAction(f[PublicCollectionsFragment], view, resGetColor(R.color.collection_fab_button_item_public_collection))
    }).get
  )

  private[this] def startOpenCollectionMenu(): Ui[_] =
    (menuCollectionRoot <~ vVisible <~ vClearClick) ~
      (appDrawerPanel <~ fade(out = true)) ~
      (paginationPanel <~ fade(out = true)) ~
      (searchPanel <~ fade(out = true))

  private[this] def updateOpenCollectionMenu(percent: Float): Ui[_] = {
    val backgroundPercent = maxBackgroundPercent * percent
    val colorBackground = ColorsUtils.setAlpha(Color.BLACK, backgroundPercent)
    val height = (menuCollectionContent map (_.getHeight) getOrElse 0) + getNavigationBarHeight
    val translate = height - (height * percent)
    (menuCollectionRoot <~ vBackgroundColor(colorBackground)) ~
      (menuCollectionContent <~ vTranslationY(translate))
  }

  private[this] def closeCollectionMenu(opened: Boolean): Ui[_] =
    if (opened) {
      menuCollectionRoot <~ On.click(closeCollectionMenu())
    } else {
      (appDrawerPanel <~ fade()) ~
        (paginationPanel <~ fade()) ~
        (searchPanel <~ fade()) ~
        (menuCollectionRoot <~ vGone)
    }

  private[this] def createPager(activatePosition: Int) =
    workspaces map { ws =>
      val pagerViews = 0 until ws.getWorksSpacesCount map { position =>
        val view = pagination(position)
        view.setActivated(activatePosition == position)
        view
      }
      paginationPanel <~ vgRemoveAllViews <~ vgAddViews(pagerViews)
    } getOrElse Ui.nop

  private[this] def reloadPagerAndActiveLast =
    workspaces map { ws =>
      val count = ws.getWorksSpacesCount
      val pagerViews = 0 until count map { position =>
        val view = pagination(position)
        view.setActivated(count - 1 == position)
        view
      }
      paginationPanel <~ vgRemoveAllViews <~ vgAddViews(pagerViews)
    } getOrElse Ui.nop

  private[this] def fillAppDrawer = Transformer {
    case i: ImageView if i.isType(LauncherTags.app) =>
      i.getPosition map { position =>
        val dockApp = dockApps(position)
        i <~ ivUri(dockApp.imagePath)
      } getOrElse Ui.nop
  }

  def pagination(position: Int) =
    (w[ImageView] <~ paginationItemStyle <~ vSetPosition(position)).get

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], view: View, color: Int, map: Map[String, String] = Map.empty): Ui[_] = {
    val sizeIconWorkSpaceMenuItem = resGetDimensionPixelSize(R.dimen.size_workspace_menu_item)
    val (startX: Int, startY: Int) = Option(view.findViewById(R.id.workspace_icon)) map calculateAnchorViewPosition getOrElse(0, 0)
    val x = startX + (sizeIconWorkSpaceMenuItem / 2)
    val y = startY + (sizeIconWorkSpaceMenuItem / 2)
    val args = new Bundle()
    args.putInt(BaseActionFragment.sizeIcon, sizeIconWorkSpaceMenuItem)
    args.putInt(BaseActionFragment.startRevealPosX, x)
    args.putInt(BaseActionFragment.startRevealPosY, y)
    args.putInt(BaseActionFragment.endRevealPosX, x)
    args.putInt(BaseActionFragment.endRevealPosY, y)
    map foreach {
      case (key, value) => args.putString(key, value)
    }
    args.putInt(BaseActionFragment.colorPrimary, color)
    (fragmentContent <~ vClickable(true)) ~
      addFragment(fragmentBuilder.pass(args), Option(R.id.action_fragment_content), Option(nameActionFragment))
  }

}
