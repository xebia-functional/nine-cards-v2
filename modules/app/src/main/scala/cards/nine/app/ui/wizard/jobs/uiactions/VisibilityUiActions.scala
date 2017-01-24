/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.wizard.jobs.uiactions

import android.view.View
import android.view.animation.DecelerateInterpolator
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{SystemBarsTint, UiContext}
import cards.nine.commons.services.TaskService._
import cards.nine.models.PackagesByCategory
import macroid.extras.ProgressBarTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class VisibilityUiActions(dom: WizardDOM, listener: WizardUiListener)(
    implicit val context: ActivityContextWrapper,
    val uiContext: UiContext[_]) {

  lazy val systemBarsTint = new SystemBarsTint

  lazy val defaultInterpolator = new DecelerateInterpolator(.7f)

  val translate = resGetDimensionPixelSize(R.dimen.padding_xlarge)

  def goToUser(): TaskService[Unit] = {

    def applyAnim()(implicit context: ContextWrapper): Snail[View] =
      vVisible +
        vAlpha(0) +
        vTranslationY(-translate) ++
        applyAnimation(
          alpha = Option(1),
          y = Option(0),
          duration = Option(resGetInteger(R.integer.wizard_anim_ripple_duration)))

    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vVisible) ~
      (dom.userLogo <~ vInvisible) ~
      (dom.userTitle <~ vInvisible) ~
      (dom.userAction <~ vInvisible) ~
      (dom.usersTerms <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible) ~
      (dom.userLogo <~~ applyAnim()) ~~
      (dom.userTitle <~~ applyAnim()) ~~
      (dom.userAction <~~ applyAnim()) ~~
      (dom.usersTerms <~~ applyAnim())).toService()
  }

  def goToWizard(cloudId: String): TaskService[Unit] = {
    val backgroundColor = resGetColor(R.color.wizard_background_step_1)
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      systemBarsTint.updateStatusColor(backgroundColor) ~
      systemBarsTint.defaultStatusBar() ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible) ~
      (dom.workspaces <~ vInvisible) ~
      (dom.wizardRootLayout <~ vVisible) ~
      (dom.stepsBackground <~
        vBackgroundColor(backgroundColor) <~
        vPivotY(0) <~
        vScaleY(0) <~~
        applyAnimation(scaleY = Some(1))) ~~
      (dom.workspaces <~~ applyFadeIn()) ~
      Ui(listener.onStartLoadConfiguration(cloudId))).toService()
  }

  def goToNewConfiguration(packages: Seq[PackagesByCategory]): TaskService[Unit] =
    (showNewConfigurationScreen() ~
      Ui(listener.onStartNewConfiguration(packages))).toService()

  def showNewConfiguration(): TaskService[Unit] =
    showNewConfigurationScreen().toService()

  def showLoadingConnectingWithGoogle(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_connecting_with_google).toService()

  def showLoadingRequestGooglePermission(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_request_google_permission).toService()

  def showLoadingConnectingWithGooglePlus(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_connecting_with_plus).toService()

  def hideFistStepAndShowLoadingBetterCollections(hidePrevious: Boolean): TaskService[Unit] =
    ((dom.newConfigurationNext <~ vClickable(false)) ~
      firstStepChoreographyOut.ifUi(hidePrevious) ~
      showLoading(R.string.wizard_loading_looking_for_better_collection) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_1)))).toService()

  def hideSecondStepAndShowLoadingSavingCollection(): TaskService[Unit] =
    ((dom.newConfigurationNext <~ vClickable(false)) ~
      secondStepChoreographyOut ~
      showLoading(R.string.wizard_loading_saving_collections) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_2)))).toService()

  def hideThirdStep(): TaskService[Unit] = thirdStepChoreographyOut.toService()

  def cleanNewConfiguration(): TaskService[Unit] =
    (dom.newConfigurationStep <~ vgRemoveAllViews).toService()

  def showLoadingSavingMoments(): TaskService[Unit] =
    (showLoading(R.string.wizard_loading_saving_moments) ~
      updateStatusColor() ~
      (dom.loadingBar <~ pbColor(resGetColor(R.color.wizard_new_conf_accent_3)))).toService()

  def showLoadingDevices(): TaskService[Unit] =
    showLoading(R.string.wizard_loading_devices).toService()

  private[this] def updateStatusColor(): Ui[Any] =
    systemBarsTint.lightStatusBar() ~ systemBarsTint.updateStatusColor(
      resGetColor(R.color.background_app))

  private[this] def showLoading(resText: Int, colorBar: Option[Int] = None): Ui[Any] =
    (dom.loadingRootLayout <~ vVisible) ~
      (dom.loadingText <~ tvText(resText)) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible) ~
      (dom.newConfigurationContent <~ vInvisible) ~
      (dom.newConfigurationNext <~ vClickable(true)) ~
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
      (dom.newConfigurationStep0Description <~ applyFadeOut())
  }

  private[this] def secondStepChoreographyOut = {
    (dom.newConfigurationStep1Title <~ applyFadeOut()) ~
      (dom.newConfigurationStep1Description <~ applyFadeOut()) ~
      (dom.newConfigurationStep1AllCollections <~ applyFadeOut()) ~
      (dom.newConfigurationStep1CollectionCount <~ applyFadeOut()) ~
      (dom.newConfigurationStep1CollectionsContent <~ applyFadeOut())
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

}
