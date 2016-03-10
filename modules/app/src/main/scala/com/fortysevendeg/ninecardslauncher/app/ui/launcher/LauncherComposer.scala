package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.fortysevendeg.macroid.extras.DeviceVersion.{KitKat, Lollipop}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SystemBarsTint
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.collection.CollectionsComposer
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.{DrawerComposer, DrawerListeners}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

trait LauncherComposer
  extends CollectionsComposer
  with DrawerComposer {

  self: AppCompatActivity with TypedFindView with SystemBarsTint with DrawerListeners =>

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme, managerContext: FragmentManagerContext[Fragment, FragmentManager]) =
    prepareBars ~ initCollectionsUi ~ initDrawerUi

  def backByPriority(implicit context: ActivityContextWrapper, manager: FragmentManagerContext[Fragment, FragmentManager], theme: NineCardsTheme): Ui[_] =
    if (isTabsOpened) {
      closeTabs
    } else if (isMenuVisible) {
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
          vPadding(paddingDefault, paddingDefault + sbHeight, paddingDefault, paddingDefault + nbHeight)) ~
        (drawerLayout <~ vBackground(R.drawable.background_workspace)) ~
        (Lollipop.ifSupportedThen {
          actionFragmentContent <~ vElevation(elevation)
        } getOrElse Ui.nop)
    } getOrElse Ui.nop

}
