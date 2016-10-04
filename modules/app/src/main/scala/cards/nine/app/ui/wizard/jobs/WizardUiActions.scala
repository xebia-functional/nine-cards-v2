package cards.nine.app.ui.wizard.jobs

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, SystemBarsTint, UiContext, UiException}
import cards.nine.app.ui.components.dialogs.AlertDialogFragment
import cards.nine.app.ui.components.layouts.StepData
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.StepsWorkspacesTweaks._
import cards.nine.app.ui.components.widgets.tweaks.RippleBackgroundViewTweaks._
import cards.nine.app.ui.wizard.models.{UserCloudDevice, UserCloudDevices}
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cats.implicits._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

class WizardUiActions(dom: WizardDOM with WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends WizardStyles
  with ImplicitsUiExceptions {

  val newConfigurationKey = "new_configuration"

  val tagDialog = "wizard-dialog"

  lazy val systemBarsTint = new SystemBarsTint

  lazy val steps = Seq(
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_1)),
    StepData(R.drawable.wizard_02, resGetString(R.string.wizard_step_2)),
    StepData(R.drawable.wizard_03, resGetString(R.string.wizard_step_3)),
    StepData(R.drawable.wizard_04, resGetString(R.string.wizard_step_4)),
    StepData(R.drawable.wizard_05, resGetString(R.string.wizard_step_5)))

  def initialize(): TaskService[Unit] = {

    def pagination(position: Int) =
      (w[ImageView] <~ paginationItemStyle <~ ivSrc(R.drawable.wizard_pager) <~ vTag(position.toString)).get

    def createPagers(steps: Seq[StepData]) = {
      val pagerViews = steps.indices map { position =>
        val view = pagination(position)
        view.setActivated(position == 0)
        view
      }
      dom.paginationPanel <~ vgAddViews(pagerViews)
    }

    def reloadPagers(currentPage: Int) = Transformer {
      case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true)
      case i: ImageView => i <~ vActivated(false)
    }

    def initializeUi(): Ui[Any] =
      (dom.userAction <~
        defaultActionStyle <~
        On.click {
          Ui {
            val termsAccept = dom.usersTerms.isChecked
            dom.onClickAcceptTermsButton(termsAccept)
          }
        }) ~
        (dom.deviceAction <~
          defaultActionStyle <~
          On.click {
            dom.devicesGroup <~ Transformer {
              case i: RadioButton if i.isChecked =>
                Ui {
                  val tag = Option(i.getTag) map (_.toString)
                  tag match {
                    case Some(`newConfigurationKey`) =>
                      dom.onClickSelectDeviceButton(None)
                    case cloudId =>
                      dom.onClickSelectDeviceButton(cloudId)
                  }
                }
            }
          }) ~
        (dom.workspaces <~
          vGlobalLayoutListener(_ => {
            dom.workspaces <~
              swData(steps) <~
              awsAddPageChangedObserver(currentPage => {
                val backgroundColor = resGetColor(s"wizard_background_step_$currentPage") getOrElse resGetColor(R.color.primary)
                ((dom.wizardRootLayout <~ rbvColor(backgroundColor)) ~
                  (dom.stepsAction <~ (if (currentPage == steps.length - 1) vVisible else vInvisible)) ~
                  (dom.paginationPanel <~ reloadPagers(currentPage))).run
              })
          })) ~
        (dom.stepsAction <~
          diveInActionStyle <~
          On.click(Ui(dom.onClickFinishWizardButton()))) ~
        createPagers(steps) ~
        systemBarsTint.initSystemStatusBarTint()

    for {
      _ <- initializeUi().toService
      _ <- goToUser()
    } yield ()
  }

  def goToUser(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vVisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible)).toService

  def goToWizard(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vVisible <~ rbvColor(resGetColor(R.color.wizard_background_step_0), forceFade = true)) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible)).toService

  def goToNewConfiguration(): TaskService[Unit] =
    (showNewConfigurationScreen() ~
      Ui(dom.onStartNewConfiguration())).toService

  def showNewConfiguration(): TaskService[Unit] = showNewConfigurationScreen().toService

  def showLoading(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vVisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible) ~
      (dom.newConfigurationStep <~ vgRemoveAllViews)).toService

  def showErrorLoginUser(): TaskService[Unit] = backToUser(R.string.errorLoginUser)

  def showErrorConnectingGoogle(): TaskService[Unit] = backToUser(R.string.errorConnectingGoogle)

  private[this] def showNewConfigurationScreen(): Ui[Any] =
    (dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vVisible)

  private[this] def backToUser(errorMessage: Int): TaskService[Unit] =
    uiShortToast2(errorMessage).toService *> goToUser()

  def showErrorAcceptTerms(): TaskService[Unit] =
    (dom.rootLayout <~ vSnackbarShort(R.string.messageAcceptTerms)).toService

  def showDevices(devices: UserCloudDevices): TaskService[Unit] = {

    def subtitle(device: UserCloudDevice): String = {
      if (device.fromV1) resGetString(R.string.deviceMigratedFromV1) else {
        val time = new PrettyTime().format(device.modifiedDate)
        resGetString(R.string.syncLastSynced, time)
      }
    }

    def userRadio(title: String, tag: String, visible: Boolean = true): RadioButton =
      (w[RadioButton] <~
        radioStyle <~
        tvText(title) <~
        vTag(tag) <~
        (if (visible) vVisible else vGone)).get

    def userRadioSubtitle(text: String, visible: Boolean = true): TextView =
      (w[TextView] <~
        radioSubtitleStyle <~
        tvText(text) <~
        (if (visible) vVisible else vGone)).get

    def otherDevicesLink(text: String): TextView =
      (w[TextView] <~
        otherDevicesLinkStyle <~
        tvUnderlineText(text) <~
        FuncOn.click { v: View =>
          (dom.devicesGroup <~ Transformer {
            case view if view.getVisibility == View.GONE => view <~ vVisible
            case _ => Ui.nop
          }) ~ (v <~ vGone)
        }).get

    def addDevicesToRadioGroup(): Ui[Any] = {

      val userRadioView = devices.userDevice.toSeq.flatMap { device =>
        Seq(
          userRadio(resGetString(R.string.currentDeviceTitle, device.deviceName), device.cloudId),
          userRadioSubtitle(subtitle(device)))
      }

      val newConfRadioView = Seq(
        userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), newConfigurationKey),
        userRadioSubtitle(resGetString(R.string.newConfigurationSubtitle)))

      val allRadioViews = {

        val radioViews = devices.devices flatMap { device =>
          Seq(
            userRadio(device.deviceName, device.cloudId, visible = false),
            userRadioSubtitle(subtitle(device), visible = false))
        }

        if (radioViews.isEmpty) radioViews else {
          otherDevicesLink(resGetString(R.string.otherDevicesLink)) +: radioViews
        }
      }

      val radioViews = userRadioView ++ newConfRadioView ++ allRadioViews

      (dom.devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
        Ui {
          radioViews.headOption match {
            case Some(radioButton: RadioButton) => radioButton.setChecked(true)
            case _ =>
          }
        }
    }

    def showDevices(): Ui[Any] =
      (dom.loadingRootLayout <~ vGone) ~
        (dom.userRootLayout <~ vGone) ~
        (dom.wizardRootLayout <~ vGone) ~
        (dom.deviceRootLayout <~ vVisible)

    for {
      _ <- addDevicesToRadioGroup().toService
      _ <- showDevices().toService
      _ <- (dom.titleDevice <~ tvText(resGetString(R.string.addDeviceTitle, devices.name))).toService
    } yield ()
  }

  def showDiveIn(): TaskService[Unit] = (dom.stepsAction <~ vEnabled(true)).toService

  def showMarketPermissionDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorAndroidMarketPermissionNotAccepted,
      action = dom.onClickOkMarketPermissionDialog,
      negativeAction = dom.onClickCancelMarketPermissionDialog)

  def showGooglePermissionDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorGooglePermissionNotAccepted,
      action = dom.onClickOkGooglePermissionDialog,
      negativeAction = dom.onClickCancelGooglePermissionDialog)

  def showRequestPermissionsDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorFineLocationMessage,
      action = dom.onClickOkPermissionsDialog,
      negativeAction = dom.onClickCancelPermissionsDialog)

  def showSelectAccountDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorAccountsMessage,
      action = dom.onClickOkSelectAccountsDialog,
      negativeAction = dom.onClickCancelSelectAccountsDialog)

  private[this] def showErrorDialog(message: Int, action: () => Unit, negativeAction: () => Unit): TaskService[Unit] =
    TaskService[Unit] {
      CatchAll[UiException] {
        context.original.get match {
          case Some(activity: AppCompatActivity) =>
            val fm = activity.getSupportFragmentManager
            val ft = fm.beginTransaction()
            Option(fm.findFragmentByTag(tagDialog)) foreach ft.remove
            val dialog = new AlertDialogFragment(
              message = message,
              positiveAction = action,
              negativeAction = negativeAction)
            ft.add(dialog, tagDialog).addToBackStack(javaNull)
            ft.commitAllowingStateLoss()
          case _ =>
        }
      }
    }

}