package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks

import android.support.v7.widget.RecyclerView
import android.view.animation.AnimationUtils
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.snails.RippleBackgroundSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.{AppsMenuOption, ContactsMenuOption}
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

object TintableImageViewTweaks {
  type W = TintableImageView

  def tivDefaultColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { view =>
    view.defaultColor = color
    view.setTint(color)
  }

  def tivPressedColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.pressedColor = color)

}

object TintableButtonTweaks {
  type W = TintableButton

  def tbDefaultColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { view =>
    view.defaultColor = color
    view.setTint(color)
  }

  def tbPressedColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.pressedColor = color)

  def tbResetColor()(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.setDefaultColor())

}

object RippleBackgroundViewTweaks {

  def rbvColor(color: Int, forceFade: Boolean = false)(implicit contextWrapper: ContextWrapper) = Tweak[RippleBackgroundView] { view =>
    (view <~~ ripple(color, forceFade)).run
  }

}

object CollectionRecyclerViewTweaks {
  type W = CollectionRecyclerView

  def nrvRegisterScroll(register: Boolean) = Tweak[W]( view => view.statuses = view.statuses.copy(registerScroll = register))

  def nrvDisableScroll(disable: Boolean) = Tweak[W]( view => view.statuses = view.statuses.copy(disableScroll = disable))

  def nrvEnableAnimation(res: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    view.statuses = view.statuses.copy(enableAnimation = true)
    view.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(contextWrapper.application, res))
  }

  def nrvScheduleLayoutAnimation = Tweak[W](_.scheduleLayoutAnimation())

  def nrvResetScroll(y: Int) = Tweak[W] { rv =>
    rv.scrollListener.foreach(_.scrollY = y)
  }

  def nrvCollectionScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit
  )(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.createScrollListener(scrolled, scrollStateChanged))

}

object DrawerRecyclerViewTweaks {
  type W = DrawerRecyclerView

  def drvSetType(option: AppsMenuOption) = Tweak[W] { view =>
    if (view.statuses.contentView == AppsView) {
      view.statuses = view.statuses.copy(lastTimeContentViewWasChanged = false)
    } else {
      view.statuses = view.statuses.copy(contentView = AppsView, lastTimeContentViewWasChanged = true)
    }
    (view <~ vSetType(option.name)).run
  }

  def drvSetType(option: ContactsMenuOption) = Tweak[W] { view =>
    if (view.statuses.contentView == ContactView) {
      view.statuses = view.statuses.copy(lastTimeContentViewWasChanged = false)
    } else {
      view.statuses = view.statuses.copy(contentView = ContactView, lastTimeContentViewWasChanged = true)
    }
    (view <~ vSetType(option.name)).run
  }

  def drvListener(listener: DrawerRecyclerViewListener) = Tweak[W](_.drawerRecyclerListener = listener)

}
