/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.launcher.jobs.uiactions

import android.content.res.ColorStateList
import android.support.design.widget.NavigationView
import android.support.v4.app.{Fragment, FragmentManager}
import android.widget.ImageView
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.drawables.CharDrawable
import cards.nine.app.ui.components.layouts.tweaks.AppsMomentLayoutTweaks._
import cards.nine.app.ui.components.models.LauncherMoment
import cards.nine.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor}
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.ImageViewTweaks._
import macroid.extras.NavigationViewTweaks._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.FullDsl._

class MenuDrawersUiActions(val dom: LauncherDOM)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit lazy val systemBarsTint = new SystemBarsTint

  lazy val navigationJobs = createNavigationJobs

  implicit def theme: NineCardsTheme = statuses.theme

  def initialize(): TaskService[Unit] = {
    ((dom.drawerLayout <~ dlStatusBarBackground(R.color.primary)) ~
      (dom.navigationView <~
        navigationViewStyle <~
        nvNavigationItemSelectedListener(itemId => {
          navigationJobs.goToMenuOption(itemId).resolveAsync()
          closeMenu().run
          true
        }))).toService()
  }

  def loadUserProfileMenu(
      maybeEmail: Option[String],
      maybeName: Option[String],
      maybeAvatarUrl: Option[String],
      maybeCoverUrl: Option[String]): TaskService[Unit] =
    ((dom.menuName <~ tvText(maybeName.getOrElse(""))) ~
      (dom.menuEmail <~ tvText(maybeEmail.getOrElse(""))) ~
      (dom.menuAvatar <~
        ((maybeAvatarUrl, maybeName) match {
          case (Some(url), _) => ivUri(url)
          case (_, Some(name)) =>
            ivSrc(CharDrawable(name.substring(0, 1).toUpperCase))
          case _ => ivBlank
        }) <~
        menuAvatarStyle) ~
      (dom.menuHeader <~ On.click {
        closeMenu() ~ Ui(navigationJobs.navigationUiActions.goToProfile().resolveAsync())
      }) ~
      (dom.menuCover <~
        (maybeCoverUrl match {
          case Some(url) => ivUri(url)
          case None      => ivBlank
        }))).toService(Option("loadUserProfileMenu"))

  def openMenu(): TaskService[Unit] =
    (dom.drawerLayout <~ dlOpenDrawer).toService()

  def reloadBarMoment(data: LauncherMoment): TaskService[Unit] =
    ((dom.workspaces <~ lwsReloadMomentCollection(data.collection)) ~
      (dom.appsMoment <~ amlPopulate(data)) ~
      (dom.drawerLayout <~ (data.collection match {
        case Some(_) => dlUnlockedEnd
        case None    => dlLockedClosedEnd
      }))).toService(Option("reloadBarMoment"))

  def openAppsMoment(): TaskService[Unit] =
    (if ((dom.drawerLayout ~> dlIsLockedClosedDrawerEnd).get) {
       Ui.nop
     } else {
       dom.drawerLayout <~ dlOpenDrawerEnd
     }).toService()

  def closeAppsMoment(): TaskService[Unit] =
    (dom.drawerLayout <~ dlCloseDrawerEnd).toService()

  def close(): TaskService[Unit] = closeMenu().toService()

  private[this] def closeMenu(): Ui[Any] = dom.drawerLayout <~ dlCloseDrawer

  // Styles

  private[this] def navigationViewStyle(
      implicit context: ContextWrapper,
      theme: NineCardsTheme): Tweak[NavigationView] =
    Tweak[NavigationView] { view =>
      view.setBackgroundColor(theme.get(DrawerBackgroundColor))
      view.setItemTextColor(ColorStateList.valueOf(theme.get(DrawerTextColor)))
      view.setItemIconTintList(ColorStateList.valueOf(theme.get(DrawerIconColor)))
    }

  private[this] def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

}
