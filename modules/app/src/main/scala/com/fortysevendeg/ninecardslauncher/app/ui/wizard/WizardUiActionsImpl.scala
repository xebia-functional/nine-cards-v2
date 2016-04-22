package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts._
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.widget._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SpinnerTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.StepData
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.StepsWorkspacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.RippleBackgroundViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevices
import com.fortysevendeg.ninecardslauncher.process.cloud.models.CloudStorageDevice
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait WizardUiActionsImpl
  extends WizardUiActions
  with WizardStyles {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val presenter: WizardPresenter

  val newConfigurationKey = "new_configuration"

  lazy val rootLayout = Option(findView(TR.wizard_root))

  lazy val loadingRootLayout = Option(findView(TR.wizard_loading_content))

  lazy val userRootLayout = Option(findView(TR.wizard_user_content))

  lazy val usersSpinner = Option(findView(TR.wizard_user_group))

  lazy val usersTerms = Option(findView(TR.wizard_user_terms))

  lazy val userAction = Option(findView(TR.wizard_user_action))

  lazy val titleDevice = Option(findView(TR.wizard_device_title))

  lazy val deviceRootLayout = Option(findView(TR.wizard_device_content))

  lazy val devicesGroup = Option(findView(TR.wizard_device_group))

  lazy val deviceAction = Option(findView(TR.wizard_device_action))

  lazy val stepsAction = Option(findView(TR.wizard_steps_action))

  lazy val wizardRootLayout = Option(findView(TR.wizard_steps_content))

  lazy val paginationPanel = Option(findView(TR.wizard_steps_pagination_panel))

  lazy val workspaces = Option(findView(TR.wizard_steps_workspace))

  lazy val steps = Seq(
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_1)),
    StepData(R.drawable.wizard_02, resGetString(R.string.wizard_step_2)),
    StepData(R.drawable.wizard_03, resGetString(R.string.wizard_step_3)),
    StepData(R.drawable.wizard_04, resGetString(R.string.wizard_step_4)),
    StepData(R.drawable.wizard_05, resGetString(R.string.wizard_step_5)))

  override def initialize(accounts: Seq[Account]): Ui[Any] = {
    addUsersToRadioGroup(accounts) ~
      (userAction <~
        defaultActionStyle <~
        On.click {
          Ui {
            val termsAccept = usersTerms exists (_.isChecked)
            val username = usersSpinner map (_.getSelectedItem.toString) getOrElse ""
            presenter.connectAccount(username, termsAccept)
          }
        }) ~
      (deviceAction <~
        defaultActionStyle <~
        On.click {
          devicesGroup <~ Transformer {
            case i: RadioButton if i.isChecked =>
              Ui {
                val tag = Option(i.getTag) map (_.toString)
                tag match {
                  case Some(`newConfigurationKey`) => presenter.generateCollections(None)
                  case device => presenter.generateCollections(device)
                }
              }
          }
        }) ~
      (workspaces <~
        swData(steps) <~
        awsAddPageChangedObserver(currentPage => {
          val backgroundColor = resGetColor(s"wizard_background_step_$currentPage") getOrElse resGetColor(R.color.primary)
          ((wizardRootLayout <~ rbvColor(backgroundColor)) ~
            (stepsAction <~ (if (currentPage == steps.length - 1) vVisible else vInvisible)) ~
            (paginationPanel <~ reloadPagers(currentPage))).run
        }
        )) ~
      (stepsAction <~
        diveInActionStyle <~
        On.click(Ui(presenter.finishWizard()))) ~
      createPagers(steps) ~
      goToUser()
  }

  override def goToUser(): Ui[Any] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vVisible) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  override def goToWizard(): Ui[Any] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vVisible <~ rbvColor(resGetColor(R.color.wizard_background_step_0), forceFade = true)) ~
      (deviceRootLayout <~ vGone)

  override def showLoading(): Ui[Any] =
    (loadingRootLayout <~ vVisible) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vGone)

  override def showErrorLoginUser(): Ui[Any] = backToUser(R.string.errorLoginUser)

  override def showErrorConnectingGoogle(): Ui[Any] = backToUser(R.string.errorConnectingGoogle)

  override def showErrorSelectUser(): Ui[Any] = showMessage(R.string.errorSelectUser)

  override def showErrorAcceptTerms(): Ui[Any] = showMessage(R.string.messageAcceptTerms)

  override def showErrorAndroidMarketNotAccepted(): Ui[Any] = Ui.nop

  override def showErrorGoogleDriveNotAccepted(): Ui[Any] = Ui.nop

  override def showDevices(devices: UserCloudDevices): Ui[Any] =
    addDevicesToRadioGroup(devices.devices) ~
      showDevices ~
      (titleDevice <~ tvText(resGetString(R.string.addDeviceTitle, devices.name)))

  override def showDiveIn(): Ui[Any] = stepsAction <~ vEnabled(true)

  private[this] def showMessage(message: Int): Ui[Any] = rootLayout <~ vSnackbarShort(message)

  private[this] def showDevices: Ui[Any] =
    (loadingRootLayout <~ vGone) ~
      (userRootLayout <~ vGone) ~
      (wizardRootLayout <~ vGone) ~
      (deviceRootLayout <~ vVisible)

  private[this] def backToUser(errorMessage: Int): Ui[Any] =
    uiShortToast(errorMessage) ~ goToUser()

  private[this] def addUsersToRadioGroup(accounts: Seq[Account]): Ui[Any] = {
    val accountsName = accounts map (_.name) toArray
    val sa = new ArrayAdapter[String](activityContextWrapper.getOriginal, android.R.layout.simple_spinner_dropdown_item, accountsName)
    usersSpinner <~ sAdapter(sa)
  }

  private[this] def addDevicesToRadioGroup(devices: Seq[CloudStorageDevice]): Ui[Any] = {
    val radioViews = (devices map (device => userRadio(device.deviceName, device.deviceId))) :+
      userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), newConfigurationKey)
    (devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui {
        radioViews.headOption foreach (_.setChecked(true))
      }
  }

  private[this] def createPagers(steps: Seq[StepData]) = {
    val pagerViews = steps.indices map { position =>
      val view = pagination(position)
      view.setActivated(position == 0)
      view
    }
    paginationPanel <~ vgAddViews(pagerViews)
  }

  private[this] def reloadPagers(currentPage: Int) = Transformer {
    case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true)
    case i: ImageView => i <~ vActivated(false)
  }

  private[this] def pagination(position: Int) =
    (w[ImageView] <~ paginationItemStyle <~ vTag(position.toString)).get

  private[this] def userRadio(title: String, tag: String): RadioButton =
    (w[RadioButton] <~ radioStyle <~ tvText(title) <~ vTag(tag)).get

}

