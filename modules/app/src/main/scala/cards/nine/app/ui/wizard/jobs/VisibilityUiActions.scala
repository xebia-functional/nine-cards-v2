package cards.nine.app.ui.wizard.jobs

import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.app.ui.components.widgets.tweaks.RippleBackgroundViewTweaks._
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.commons.ops.UiOps._
import com.fortysevendeg.macroid.extras.ProgressBarTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

class VisibilityUiActions(dom: WizardDOM with WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_]) {

  lazy val systemBarsTint = new SystemBarsTint

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

  def showLoadingConnectingWithGoogle(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_connecting_with_google).toService

  def showLoadingRequestGooglePermission(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_request_google_permission).toService

  def showLoadingConnectingWithGooglePlus(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_connecting_with_plus).toService

  def showLoadingBetterCollections(): TaskService[Unit] =
    (showLoading(R.string.wizard_loading_looking_for_better_collection) ~
      systemBarsTint.lightStatusBar() ~
      systemBarsTint.updateStatusColor(resGetColor(R.color.background_app)) ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_1)))).toService

  def showLoadingSavingCollection(): TaskService[Unit] =
    (showLoading(R.string.wizard_loading_saving_collections) ~
      systemBarsTint.lightStatusBar() ~
      systemBarsTint.updateStatusColor(resGetColor(R.color.background_app)) ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_2)))).toService

  def showLoadingSavingMoments(): TaskService[Unit] =
    (showLoading(R.string.wizard_loading_saving_moments) ~
      systemBarsTint.lightStatusBar() ~
      systemBarsTint.updateStatusColor(resGetColor(R.color.background_app)) ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_3)))).toService

  def showLoadingDevices(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_devices).toService

  def cleanStep(): TaskService[Unit] = (dom.newConfigurationStep <~ vgRemoveAllViews).toService

  private[this] def showLoading(resText: Int, colorBar: Option[Int] = None): Ui[Any] =
    (dom.loadingRootLayout <~ vVisible) ~
      (dom.loadingText <~ tvText(resText)) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible) ~
      (dom.newConfigurationStep <~ vgRemoveAllViews)

  private[this] def showNewConfigurationScreen(): Ui[Any] =
    (dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vVisible)

}
