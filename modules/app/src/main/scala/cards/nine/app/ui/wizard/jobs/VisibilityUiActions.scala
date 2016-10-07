package cards.nine.app.ui.wizard.jobs

import android.view.animation.DecelerateInterpolator
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.app.ui.components.widgets.snails.RippleBackgroundSnails._
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.macroid.extras.ProgressBarTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class VisibilityUiActions(dom: WizardDOM with WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_]) {

  lazy val systemBarsTint = new SystemBarsTint

  lazy val defaultInterpolator = new DecelerateInterpolator(.7f)

  def goToUser(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vVisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible)).toService

  def goToWizard(): TaskService[Unit] = {
    val backgroundColor = resGetColor(R.color.wizard_background_step_0)
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vVisible <~ ripple(backgroundColor, forceFade = true)) ~
      systemBarsTint.updateStatusColor(backgroundColor) ~
      systemBarsTint.defaultStatusBar() ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible)).toService
  }

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

  def hideFistStepAndShowLoadingBetterCollections(): TaskService[Unit] =
    (firstStepChoreographyOut ~~
      showLoading(R.string.wizard_loading_looking_for_better_collection) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_1)))).toService

  def hideSecondStepAndShowLoadingSavingCollection(): TaskService[Unit] =
    (secondStepChoreographyOut ~~
      showLoading(R.string.wizard_loading_saving_collections) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_2)))).toService

  def hideThirdStep(): TaskService[Unit] = thirdStepChoreographyOut.toService

  def fadeOutInAllChildInStep = fadeOutAllStep.toService

  def showLoadingSavingMoments(): TaskService[Unit] =
    (showLoading(R.string.wizard_loading_saving_moments) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_3)))).toService

  def showLoadingDevices(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_devices).toService

  private[this] def updateStatusColor(): Ui[Any] =
    systemBarsTint.lightStatusBar() ~ systemBarsTint.updateStatusColor(resGetColor(R.color.background_app))

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

  private[this] def firstStepChoreographyOut = {
    (dom.newConfigurationStep0HeaderContent <~
      vPivotY(0) <~
      applyAnimation(alpha = Some(0), scaleY = Some(0), interpolator = Some(defaultInterpolator))) ~
      (dom.newConfigurationStep0HeaderImage <~ applyFadeOut()) ~
      (dom.newConfigurationStep0Title <~ applyFadeOut()) ~
      (dom.newConfigurationStep0Description <~~ applyFadeOut())
  }

  private[this] def secondStepChoreographyOut = {
    (dom.newConfigurationStep1Title <~ applyFadeOut()) ~
      (dom.newConfigurationStep1Description <~ applyFadeOut()) ~
      (dom.newConfigurationStep1AllApps <~ applyFadeOut()) ~
      (dom.newConfigurationStep1Best9 <~ applyFadeOut()) ~
      (dom.newConfigurationStep1CollectionCount <~ applyFadeOut()) ~
      (dom.newConfigurationStep1CollectionsContent <~~ applyFadeOut())
  }

  private[this] def thirdStepChoreographyOut = {
    (dom.newConfigurationStep2HeaderContent <~
      vPivotY(0) <~
      applyAnimation(alpha = Some(0), scaleY = Some(0), interpolator = Some(defaultInterpolator))) ~
      (dom.newConfigurationStep2HeaderImage1 <~ applyFadeOut()) ~
      (dom.newConfigurationStep2HeaderImage2 <~ applyFadeOut()) ~
      (dom.newConfigurationStep2Title <~ applyFadeOut()) ~
      (dom.newConfigurationStep2Description <~~ applyFadeOut())
  }

  private[this] def fadeOutAllStep = {
    val fades = (0 to dom.newConfigurationStep.getChildCount) map { position =>
      dom.newConfigurationStep.getChildAt(position) <~~ applyFadeOut()
    }
    Ui.sequence(fades: _*)
  }

}
