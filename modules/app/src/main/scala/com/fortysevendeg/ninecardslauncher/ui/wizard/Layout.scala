package com.fortysevendeg.ninecardslauncher.ui.wizard

import android.accounts.Account
import android.os.Build
import android.widget._
import com.fortysevendeg.ninecardslauncher.services.api.models.UserConfigDevice
import com.fortysevendeg.ninecardslauncher.ui.commons.ToolbarLayout
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid._

trait Layout
  extends Styles
  with ToolbarLayout
  with IdGeneration {

  val NewConfigurationKey = "new_configuration"

  var loadingRootLayout = slot[LinearLayout]

  var userRootLayout = slot[LinearLayout]

  var usersGroup = slot[RadioGroup]

  var userAction = slot[Button]

  var titleDevice = slot[TextView]

  var deviceRootLayout = slot[LinearLayout]

  var devicesGroup = slot[RadioGroup]

  var deviceAction = slot[Button]

  var finishAction = slot[Button]

  var wizardRootLayout = slot[LinearLayout]

  def layout(implicit context: ActivityContextWrapper) = getUi(
    l[FrameLayout](
      l[LinearLayout](
        w[ProgressBar],
        w[TextView] <~ defaultTextStyle <~ tvText(R.string.loading)
      ) <~ loadingRootStyle <~ wire(loadingRootLayout) <~ vGone,
      l[LinearLayout](
        w[TextView] <~ titleTextStyle <~ tvText(R.string.welcome),
        w[TextView] <~ defaultTextStyle <~ tvText(R.string.welcomeMessage),
        l[RadioGroup]() <~ wire(usersGroup) <~ groupStyle,
        w[Button] <~ actionButtonStyle <~ wire(userAction) <~ tvText(R.string.buttonContinue)
      ) <~ contentStyle <~ wire(userRootLayout),
      l[LinearLayout](
        w[TextView] <~ titleTextStyle <~ wire(titleDevice),
        w[TextView] <~ defaultTextStyle <~ tvText(R.string.addDeviceMessage),
        l[RadioGroup]() <~ wire(devicesGroup) <~ groupStyle,
        w[Button] <~ actionButtonStyle <~ wire(deviceAction) <~ tvText(R.string.buttonContinue)
      ) <~ contentStyle <~ wire(deviceRootLayout) <~ vGone,
      l[LinearLayout](
        w[TextView] <~ endMessageStyle <~ tvText("We are working in Wizard. Please, you should wait that the icon disappear in Notification Bar before to press the button"),
        w[Button] <~ actionButtonStyle <~ wire(finishAction) <~ tvText(R.string.goTo9Cards)
      ) <~ contentStyle <~ wire(wizardRootLayout) <~ vGone
    ) <~ rootStyle
  )

  def addUsersToRadioGroup(accounts: Seq[Account])(implicit context: ActivityContextWrapper): Ui[_] = {
    val radioViews = accounts map (account => userRadio(account.name, account.name))
    (usersGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui { radioViews lift 0 foreach (_.setChecked(true)) }
  }

  def addDevicesToRadioGroup(devices: Seq[UserConfigDevice])(implicit context: ActivityContextWrapper): Ui[_] = {
    val radioViews = (devices map (account => userRadio(account.deviceName, account.deviceId))) :+
      userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), NewConfigurationKey)
    (devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui { radioViews lift 0 foreach (_.setChecked(true)) }
  }

  private def userRadio(title: String, tag: String)(implicit context: ActivityContextWrapper): RadioButton = getUi(
    w[RadioButton] <~ radioStyle <~ tvText(title) <~ vTag(tag)
  )

}
