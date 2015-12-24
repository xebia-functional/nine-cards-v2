package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.graphics.Color
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.{View, WindowManager}
import com.fortysevendeg.macroid.extras.DeviceVersion.KitKat
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ColorsUtils, SystemBarsTint}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{AnimatedWorkSpacesListener, LauncherWorkSpaces, LauncherWorkSpacesListener}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Snails._
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

  val maxBackgroundPercent: Float = 0.4f

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
              onStartOpenMenu = startOpenMenu,
              onUpdateOpenMenu = updateOpenMenu,
              onEndOpenMenu = closeMenu
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
    fragmentContent <~ vClickable(false)

  private[this] def startOpenMenu()(implicit activityContextWrapper: ActivityContextWrapper): Ui[_] =
    (menuCollectionRoot <~ vVisible <~ vClearClick) ~
      (appDrawerPanel <~ fade(out = true)) ~
      (paginationPanel <~ fade(out = true)) ~
      (searchPanel <~ fade(out = true))

  private[this] def updateOpenMenu(percent: Float): Ui[_] = {
    val backgroundPercent = maxBackgroundPercent * percent
    val colorBackground = ColorsUtils.setAlpha(Color.BLACK, backgroundPercent)
    val height = (menuCollectionContent map (_.getHeight) getOrElse 0) + getNavigationBarHeight
    val translate = height - (height * percent)
    (menuCollectionRoot <~ vBackgroundColor(colorBackground)) ~
      (menuCollectionContent <~ vTranslationY(translate))
  }

  private[this] def closeMenu(opened: Boolean)(implicit activityContextWrapper: ActivityContextWrapper): Ui[_] =
    if (opened) {
      menuCollectionRoot <~ On.click(closeCollectionMenu())
    } else {
      (appDrawerPanel <~ fade()) ~
        (paginationPanel <~ fade()) ~
        (searchPanel <~ fade()) ~
        (menuCollectionRoot <~ vGone)
    }

  private[this] def prepareBars(implicit context: ActivityContextWrapper) =
    KitKat.ifSupportedThen {
      val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)
      val sbHeight = getStatusBarHeight
      val nbHeight = getNavigationBarHeight
      val elevation = resGetDimensionPixelSize(R.dimen.elevation_fab_button)
      Ui(getWindow.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)) ~
        (content <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (menuCollectionRoot <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (drawerContent <~ vPadding(0, sbHeight, 0, nbHeight)) ~
        (actionFragmentContent <~
          vPadding(paddingDefault, paddingDefault + sbHeight, paddingDefault, paddingDefault + nbHeight) <~
          vElevation(elevation)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace))
    } getOrElse Ui.nop

}
