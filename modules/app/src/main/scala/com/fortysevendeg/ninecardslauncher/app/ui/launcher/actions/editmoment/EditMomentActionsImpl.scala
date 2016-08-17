package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._

trait EditMomentActionsImpl
  extends EditMomentActions {

  self: TypedFindView with BaseActionFragment =>

  override def initialize(): Ui[Any] =
    toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.editMoment) <~
      dtbNavigationOnClickListener((_) => unreveal())

}
