package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.Intent
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonBehaviour
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FabItemMenu
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

trait LauncherComposer
  extends Styles
  with FabButtonBehaviour {

  self: AppCompatActivity with TypedFindView =>

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  var workspaces: Option[LauncherWorkSpaces] = None

  lazy val loading = Option(findView(TR.launcher_loading))

  lazy val content = Option(findView(TR.launcher_content))

  lazy val workspacesContent = Option(findView(TR.launcher_work_spaces_content))

  lazy val appDrawerPanel = Option(findView(TR.launcher_drawer_panel))

  lazy val appDrawer1 = Option(findView(TR.launcher_page_1))

  lazy val appDrawer2 = Option(findView(TR.launcher_page_2))

  lazy val appDrawer3 = Option(findView(TR.launcher_page_3))

  lazy val appDrawer4 = Option(findView(TR.launcher_page_4))

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val paginationPanel = Option(findView(TR.launcher_pagination_panel))

  lazy val searchPanel = Option(findView(TR.launcher_search_panel))

  lazy val burgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val googleIcon = Option(findView(TR.launcher_google_icon))

  lazy val micIcon = Option(findView(TR.launcher_mic_icon))

  lazy val systemBarTintManager = new SystemBarTintManager(this)

  def showLoading(implicit context: ActivityContextWrapper): Ui[_] = loading <~ vVisible

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (workspacesContent <~
      vgAddView(getUi(w[LauncherWorkSpaces] <~
        wire(workspaces) <~
        Tweak[LauncherWorkSpaces](_.startScroll = (toRight: Boolean) => {
          val goToWizardScreen = workspaces exists (_.goToWizardScreen(toRight))
          val collectionScreen = workspaces exists (_.isCollectionScreen)
          if (collectionScreen && !goToWizardScreen) showFabButton
        })))) ~
      (searchPanel <~ searchContentStyle) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      (burgerIcon <~ burgerButtonStyle <~ On.click(
        uiShortToast("Open Menu")
      )) ~
      (googleIcon <~ googleButtonStyle <~ On.click(
        uiStartIntent(new Intent(Intent.ACTION_WEB_SEARCH))
      )) ~
      (micIcon <~ micButtonStyle <~ On.click(
        uiStartIntent(new Intent(RecognizerIntent.ACTION_WEB_SEARCH))
      )) ~
      (appDrawer1 <~ drawerItemStyle <~ On.click {
        uiShortToast("App 1")
      }) ~
      (appDrawer2 <~ drawerItemStyle <~ On.click {
        uiShortToast("App 2")
      }) ~
      (appDrawer3 <~ drawerItemStyle <~ On.click {
        uiShortToast("App 3")
      }) ~
      (appDrawer4 <~ drawerItemStyle <~ On.click {
        uiShortToast("App 4")
      }) ~
      (appDrawerMain <~ drawerAppStyle <~ On.click {
        revealInDrawer
      }) ~
      (drawerContent <~ vGone)

  def createCollections(collections: Seq[Collection])(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
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
    workspaces map {
      ws =>
        val pagerViews = 0 until ws.getWorksSpacesCount map { position =>
          val view = pagination(position)
          view.setActivated(activatePosition == position)
          view
        }
        paginationPanel <~ vgAddViews(pagerViews)
    } getOrElse Ui.nop


  // TODO We add app randomly, in the future we should get the app from repository
  private[this] def fillAppDrawer(collections: Seq[Collection])(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = Transformer {
    case i: ImageView if tagEquals(i, R.id.`type`, LauncherTags.app) => {
      val r = scala.util.Random
      val randomCollection = collections(r.nextInt(collections.length))
      val randomCard = randomCollection.cards(r.nextInt(randomCollection.cards.length))
      i <~ ivUri(randomCard.imagePath)
    }
  }

  private[this] def reloadPager(currentPage: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = Transformer {
    case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true) <~~ pagerAppear
    case i: ImageView => i <~ vActivated(false)
  }

  private[this] def pagination(position: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = getUi(
    w[ImageView] <~ paginationItemStyle <~ vTag(position.toString)
  )

  class RunnableWrapper(implicit context: ActivityContextWrapper) extends Runnable {
    override def run(): Unit = runUi(fabButton <~ hideFabMenu)
  }

  private[this] def updateStatusColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        getWindow.setStatusBarColor(color)
      } getOrElse {
        systemBarTintManager.setStatusBarTintColor(color)
      }
    }

  private[this] def updateNavigationColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        getWindow.setNavigationBarColor(color)
      } getOrElse {
        systemBarTintManager.setNavigationBarTintColor(color)
      }
    }

  def isDrawerVisible = drawerContent map (_.getVisibility) match {
    case Some(View.VISIBLE) => true
    case _ => false
  }

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateNavigationColor(resGetColor(android.R.color.black)) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source))) ~~
      updateStatusColor(resGetColor(R.color.drawer_toolbar))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateStatusColor(resGetColor(android.R.color.transparent)) ~
      updateNavigationColor(resGetColor(android.R.color.transparent)) ~
      (appDrawerMain mapUi (source => drawerContent <~ revealOutAppDrawer(source)))

}
