package cards.nine.app.ui.components.widgets.tweaks

import android.graphics.Color
import android.view.animation.AnimationUtils
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.components.widgets._
import cards.nine.app.ui.launcher.types.{AppsMenuOption, ContactsMenuOption}
import cards.nine.models.{NineCardsTheme, PackagesByCategory}
import cards.nine.models.types.NineCardsMoment
import macroid._

object TintableImageViewTweaks {
  type W = TintableImageView

  def tivDefaultColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { view =>
    view.defaultColor = color
    view.setTint(color)
  }

  def tivPressedColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](_.pressedColor = color)

  def tivClean(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { view =>
    view.defaultColor = Color.WHITE
    view.pressedColor = Color.WHITE
    view.setTint(Color.WHITE)
  }

  def tivColor(color: Int)(implicit context: ContextWrapper): Tweak[W] = Tweak[W] { view =>
    view.defaultColor = color
    view.pressedColor = color
    view.setTint(color)
  }

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

object CollectionRecyclerViewTweaks {
  type W = CollectionRecyclerView

  def nrvDisableScroll(disable: Boolean) = Tweak[W]( view => view.statuses = view.statuses.copy(disableScroll = disable))

  def nrvEnableAnimation(res: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    view.statuses = view.statuses.copy(enableAnimation = true)
    view.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(contextWrapper.application, res))
  }

  def nrvScheduleLayoutAnimation = Tweak[W](_.scheduleLayoutAnimation())

}

object DrawerRecyclerViewTweaks {
  type W = DrawerRecyclerView

  def drvSetType(option: AppsMenuOption) = Tweak[W] { view =>
    view.statuses = view.statuses.copy(contentView = AppsView)
    (view <~ vSetType(option.name)).run
  }

  def drvSetType(option: ContactsMenuOption) = Tweak[W] { view =>
    view.statuses = view.statuses.copy(contentView = ContactView)
    (view <~ vSetType(option.name)).run
  }

}

object WizardCheckBoxTweaks {
  type W = WizardCheckBox

  def wcbInitialize(resText: Int, defaultCheck: Boolean = true) = Tweak[W] (_.initialize(resText, defaultCheck).run)

  def wcbInitializeCollection(packagesByCategory: PackagesByCategory, defaultCheck: Boolean = true) =
    Tweak[W] (_.initializeCollection(packagesByCategory, defaultCheck).run)

  def wcbDoCheck(doCheck: Boolean) = Tweak[W] { view => (if (doCheck) view.check() else view.uncheck()).run }

  def wcbCheck() = Tweak[W] (_.check().run)

  def wcbUncheck() = Tweak[W] (_.uncheck().run)

  def wcbSwap() = Tweak[W] (_.swap().run)

  def wcbBest9(filter9: Boolean) = Tweak[W] (_.setBest9(filter9).run)

}

object WizardWifiCheckBoxTweaks {
  type W = WizardWifiCheckBox

  def wwcbInitialize(moment: NineCardsMoment, onWifiClick: () => Unit, defaultCheck: Boolean = true) =
    Tweak[W](_.initialize(moment, onWifiClick, defaultCheck).run)

  def wwcbDoCheck(doCheck: Boolean) = Tweak[W] { view => (if (doCheck) view.check() else view.uncheck()).run }

  def wwcbCheck() = Tweak[W] (_.check().run)

  def wwcbUncheck() = Tweak[W] (_.uncheck().run)

  def wwcbSwap() = Tweak[W] (_.swap().run)

  def wwcbWifiName(wifi: String) = Tweak[W] (_.setWifiName(wifi).run)
}

object WizardMomentCheckBoxTweaks {
  type W = WizardMomentCheckBox

  def wmcbInitialize(moment: NineCardsMoment, defaultCheck: Boolean = true) =
    Tweak[W](_.initialize(moment, defaultCheck).run)

  def wmcbDoCheck(doCheck: Boolean) = Tweak[W] { view => (if (doCheck) view.check() else view.uncheck()).run }

  def wmcbCheck() = Tweak[W] (_.check().run)

  def wmcbUncheck() = Tweak[W] (_.uncheck().run)

  def wmcbSwap() = Tweak[W] (_.swap().run)

}

object CollectionCheckBoxTweaks {
  type W = CollectionCheckBox

  def ccbInitialize(collectionIcon: Int, color: Int, theme: NineCardsTheme, defaultCheck: Boolean = true) =
    Tweak[W](_.initialize(collectionIcon, color, theme, defaultCheck).run)

}