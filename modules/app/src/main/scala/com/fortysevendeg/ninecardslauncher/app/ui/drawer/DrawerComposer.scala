package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SystemBarsTint
import com.fortysevendeg.ninecardslauncher.app.ui.components.TextTab._
import DrawerSnails._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.{ActivityContextWrapper, Ui}
import macroid.FullDsl._

trait DrawerComposer
  extends DrawerStyles {

  self: AppCompatActivity with TypedFindView with SystemBarsTint =>

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val drawerTabApp = Option(findView(TR.launcher_drawer_tab_app))

  lazy val drawerTabContacts = Option(findView(TR.launcher_drawer_tab_contact))

  def initDrawerUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme): Ui[_] =
    (appDrawerMain <~ drawerAppStyle <~ On.click {
      revealInDrawer
    }) ~
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

}
