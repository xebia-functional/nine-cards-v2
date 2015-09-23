package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.models.AppHeadered._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.TextTab._
import com.fortysevendeg.ninecardslauncher.app.ui.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

trait DrawerComposer
  extends DrawerStyles
  with ContextSupportProvider {

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

  def showDrawerLoading: Ui[_] = (loadingDrawer <~ vVisible) ~
    (recycler <~ vGone) ~
    (scrollerLayout <~ fslInvisible)

  def showDrawerData: Ui[_] = (loadingDrawer <~ vGone) ~
    (recycler <~ vVisible) ~
    (scrollerLayout <~ fslVisible)

  def initDrawerUi(onAppDrawerListener: () => Unit)(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (appDrawerMain <~ drawerAppStyle <~ On.click {
      revealInDrawer ~ Ui {
        onAppDrawerListener()
      }
    }) ~
      (loadingDrawer <~ pbColor(resGetColor(R.color.drawer_toolbar))) ~
      (recycler <~ recyclerStyle) ~
      (scrollerLayout <~ fslColor(resGetColor(R.color.drawer_toolbar))) ~
      (drawerContent <~ vGone) ~
      (drawerTabApp <~
        ttInitTab(R.drawable.app_drawer_icon_applications, R.drawable.app_drawer_icon_applications_inactive) <~
        On.click {
          uiShortToast("App") ~ (drawerTabApp <~ ttSelect) ~ (drawerTabContacts <~ ttUnselect)
        }) ~
      (drawerTabContacts <~
        ttInitTab(R.drawable.app_drawer_icon_contacts, R.drawable.app_drawer_icon_contacts_inactive, false) <~
        On.click {
          uiShortToast("Contacts") ~ (drawerTabContacts <~ ttSelect) ~ (drawerTabApp <~ ttUnselect)
        })

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    updateNavigationToBlack ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealInAppDrawer(source))) ~~
      updateStatusColor(resGetColor(R.color.drawer_toolbar))

  def revealOutDrawer(implicit context: ActivityContextWrapper): Ui[_] =
    ((recycler <~
      rvAdapter(emptyAdapter)) ~
      updateStatusToTransparent ~
      updateNavigationToTransparent) ~
      (appDrawerMain mapUiF (source => drawerContent <~~ revealOutAppDrawer(source)))

  def addApps(apps: Seq[AppCategorized], clickListener: (AppCategorized) => Unit)
    (implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Ui[_] = {
    val adapter = new AppsAdapter(
      apps = generateAppHeaderedList(apps),
      clickListener = clickListener)
    showDrawerData ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      (scrollerLayout <~
        fslLinkRecycler <~
        fslReset)
  }
}
