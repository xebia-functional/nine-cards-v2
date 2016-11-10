package cards.nine.app.ui.wizard.jobs.uiactions

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.{Gravity, View, ViewGroup}
import android.widget._
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, SystemBarsTint, UiContext, UiException}
import cards.nine.app.ui.components.dialogs.AlertDialogFragment
import cards.nine.app.ui.components.layouts.StepData
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.StepsWorkspacesTweaks._
import cards.nine.app.ui.wizard.models.{UserCloudDevice, UserCloudDevices}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

class WizardUiActions(dom: WizardDOM, listener: WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  val newConfigurationKey = "new_configuration"

  val tagDialog = "wizard-dialog"

  lazy val systemBarsTint = new SystemBarsTint

  lazy val steps = Seq(
    StepData(
      R.drawable.wizard_01,
      resGetColor(R.color.wizard_background_step_1),
      resGetString(R.string.wizard_step_title_1),
      resGetString(R.string.wizard_step_1)),
    StepData(
      R.drawable.wizard_02,
      resGetColor(R.color.wizard_background_step_2),
      resGetString(R.string.wizard_step_title_2),
      resGetString(R.string.wizard_step_2)),
    StepData(
      R.drawable.wizard_03,
      resGetColor(R.color.wizard_background_step_3),
      resGetString(R.string.wizard_step_title_3),
      resGetString(R.string.wizard_step_3)),
    StepData(
      R.drawable.wizard_04,
      resGetColor(R.color.wizard_background_step_4),
      resGetString(R.string.wizard_step_title_4),
      resGetString(R.string.wizard_step_4)),
    StepData(
      R.drawable.wizard_05,
      resGetColor(R.color.wizard_background_step_5),
      resGetString(R.string.wizard_step_title_5),
      resGetString(R.string.wizard_step_5)))

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

    def devicesChecked() = Transformer {
      case i: RadioButton if i.isChecked =>
        Ui {
          val tag = Option(i.getTag) map (_.toString)
          tag match {
            case Some(`newConfigurationKey`) =>
              listener.onClickSelectDeviceButton(None)
            case cloudId =>
              listener.onClickSelectDeviceButton(cloudId)
          }
        }
    }

    ((dom.userAction <~
      defaultActionStyle <~
      On.click(Ui(listener.onClickAcceptTermsButton()))) ~
      (dom.deviceAction <~
        defaultActionStyle <~
        On.click (dom.devicesGroup <~ devicesChecked())) ~
      (dom.workspaces <~
        vGlobalLayoutListener(_ => {
          dom.workspaces <~
            swData(steps) <~
            swAddMovementObserver((current, go, isLeft, fraction) => {
              val color = (current.color, go.color).interpolateColors(fraction)
              ((dom.stepsBackground <~ vBackgroundColor(color)) ~ systemBarsTint.updateStatusColor(color)).run
            }) <~
            awsAddPageChangedObserver(currentPage => {
              val showAction = currentPage == steps.length - 1
              ((dom.paginationPanel <~ reloadPagers(currentPage)) ~
                ((showAction, (dom.stepsAction ~> isVisible).get, (dom.paginationPanel ~> isVisible).get) match {
                  case (true, false, _) =>
                    (dom.stepsAction <~ applyFadeIn()) ~
                      (dom.paginationPanel <~ applyFadeOut()) ~
                      (dom.stepsDownloadingMessage <~ (if ((dom.stepsAction ~> isEnabled).get) vGone else vVisible))
                  case (false, _, false) =>
                    (dom.stepsAction <~ applyFadeOut()) ~
                      (dom.paginationPanel <~ applyFadeIn()) ~
                      (dom.stepsDownloadingMessage <~ vGone)
                  case _ => Ui.nop
                })).run
            })
        })) ~
      (dom.userTitle <~ tvText(Html.fromHtml(resGetString(R.string.welcome)))) ~
      (dom.usersTerms <~ tvText(Html.fromHtml(resGetString(R.string.termsAndConditions)))) ~
      (dom.stepsAction <~
        diveInActionStyle <~
        On.click(Ui(listener.onClickFinishWizardButton()))) ~
      createPagers(steps) ~
      systemBarsTint.initSystemStatusBarTint() ~
      systemBarsTint.updateStatusColor(resGetColor(R.color.background_app)) ~
      systemBarsTint.lightStatusBar()).toService
  }

  def showErrorLoginUser(): TaskService[Unit] = uiShortToast2(R.string.errorLoginUser).toService

  def showErrorGeneral(): TaskService[Unit] = uiShortToast2(R.string.contactUsError).toService

  def showNoCollectionsSelectedMessage(): TaskService[Unit] = uiShortToast2(R.string.errorNoCollectionsSelected).toService

  def showErrorConnectingGoogle(): TaskService[Unit] = uiShortToast2(R.string.errorConnectingGoogle).toService

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

  def showDiveIn(): TaskService[Unit] =
    ((dom.stepsDownloadingMessage <~ vGone) ~
      (dom.stepsAction <~
        vEnabled(true) <~
        vBackgroundTint(resGetColor(R.color.wizard_background_action_enable)))).toService

  def showMarketPermissionDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorAndroidMarketPermissionNotAccepted,
      action = listener.onClickOkMarketPermissionDialog,
      negativeAction = listener.onClickCancelMarketPermissionDialog)

  def showGooglePermissionDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorGooglePermissionNotAccepted,
      action = listener.onClickOkGooglePermissionDialog,
      negativeAction = listener.onClickCancelGooglePermissionDialog)

  def showRequestPermissionsDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorFineLocationMessage,
      action = listener.onClickOkPermissionsDialog,
      negativeAction = listener.onClickCancelPermissionsDialog)

  def showSelectAccountDialog(): TaskService[Unit] =
    showErrorDialog(
      message = R.string.errorAccountsMessage,
      action = listener.onClickOkSelectAccountsDialog,
      negativeAction = listener.onClickCancelSelectAccountsDialog)

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

  // Styles

  private[this] def defaultActionStyle(implicit context: ActivityContextWrapper): Tweak[Button] =
    vBackgroundTint(resGetColor(R.color.primary))

  private[this] def diveInActionStyle(implicit context: ActivityContextWrapper): Tweak[Button] =
    vInvisible +
      vBackgroundTint(resGetColor(R.color.wizard_background_action_disable)) +
      vEnabled(false)

  private[this] def radioStyle(implicit context: ActivityContextWrapper): Tweak[RadioButton] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_checkbox)
    vWrapContent +
      vPadding(paddingLeft = padding, paddingRight = padding) +
      tvGravity(Gravity.CENTER_VERTICAL)

  }

  private[this] def radioSubtitleStyle(implicit context: ActivityContextWrapper): Tweak[TextView] =
    vWrapContent +
      vPadding(
        paddingLeft = resGetDimensionPixelSize(R.dimen.margin_left_subtitle),
        paddingRight = resGetDimensionPixelSize(R.dimen.padding_default),
        paddingBottom = resGetDimensionPixelSize(R.dimen.padding_default))

  private[this] def otherDevicesLinkStyle(implicit context: ActivityContextWrapper): Tweak[TextView] =
    vMatchWidth +
      tvGravity(Gravity.CENTER_HORIZONTAL) +
      vPaddings(resGetDimensionPixelSize(R.dimen.padding_large)) +
      tvColorResource(R.color.primary)

  private[this] def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.wizard_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.wizard_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin)
  }

}