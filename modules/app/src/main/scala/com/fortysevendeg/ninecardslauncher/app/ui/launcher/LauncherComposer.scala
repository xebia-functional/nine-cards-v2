package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import java.io.File

import android.content.{Context, Intent}
import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{WindowManager, View}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.KitKat
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FabButtonBehaviour, LauncherExecutor, SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{AnimatedWorkSpacesListener, FabItemMenu}
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerComposer
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.newcollection.NewCollectionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections.PrivateCollectionsFragment
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.UserInfo
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

trait LauncherComposer
  extends Styles
  with DrawerComposer
  with ActionsBehaviours
  with FabButtonBehaviour
  with LauncherExecutor {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  // TODO For now, we always use 4 applications in app drawer panel
  lazy val packagesForAppsDrawer = Seq(
    Seq(("com.google.android.talk", "com.google.android.talk.SigningInActivity")),
    Seq(("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail")),
    Seq(("com.android.chrome", "com.google.android.apps.chrome.Main")),
    Seq(
      ("com.google.android.GoogleCamera", "com.android.camera.CameraLauncher"),
      ("com.oneplus.camera", "com.oneplus.camera.OPCameraActivity"))
  )

  lazy val cardsForAppsDrawer = packagesForAppsDrawer map { apps =>
    val dirPath = getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE).getPath
    val maybeApp = apps find { app =>
      val (packageName, className) = app
      val imagePath = s"$dirPath/${packageName.toLowerCase.replace(".", "_")}_${className.toLowerCase.replace(".", "_")}"
      new File(imagePath).exists()
    }
    maybeApp map { app =>
      val (packageName, className) = app
      val imagePath = s"$dirPath/${packageName.toLowerCase.replace(".", "_")}_${className.toLowerCase.replace(".", "_")}"
      val intent = NineCardIntent(NineCardIntentExtras(
        package_name = Option(packageName),
        class_name = Option(className)))
      intent.setAction(NineCardsIntentExtras.openApp)
      intent.setClassName(packageName, className)
      Card(
        id = 0,
        position = 0,
        term = "Doesn't matter",
        packageName = Option(packageName),
        cardType = CardType.app,
        intent = intent,
        imagePath = imagePath
      )
    } getOrElse {
      val packageName = "com.fortysevendeg.ninecardslauncher2"
      val className = "com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherActivity"
      val intent = NineCardIntent(NineCardIntentExtras(
        package_name = Option(packageName),
        class_name = Option(className)))
      intent.setAction(NineCardsIntentExtras.openApp)
      intent.setClassName(packageName, className)
      val imagePath = s"$dirPath/${packageName.toLowerCase.replace(".", "_")}_${className.toLowerCase.replace(".", "_")}"
      Card(
        id = 0,
        position = 0,
        term = "Doesn't matter",
        packageName = Option(packageName),
        cardType = CardType.app,
        intent = intent,
        imagePath = imagePath
      )
    }

  }

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  val pageWidgets = 0

  val pageCollections = 1

  var workspaces: Option[LauncherWorkSpaces] = None

  lazy val drawerLayout = Option(findView(TR.launcher_drawer_layout))

  lazy val navigationView = Option(findView(TR.launcher_navigation_view))

  lazy val menuName = Option(findView(TR.menu_name))

  lazy val menuAvatar = Option(findView(TR.menu_avatar))

  lazy val loading = Option(findView(TR.launcher_loading))

  lazy val content = Option(findView(TR.launcher_content))

  lazy val workspacesContent = Option(findView(TR.launcher_work_spaces_content))

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

  def showMessage(message: Int): Ui[_] = drawerLayout <~ uiSnackbarShort(message)

  def updateBarsInFabMenuShow: Ui[_] = Ui.nop

  def updateBarsInFabMenuHide: Ui[_] = Ui.nop

  def showLoading(implicit context: ActivityContextWrapper): Ui[_] = loading <~ vVisible

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme, managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    prepareBars ~
      (drawerLayout <~ dlStatusBarBackground(android.R.color.transparent)) ~
      (navigationView <~ nvNavigationItemSelectedListener(itemId => {
        runUi(goToMenuOption(itemId))
        true
      })) ~
      (workspacesContent <~
        vgAddView(getUi(w[LauncherWorkSpaces] <~
          wire(workspaces) <~
          awsListener(AnimatedWorkSpacesListener(
            startScroll = (toRight: Boolean) => {
              val goToWizardScreen = workspaces exists (_.goToWizardScreen(toRight))
              val collectionScreen = workspaces exists (_.isCollectionScreen)
              (goToWizardScreen, collectionScreen) match {
                case (false, true) => runUi(showFabButton())
                case _ =>
              }
            },
            endScroll = () => {
              val collectionScreen = workspaces exists (_.isCollectionScreen)
              if (collectionScreen) runUi(showFabButton())
            },
            onClick = () => {
              val collectionScreen = workspaces exists (_.isCollectionScreen)
              if (collectionScreen) runUi(showFabButton())
            }))))) ~
      (searchPanel <~ searchContentStyle) ~
      (menuAvatar <~ menuAvatarStyle) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      (burgerIcon <~ burgerButtonStyle <~ On.click(
        drawerLayout <~ dlOpenDrawer
      )) ~
      (googleIcon <~ googleButtonStyle <~ On.click(Ui(launchSearch))) ~
      (micIcon <~ micButtonStyle <~ On.click(Ui(launchVoiceSearch))) ~
      (appDrawer1 <~ drawerItemStyle <~ vTag2(R.id.app_drawer_position, 0) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer2 <~ drawerItemStyle <~ vTag2(R.id.app_drawer_position, 1) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer3 <~ drawerItemStyle <~ vTag2(R.id.app_drawer_position, 2) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer4 <~ drawerItemStyle <~ vTag2(R.id.app_drawer_position, 3) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      })

  def createCollections(
    collections: Seq[Collection])
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] =
    (loading <~ vGone) ~
      (workspaces <~
        lwsData(collections, selectedPageDefault) <~
        awsAddPageChangedObserver(currentPage => {
          val widgetScreen = workspaces exists (_.isWidgetScreen(currentPage))
          runUi((paginationPanel <~ reloadPager(currentPage)) ~
            (if (widgetScreen) {
              hideFabButton
            } else {
              Ui.nop
            }))
        }
        )) ~
      (appDrawerPanel <~ fillAppDrawer(collections)) ~
      createPager(selectedPageDefault)

  def userInfoMenu(userInfo: UserInfo)(implicit uiContext: UiContext[_]): Ui[_] =
    (menuName <~ tvText(userInfo.email)) ~
      (menuAvatar <~ ivUri(userInfo.imageUrl))

  def uiAddCollection(collection: Collection)
    (implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (workspaces <~ lwsAddCollection(collection)) ~ reloadPagerAndActiveLast

  def uiRemoveCollection(collection: Collection)
    (implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (workspaces <~ lwsRemoveCollection(collection)) ~ reloadPagerAndActiveLast

  def closeMenu(): Ui[_] = drawerLayout <~ dlCloseDrawer

  def isMenuVisible: Boolean = drawerLayout exists (_.isDrawerOpen(GravityCompat.START))

  def goToWorkspace(page: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (workspaces <~ lwsSelect(page)) ~
      (paginationPanel <~ reloadPager(page)) ~
      closeMenu()

  def backByPriority(implicit context: ActivityContextWrapper, manager: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] = if (isMenuVisible) {
    closeMenu()
  } else if (fabMenuOpened) {
    swapFabButton()
  } else if (isDrawerVisible) {
    revealOutDrawer
  } else if (isActionShowed) {
    unrevealActionFragment
  } else {
    Ui.nop
  }

  private[this] def goToMenuOption(itemId: Int)
    (implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = itemId match {
    case R.id.menu_collections => goToWorkspace(pageCollections)
    case R.id.menu_moments => goToWorkspace(pageWidgets)
    case R.id.menu_profile => showMessage(R.string.todo)
    case R.id.menu_wallpapers => uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER))
    case R.id.menu_android_settings => uiStartIntent(new Intent(android.provider.Settings.ACTION_SETTINGS))
    case R.id.menu_9cards_settings => showMessage(R.string.todo)
    case R.id.menu_widgets => showMessage(R.string.todo)
    case _ => Ui.nop
  }

  private[this] def clickAppDrawerItem(view: View)(implicit context: ActivityContextWrapper): Ui[_] = Ui {
    val position = Int.unbox(view.getTag(R.id.app_drawer_position))
    val card = cardsForAppsDrawer(position)
    execute(card.intent)
  }

  private[this] def getItemsForFabMenu(implicit context: ActivityContextWrapper, theme: NineCardsTheme, managerContext: FragmentManagerContext[Fragment, FragmentManager]) = Seq(
    getUi(w[FabItemMenu] <~ fabButtonCreateCollectionStyle <~ FuncOn.click { view: View =>
      showAction(f[NewCollectionFragment], view, resGetColor(R.color.collection_fab_button_item_create_new_collection))
    }),
    getUi(w[FabItemMenu] <~ fabButtonMyCollectionsStyle <~ FuncOn.click { view: View =>
      showAction(f[PrivateCollectionsFragment], view, resGetColor(R.color.collection_fab_button_item_my_collections))
    }),
    getUi(w[FabItemMenu] <~ fabButtonPublicCollectionStyle <~ On.click {
      uiShortToast("Public Collections")
    })
  )

  private[this] def createPager(activatePosition: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    workspaces map { ws =>
      val pagerViews = 0 until ws.getWorksSpacesCount map { position =>
        val view = pagination(position)
        view.setActivated(activatePosition == position)
        view
      }
      paginationPanel <~ vgAddViews(pagerViews)
    } getOrElse Ui.nop

  private[this] def reloadPagerAndActiveLast(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    workspaces map { ws =>
      val count = ws.getWorksSpacesCount
      val pagerViews = 0 until count map { position =>
        val view = pagination(position)
        view.setActivated(count - 1 == position)
        view
      }
      paginationPanel <~ vgRemoveAllViews <~ vgAddViews(pagerViews)
    } getOrElse Ui.nop

  private[this] def fillAppDrawer(collections: Seq[Collection])(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme) = Transformer {
    case i: ImageView if tagEquals(i, R.id.`type`, LauncherTags.app) => {
      val position = Int.unbox(i.getTag(R.id.app_drawer_position))
      val card = cardsForAppsDrawer(position)
      i <~ ivUri(card.imagePath)
    }
  }

  private[this] def reloadPager(currentPage: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = Transformer {
    case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true) <~~ pagerAppear
    case i: ImageView => i <~ vActivated(false)
  }

  private[this] def pagination(position: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = getUi(
    w[ImageView] <~ paginationItemStyle <~ vTag(position.toString)
  )

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], view: View, color: Int, map: Map[String, String] = Map.empty)
    (implicit context: ActivityContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] = {
    val sizeIconFabMenuItem = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
    val sizeFabButton = fabButton map (_.getWidth) getOrElse 0
    val (startX: Int, startY: Int) = Option(view.findViewById(R.id.fab_icon)) map calculateAnchorViewPosition getOrElse(0, 0)
    val (endX: Int, endY: Int) = fabButton map calculateAnchorViewPosition getOrElse(0, 0)
    val args = new Bundle()
    args.putInt(BaseActionFragment.startRevealPosX, startX + (sizeIconFabMenuItem / 2))
    args.putInt(BaseActionFragment.startRevealPosY, startY + (sizeIconFabMenuItem / 2))
    args.putInt(BaseActionFragment.endRevealPosX, endX + (sizeFabButton / 2))
    args.putInt(BaseActionFragment.endRevealPosY, endY + (sizeFabButton / 2))
    map foreach (item => args.putString(item._1, item._2))
    args.putInt(BaseActionFragment.colorPrimary, color)
    swapFabButton(doUpdateBars = false) ~
      (fragmentContent <~ colorContentDialog(paint = true) <~ fragmentContentStyle(true)) ~
      addFragment(fragmentBuilder.pass(args), Option(R.id.action_fragment_content), Option(nameActionFragment))
  }

  private[this] def prepareBars(implicit context: ActivityContextWrapper) =
    KitKat.ifSupportedThen {
      Ui(getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (content <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (drawerContent <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (actionFragmentContent <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace))
    } getOrElse Ui.nop

}
