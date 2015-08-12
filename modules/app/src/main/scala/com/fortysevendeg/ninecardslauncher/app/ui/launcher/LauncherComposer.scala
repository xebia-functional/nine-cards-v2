package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageActivityTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Transformer, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

trait LauncherComposer
  extends Styles {

  self: TypedFindView =>

  // TODO We select the page in ViewPager with collections. In the future this will be a user preference
  val selectedPageDefault = 1

  var workspaces: Option[LauncherWorkSpaces] = None

  lazy val loading = Option(findView(TR.launcher_loading))

  lazy val content = Option(findView(TR.launcher_content))

  lazy val workspacesContent = Option(findView(TR.launcher_work_spaces_content))

  lazy val appDrawerPanel = Option(findView(TR.launcher_drawer_panel))

  lazy val fabMenuContent = Option(findView(TR.launcher_menu_content))

  lazy val fabButton = Option(findView(TR.launcher_fab_button))

  lazy val appDrawer1 = Option(findView(TR.launcher_page_1))

  lazy val appDrawer2 = Option(findView(TR.launcher_page_2))

  lazy val appDrawer3 = Option(findView(TR.launcher_page_3))

  lazy val appDrawer4 = Option(findView(TR.launcher_page_4))

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val paginationPanel = Option(findView(TR.launcher_pagination_panel))

  lazy val searchPanel = Option(findView(TR.launcher_search_panel))

  lazy val burgerIcon = Option(findView(TR.launcher_burger_icon))

  lazy val googleIcon = Option(findView(TR.launcher_google_icon))

  lazy val micIcon = Option(findView(TR.launcher_mic_icon))

  def showLoading(implicit context: ActivityContextWrapper): Ui[_] = loading <~ vVisible

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] = {
    val iconFabButton = new PathMorphDrawable(
      defaultIcon = IconTypes.ADD,
      defaultStroke = resGetDimensionPixelSize(R.dimen.default_stroke))
    (workspacesContent <~ vgAddView(getUi(w[LauncherWorkSpaces] <~ wire(workspaces)))) ~
      (searchPanel <~ searchContentStyle) ~
      (fabButton <~ ivSrc(iconFabButton)) ~
      (burgerIcon <~ burgerButtonStyle <~ On.click(
        uiShortToast("Open Menu")
      )) ~
      (googleIcon <~ googleButtonStyle <~ On.click(
        Ui {
          Try {
            val intent = new Intent(Intent.ACTION_WEB_SEARCH)
            context.getOriginal.startActivity(intent)
          }
        }
      )) ~
      (micIcon <~ micButtonStyle <~ On.click(
        Ui {
          Try {
            val intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH)
            context.getOriginal.startActivity(intent)
          }
        }
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
        uiShortToast("App Drawer")
      })
  }

  def createCollections(collections: Seq[Collection])(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (loading <~ vGone) ~
      (workspaces <~
        lwsData(collections, selectedPageDefault) <~
        lwsAddPageChangedObserver(currentPage => runUi(paginationPanel <~ reloadPager(currentPage)))) ~
      (appDrawerPanel <~ fillAppDrawer(collections)) ~
      createPager(selectedPageDefault)

  private[this] def createPager(activatePosition: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = workspaces map {
    ws =>
      val pagerViews = 0 until ws.getWorksSpacesCount map {
        position =>
          val view = pagination(position)
          view.setActivated(activatePosition == position)
          view
      }
      paginationPanel <~ vgAddViews(pagerViews)
  } getOrElse Ui.nop

  // TODO We add app randomly, in the future we should get the app from repository
  private[this] def fillAppDrawer(collections: Seq[Collection])(implicit context: ActivityContextWrapper, theme: NineCardsTheme) = Transformer {
    case i: ImageView if Option(i.getTag(R.id.`type`)).isDefined && i.getTag(R.id.`type`).equals(AppDrawer.app) => {
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

}
