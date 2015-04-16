package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.Account
import android.widget._
import com.fortysevendeg.ninecardslauncher.ui.commons.ToolbarLayout
import macroid.FullDsl._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import macroid.{Ui, ActivityContext, AppContext, IdGeneration}


trait Layout
  extends Styles
  with ToolbarLayout
  with IdGeneration {

  var usersGroup = slot[LinearLayout]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      darkToolbar <~ toolbarStyle,
      l[LinearLayout](
        l[RadioGroup]() <~ wire(usersGroup) <~ userGroupStyle,
        w[Button] <~ selectUserButtonStyle
      ) <~ contentUserStyle
    ) <~ rootStyle
  )

  def addUsersToRadioGroup(accounts: Seq[Account])(implicit appContext: AppContext, context: ActivityContext): Ui[_] = {
    val radioViews = accounts map (userRadio(_))
    radioViews lift 0 map (_.setChecked(true))
    usersGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)
  }

  private def userRadio(account: Account)(implicit appContext: AppContext, context: ActivityContext): RadioButton = getUi(
    w[RadioButton] <~ userRadioStyle <~ tvText(account.name)
  )

}
