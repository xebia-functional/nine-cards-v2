package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.{Context, Intent}
import android.speech.RecognizerIntent
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FabButtonBehaviour, LauncherExecutor, SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{AnimatedWorkSpacesListener, FabItemMenu}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
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
  with FabButtonBehaviour
  with LauncherExecutor {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  // TODO For now, we always use 4 applications in app drawer panel
  lazy val packagesForAppsDrawer = Seq(
    ("com.google.android.talk", "com.google.android.talk.SigningInActivity"),
    ("com.google.android.apps.inbox", "com.google.android.apps.bigtop.activities.InitActivity"),
    ("com.android.chrome", "com.google.android.apps.chrome.Main"),
    ("com.google.android.GoogleCamera", "com.android.camera.CameraLauncher")
  )

  lazy val cardsForAppsDrawer = packagesForAppsDrawer map { app =>
    val (packageName, className) = app
    val dirPath = getDir(getResources.getString(R.string.icons_apps_folder), Context.MODE_PRIVATE).getPath
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
  }

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

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

  def showMessage(message: Int): Ui[_] = drawerLayout <~ uiSnackbarShort(message)

  def updateBarsInFabMenuShow: Ui[_] = {
    val color = getResources.getColor(R.color.background_dialog)
    updateNavigationColor(color) ~
      updateStatusColor(color)
  }

  def updateBarsInFabMenuHide: Ui[_] = updateNavigationToTransparent ~ updateStatusToTransparent

  def showLoading(implicit context: ActivityContextWrapper): Ui[_] = loading <~ vVisible

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (drawerLayout <~ dlStatusBarBackground(android.R.color.transparent)) ~
      (navigationView <~ nvNavigationItemSelectedListener(itemId => {
        itemId match {
          case R.id.menu_collections =>
            runUi((workspaces <~ lwsSelect(1)) ~ closeMenu())
          case R.id.menu_moments =>
            runUi((workspaces <~ lwsSelect(0)) ~ closeMenu())
          case R.id.menu_profile =>
            runUi(showMessage(R.string.todo))
          case R.id.menu_wallpapers =>
            runUi(uiStartIntent(new Intent(Intent.ACTION_SET_WALLPAPER)))
          case R.id.menu_android_settings =>
            runUi(uiStartIntent(new Intent(android.provider.Settings.ACTION_SETTINGS)))
          case R.id.menu_9cards_settings =>
            runUi(showMessage(R.string.todo))
          case R.id.menu_widgets =>
            runUi(showMessage(R.string.todo))
        }
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
            }
          ))))) ~
      (searchPanel <~ searchContentStyle) ~
      (menuAvatar <~ menuAvatarStyle) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      (burgerIcon <~ burgerButtonStyle <~ On.click(
        drawerLayout <~ dlOpenDrawer
      )) ~
      (googleIcon <~ googleButtonStyle <~ On.click(
        uiStartIntent(new Intent(Intent.ACTION_WEB_SEARCH))
      )) ~
      (micIcon <~ micButtonStyle <~ On.click(
        uiStartIntent(new Intent(RecognizerIntent.ACTION_WEB_SEARCH))
      )) ~
      (appDrawer1 <~ drawerItemStyle <~ vIntTag(R.id.app_drawer_position, 0) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer2 <~ drawerItemStyle <~ vIntTag(R.id.app_drawer_position, 1) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer3 <~ drawerItemStyle <~ vIntTag(R.id.app_drawer_position, 2) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      }) ~
      (appDrawer4 <~ drawerItemStyle <~ vIntTag(R.id.app_drawer_position, 3) <~ FuncOn.click { view: View =>
        clickAppDrawerItem(view)
      })

  def createCollections(
    collections: Seq[Collection])
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme): Ui[_] =
    (loading <~ vGone) ~
      (workspaces <~
        lwsData(collections, selectedPageDefault) <~
        lwsAddPageChangedObserver(currentPage => {
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

  def closeMenu(): Ui[_] = drawerLayout <~ dlCloseDrawer

  def isMenuVisible: Boolean = drawerLayout exists (_.isDrawerOpen(GravityCompat.START))

  private[this] def clickAppDrawerItem(view: View)(implicit context: ActivityContextWrapper): Ui[_] = Ui {
    val position = Int.unbox(view.getTag(R.id.app_drawer_position))
    val card = cardsForAppsDrawer(position)
    execute(card.intent)
  }

  private[this] def getItemsForFabMenu(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = Seq(
    getUi(w[FabItemMenu] <~ fabButtonCreateCollectionStyle <~ On.click {
      uiShortToast("Create Collection")
    }),
    getUi(w[FabItemMenu] <~ fabButtonMyCollectionsStyle <~ On.click {
      uiShortToast("My Collections")
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

}
