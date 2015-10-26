package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.new_collection

import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.FullDsl._
import macroid.Ui

trait NewCollectionComposer
  extends Styles {

  self: TypedFindView with BaseActionFragment =>

  def initUi: Ui[_] =
    toolbar <~
      tbTitle(R.string.newCollection) <~
      toolbarStyle(colorPrimary) <~
      tbNavigationOnClickListener((_) => unreveal())

  def showGeneralError: Ui[_] = rootContent <~ uiSnackbarShort(R.string.contactUsError)

}
