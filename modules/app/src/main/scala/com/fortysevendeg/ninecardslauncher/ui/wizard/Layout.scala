package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.Account
import android.widget._
import com.fortysevendeg.ninecardslauncher.ui.commons.ToolbarLayout
import macroid.FullDsl._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import macroid._

trait Layout
  extends Styles
  with ToolbarLayout
  with IdGeneration {

  var usersGroup = slot[RadioGroup]

  var action = slot[Button]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      darkToolbar <~ toolbarStyle,
      l[LinearLayout](
        w[TextView] <~ welcomeTextStyle,
        l[RadioGroup]() <~ wire(usersGroup) <~ userGroupStyle,
        w[Button] <~ selectUserButtonStyle <~ wire(action)
      ) <~ contentUserStyle
    ) <~ rootStyle
  )

  def addUsersToRadioGroup(accounts: Seq[Account])(implicit appContext: AppContext, context: ActivityContext): Ui[_] = {
    val radioViews = accounts map userRadio
    (usersGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui { radioViews lift 0 foreach (_.setChecked(true)) }
  }

  private def userRadio(account: Account)(implicit appContext: AppContext, context: ActivityContext): RadioButton = getUi(
    w[RadioButton] <~ userRadioStyle <~ tvText(account.name)
  )

}
