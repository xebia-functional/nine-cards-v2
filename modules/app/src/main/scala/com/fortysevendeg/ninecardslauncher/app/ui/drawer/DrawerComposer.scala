package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SystemBarsTint
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.TextTab._
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ContextWrapper, ActivityContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.concurrent.Task

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  implicit val cW: ContextWrapper

  implicit lazy val di: Injector = new Injector

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val drawerTabApp = Option(findView(TR.launcher_drawer_tab_app))

  lazy val drawerTabContacts = Option(findView(TR.launcher_drawer_tab_contact))

  lazy val loadingDrawer = Option(findView(TR.launcher_drawer_loading))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (appDrawerMain <~ drawerAppStyle <~ On.click {
      Ui(loadApps()) ~ revealInDrawer
    }) ~
      (loadingDrawer <~ pbColor(resGetColor(R.color.drawer_toolbar))) ~
      (loadingDrawer <~ vVisible) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(resGetColor(R.color.drawer_toolbar))) ~
      (drawerContent <~ vGone) ~
      (drawerTabApp <~
        ttInitTab(R.string.apps, R.drawable.app_drawer_icon_list_app) <~
        ttSelect <~
        On.click {
          uiShortToast("App") ~ (drawerTabApp <~ ttSelect) ~ (drawerTabContacts <~ ttUnselect)
        }) ~
      (drawerTabContacts <~
        ttInitTab(R.string.contacts, R.drawable.app_drawer_icon_list_contact) <~
        ttUnselect <~
        On.click {
          uiShortToast("Contacts") ~ (drawerTabContacts <~ ttSelect) ~ (drawerTabApp <~ ttUnselect)
        })

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateNavigationToBlack ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source))) ~~
      updateStatusColor(resGetColor(R.color.drawer_toolbar))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateStatusToTransparent ~
      updateNavigationToTransparent ~
      (appDrawerMain mapUi (source => drawerContent <~ revealOutAppDrawer(source)))

  private[this] def loadApps() =
    Task.fork(di.deviceProcess.getCategorizedApps.run).resolveAsyncUi(
      onResult = (apps: Seq[AppCategorized]) => addApps(apps, (app: AppCategorized) => {})
    )

  private[this] def addApps(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)(implicit fragment: Fragment) = {
    val adapter = new AppsAdapter(
      apps = generateAppHeaderedList(apps),
      clickListener = clickListener)
    (recycler <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loadingDrawer <~ vGone) ~
      (scrollerLayout <~ fslLinkRecycler)
  }
}
