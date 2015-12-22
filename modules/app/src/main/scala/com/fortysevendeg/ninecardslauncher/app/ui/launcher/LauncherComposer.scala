package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.{View, WindowManager}
import com.fortysevendeg.macroid.extras.DeviceVersion.KitKat
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SystemBarsTint
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{AnimatedWorkSpacesListener, LauncherWorkSpaces, LauncherWorkSpacesListener}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection.CollectionsComposer
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.{DrawerComposer, DrawerListeners}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid._

trait LauncherComposer
  extends CollectionsComposer
  with DrawerComposer {

  self: AppCompatActivity with TypedFindView with SystemBarsTint with DrawerListeners =>

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme, managerContext: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] =
    prepareBars ~
      (drawerLayout <~ dlStatusBarBackground(android.R.color.transparent)) ~
      (navigationView <~ nvNavigationItemSelectedListener(itemId => {
        runUi(goToMenuOption(itemId))
        true
      })) ~
      (menuCollectionRoot <~ vGone) ~
      (workspacesContent <~
        vgAddView(getUi(w[LauncherWorkSpaces] <~
          wire(workspaces) <~
          lwsListener(
            LauncherWorkSpacesListener(
              onStartOpenMenu = () => (),
              onUpdateOpenMenu = (percent: Float) => (),
              onEndOpenMenu = (opened: Boolean) => runUi(menuCollectionRoot <~ (if (opened) vVisible else vGone))
            )
          ) <~
          awsListener(AnimatedWorkSpacesListener(
            onLongClick = () => runUi(drawerLayout <~ dlOpenDrawer))
          )))) ~
      (searchPanel <~ searchContentStyle) ~
      (menuAvatar <~ menuAvatarStyle) ~
      (menuCollectionContent <~ vgAddViews(getItemsForFabMenu)) ~
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

  def backByPriority(implicit context: ActivityContextWrapper, manager: FragmentManagerContext[Fragment, FragmentManager]): Ui[_] = if (isMenuVisible) {
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

  def turnOffFragmentContent(implicit activityContextWrapper: ActivityContextWrapper): Ui[_] =
    fragmentContent <~
      fragmentContentStyle(false)

  private[this] def prepareBars(implicit context: ActivityContextWrapper) =
    KitKat.ifSupportedThen {
      Ui(getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (content <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (menuCollectionRoot <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (drawerContent <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (actionFragmentContent <~ vPadding(0, getStatusBarHeight, 0, getNavigationBarHeight)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace))
    } getOrElse Ui.nop

}
