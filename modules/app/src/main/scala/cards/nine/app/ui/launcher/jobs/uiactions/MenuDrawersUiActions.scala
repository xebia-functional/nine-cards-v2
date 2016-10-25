package cards.nine.app.ui.launcher.jobs.uiactions

import android.content.res.ColorStateList
import android.support.design.widget.NavigationView
import android.support.v4.app.{Fragment, FragmentManager}
import android.widget.ImageView
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.drawables.CharDrawable
import cards.nine.app.ui.components.layouts.tweaks.AppsMomentLayoutTweaks._
import cards.nine.app.ui.components.models.LauncherMoment
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.theme.models.{DrawerBackgroundColor, DrawerIconColor, DrawerTextColor, NineCardsTheme}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.NavigationViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

class MenuDrawersUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
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
        }))).toService
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
          case (_, Some(name)) => ivSrc(CharDrawable(name.substring(0, 1).toUpperCase))
          case _ => ivBlank
        }) <~
        menuAvatarStyle) ~
      (dom.menuCover <~
        (maybeCoverUrl match {
          case Some(url) => ivUri(url)
          case None => ivBlank
        }))).toService

  def openMenu(): TaskService[Unit] = (dom.drawerLayout <~ dlOpenDrawer).toService

  def reloadBarMoment(data: LauncherMoment): TaskService[Unit] =
    ((dom.appsMoment <~ amlPopulate(data)) ~ (dom.drawerLayout <~ (data.collection match {
      case Some(_) => dlUnlockedEnd
      case None => dlLockedClosedEnd
    }))).toService

  def openAppsMoment(): TaskService[Unit] =
    (if ((dom.drawerLayout ~> dlIsLockedClosedDrawerEnd).get) {
      Ui.nop
    } else {
      dom.drawerLayout <~ dlOpenDrawerEnd
    }).toService

  def closeAppsMoment(): TaskService[Unit] = (dom.drawerLayout <~ dlCloseDrawerEnd).toService

  def close(): TaskService[Unit] = closeMenu().toService

  private[this] def closeMenu(): Ui[Any] = dom.drawerLayout <~ dlCloseDrawer

  def showNoImplementedYetMessage(): Ui[Any] = dom.workspaces <~ vLauncherSnackbar(R.string.todo)

  // Styles

  private[this] def navigationViewStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[NavigationView] =
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
