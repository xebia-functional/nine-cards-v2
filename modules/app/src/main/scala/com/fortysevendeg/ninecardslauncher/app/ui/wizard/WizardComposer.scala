package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.os.Build
import android.widget._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserDevice, UserInfo}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.StepsWorkspacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.RippleBackgroundViewTweaks._
import macroid._

trait WizardComposer {

  self: TypedFindView =>

  val newConfigurationKey = "new_configuration"

  def createSteps(implicit activityContextWrapper: ActivityContextWrapper) = Seq(
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_1)),
    StepData(R.drawable.wizard_02, resGetString(R.string.wizard_step_2)),
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_3)),
    StepData(R.drawable.wizard_04, resGetString(R.string.wizard_step_4)),
    StepData(R.drawable.wizard_05, resGetString(R.string.wizard_step_5))
  )

  lazy val rootLayout = Option(findView(TR.wizard_root))

  lazy val loadingRootLayout = Option(findView(TR.wizard_loading_content))

  lazy val userRootLayout = Option(findView(TR.wizard_user_content))

  lazy val usersGroup = Option(findView(TR.wizard_user_group))

  lazy val usersTerms = Option(findView(TR.wizard_user_terms))

  lazy val userAction = Option(findView(TR.wizard_user_action))

  lazy val titleDevice = Option(findView(TR.wizard_device_title))

  lazy val deviceRootLayout = Option(findView(TR.wizard_device_content))

  lazy val devicesGroup = Option(findView(TR.wizard_device_group))

  lazy val deviceAction = Option(findView(TR.wizard_device_action))

  lazy val stepsAction = Option(findView(TR.wizard_steps_action))

  lazy val wizardRootLayout = Option(findView(TR.wizard_steps_content))

  lazy val wizardWorkspaceContent = Option(findView(TR.wizard_steps_workspace_content))

  var workspaces: Option[StepsWorkspaces] = None

  def showMessage(message: Int): Ui[_] = rootLayout <~ uiSnackbarShort(message)

  def initUi(
    accounts: Seq[Account],
    requestToken: (String) => Unit,
    launchService: (Option[String]) => Unit,
    finishUi: Ui[_]
  )(implicit context: ActivityContextWrapper): Ui[_] = {
    val steps = createSteps
    addUsersToRadioGroup(accounts) ~
      (userAction <~
        vBackgroundTint(resGetColor(R.color.primary)) <~
        On.click {
          usersGroup map { view =>
            val username = view.getSelectedItem.toString
            requestToken(username)
            showLoading
          } getOrElse showMessage(R.string.errorSelectUser)
        }) ~
      (deviceAction <~
        vBackgroundTint(resGetColor(R.color.primary)) <~
        On.click {
          devicesGroup <~ Transformer {
            case i: RadioButton if i.isChecked =>
              val tag = i.getTag.toString
              if (tag == newConfigurationKey) {
                launchService(None)
              } else {
                launchService(Option(tag))
              }
              showWizard
          }
        }) ~
      (wizardWorkspaceContent <~
        vgAddView(getUi(w[StepsWorkspaces] <~
          wire(workspaces) <~
          swData(steps) <~
          awsAddPageChangedObserver(currentPage => {
            val backgroundColor = resGetColor(s"wizard_background_step_$currentPage") getOrElse resGetColor(R.color.primary)
            runUi(
              (wizardRootLayout <~ rbvColor(backgroundColor)) ~
                (stepsAction <~ (if (currentPage == steps.length - 1) vVisible else vInvisible)))
          }
          )))) ~
      (stepsAction <~
        vInvisible <~
        vBackgroundTint(resGetColor(R.color.wizard_background_button_dive_in)) <~
        vEnabled(false) <~
        On.click(finishUi))
  }

  def finishProcess: Ui[_] = stepsAction <~ vEnabled(true)

  def addUsersToRadioGroup(accounts: Seq[Account])(implicit context: ActivityContextWrapper): Ui[_] = {
    val accountsName = accounts map (_.name) toArray
    val sa = new ArrayAdapter[String](context.getOriginal, android.R.layout.simple_spinner_dropdown_item, accountsName)
    usersGroup <~ sAdapter(sa)
  }

  def addDevicesToRadioGroup(devices: Seq[UserDevice])(implicit context: ActivityContextWrapper): Ui[_] = {
    val radioViews = (devices map (account => userRadio(account.deviceName, account.deviceId))) :+
      userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), newConfigurationKey)
    (devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui {
        radioViews.headOption foreach (_.setChecked(true))
      }
  }

  private def userRadio(title: String, tag: String)(implicit context: ActivityContextWrapper): RadioButton = getUi(
    w[RadioButton] <~ radioStyle <~ tvText(title) <~ vTag(tag)
  )

  def radioStyle(implicit context: ActivityContextWrapper): Tweak[RadioButton] =
    vWrapContent +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

  def searchDevices(userInfo: UserInfo)(implicit context: ActivityContextWrapper): Ui[_] =
    addDevicesToRadioGroup(userInfo.devices) ~
      showDevices ~
      (titleDevice <~ tvText(resGetString(R.string.addDeviceTitle, userInfo.name)))

  def showLoading(implicit context: ActivityContextWrapper): Ui[_] =
    (loadingRootLayout <~ vVisible) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  def showUser(implicit context: ActivityContextWrapper): Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vVisible) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  def showWizard(implicit context: ActivityContextWrapper): Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vVisible <~ rbvColor(resGetColor(R.color.wizard_background_step_0), forceFade = true)) ~
      (deviceRootLayout <~ vGone)

  def showDevices(implicit context: ActivityContextWrapper): Ui[_] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vVisible)

}

