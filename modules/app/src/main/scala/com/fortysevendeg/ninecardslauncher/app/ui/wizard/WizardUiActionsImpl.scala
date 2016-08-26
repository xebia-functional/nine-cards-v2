package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.StepData
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.StepsWorkspacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.RippleBackgroundViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevice, UserCloudDevices}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

trait WizardUiActionsImpl
  extends WizardUiActions
  with WizardStyles {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val presenter: WizardPresenter

  val newConfigurationKey = "new_configuration"

  lazy val rootLayout = findView(TR.wizard_root)

  lazy val loadingRootLayout = findView(TR.wizard_loading_content)

  lazy val userRootLayout = findView(TR.wizard_user_content)

  lazy val usersTerms = findView(TR.wizard_user_terms)

  lazy val userAction = findView(TR.wizard_user_action)

  lazy val titleDevice = findView(TR.wizard_device_title)

  lazy val deviceRootLayout = findView(TR.wizard_device_content)

  lazy val devicesGroup = findView(TR.wizard_device_group)

  lazy val deviceAction = findView(TR.wizard_device_action)

  lazy val stepsAction = findView(TR.wizard_steps_action)

  lazy val wizardRootLayout = findView(TR.wizard_steps_content)

  lazy val paginationPanel = findView(TR.wizard_steps_pagination_panel)

  lazy val workspaces = findView(TR.wizard_steps_workspace)

  lazy val steps = Seq(
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_1)),
    StepData(R.drawable.wizard_02, resGetString(R.string.wizard_step_2)),
    StepData(R.drawable.wizard_03, resGetString(R.string.wizard_step_3)),
    StepData(R.drawable.wizard_04, resGetString(R.string.wizard_step_4)),
    StepData(R.drawable.wizard_05, resGetString(R.string.wizard_step_5)))

  override def initialize(): Ui[Any] = {
      (userAction <~
        defaultActionStyle <~
        On.click {
          Ui {
            val termsAccept = usersTerms.isChecked
            presenter.connectAccount(termsAccept)
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
                  case Some(`newConfigurationKey`) => presenter.deviceSelected(None)
                  case cloudId => presenter.deviceSelected(cloudId)
                }
              }
          }
        }) ~
      (workspaces <~
        vGlobalLayoutListener(_ => {
          workspaces <~
            swData(steps) <~
            awsAddPageChangedObserver(currentPage => {
              val backgroundColor = resGetColor(s"wizard_background_step_$currentPage") getOrElse resGetColor(R.color.primary)
              ((wizardRootLayout <~ rbvColor(backgroundColor)) ~
                (stepsAction <~ (if (currentPage == steps.length - 1) vVisible else vInvisible)) ~
                (paginationPanel <~ reloadPagers(currentPage))).run
            })
        })) ~
      (stepsAction <~
        diveInActionStyle <~
        On.click(Ui(presenter.finishWizard()))) ~
      createPagers(steps) ~
      goToUser()
  }

  override def goToUser(): Ui[Any] =
    (loadingRootLayout <~ vInvisible) ~
      (userRootLayout <~ vVisible) ~
      (wizardRootLayout <~ vInvisible) ~
      (deviceRootLayout <~ vInvisible)

  override def goToWizard(): Ui[Any] =
    (loadingRootLayout <~ vInvisible) ~
      (userRootLayout <~ vInvisible) ~
      (wizardRootLayout <~ vVisible <~ rbvColor(resGetColor(R.color.wizard_background_step_0), forceFade = true)) ~
      (deviceRootLayout <~ vInvisible)

  override def showLoading(): Ui[Any] =
    (loadingRootLayout <~ vVisible) ~
      (userRootLayout <~ vInvisible) ~
      (wizardRootLayout <~ vInvisible) ~
      (deviceRootLayout <~ vInvisible)

  override def showErrorLoginUser(): Ui[Any] = backToUser(R.string.errorLoginUser)

  override def showErrorConnectingGoogle(): Ui[Any] = backToUser(R.string.errorConnectingGoogle)

  override def showErrorSelectUser(): Ui[Any] = showMessage(R.string.errorSelectUser)

  override def showErrorAcceptTerms(): Ui[Any] = showMessage(R.string.messageAcceptTerms)

  override def showDevices(devices: UserCloudDevices): Ui[Any] =
    addDevicesToRadioGroup(devices.userDevice, devices.devices) ~
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

  private[this] def addDevicesToRadioGroup(userDevice: Option[UserCloudDevice], devices: Seq[UserCloudDevice]): Ui[Any] = {

    def subtitle(device: UserCloudDevice): String = {
      if (device.fromV1) resGetString(R.string.deviceMigratedFromV1) else {
        val time = new PrettyTime().format(device.modifiedDate)
        resGetString(R.string.syncLastSynced, time)
      }
    }

    val userRadioView = userDevice.toSeq.flatMap { device =>
      Seq(
        userRadio(resGetString(R.string.currentDeviceTitle, device.deviceName), device.cloudId),
        userRadioSubtitle(subtitle(device)))
    }

    val newConfRadioView = Seq(
      userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), newConfigurationKey),
      userRadioSubtitle(resGetString(R.string.newConfigurationSubtitle)))

    val allRadioViews = {

      val radioViews = devices flatMap { device =>
        Seq(
          userRadio(device.deviceName, device.cloudId, visible = false),
          userRadioSubtitle(subtitle(device), visible = false))
      }

      if (radioViews.isEmpty) radioViews else {
        otherDevicesLink(resGetString(R.string.otherDevicesLink)) +: radioViews
      }
    }

    val radioViews = userRadioView ++ newConfRadioView ++ allRadioViews

    (devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
      Ui {
        radioViews.headOption match {
          case Some(radioButton: RadioButton) => radioButton.setChecked(true)
          case _ =>
        }
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

  private[this] def userRadio(title: String, tag: String, visible: Boolean = true): RadioButton =
    (w[RadioButton] <~
      radioStyle <~
      tvText(title) <~
      vTag(tag) <~
      (if (visible) vVisible else vGone)).get

  private[this] def userRadioSubtitle(text: String, visible: Boolean = true): TextView =
    (w[TextView] <~
      radioSubtitleStyle <~
      tvText(text) <~
      (if (visible) vVisible else vGone)).get

  private[this] def otherDevicesLink(text: String): TextView = {
    (w[TextView] <~
      otherDevicesLinkStyle <~
      tvUnderlineText(text) <~
      FuncOn.click { v: View =>
        (devicesGroup <~ Transformer {
          case view if view.getVisibility == View.GONE => view <~ vVisible
          case _ => Ui.nop
        }) ~ (v <~ vGone)
      }).get
  }

}

