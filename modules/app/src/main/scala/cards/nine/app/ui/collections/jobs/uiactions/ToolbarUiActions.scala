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

package cards.nine.app.ui.collections.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.snails.CollectionsSnails._
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, SystemBarsTint, UiContext}
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewTweaks._

class ToolbarUiActions(val dom: GroupCollectionsDOM, listener: GroupCollectionsUiListener)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
    extends ImplicitsUiExceptions {

  lazy val systemBarsTint = new SystemBarsTint

  val resistanceDisplacement = .2f

  val resistanceScale = .05f

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val maxHeightToolbar = resGetDimensionPixelSize(R.dimen.height_toolbar_collection_details)

  def initialize(
      backgroundColor: Int,
      initialColor: Int,
      iconCollection: String,
      isStateChanged: Boolean): TaskService[Unit] =
    (Ui {
      activityContextWrapper.original.get match {
        case Some(activity: AppCompatActivity) =>
          val iconIndicatorDrawable = PathMorphDrawable(
            defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
            padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))
          statuses = statuses.copy(iconHome = Option(iconIndicatorDrawable))
          activity.setSupportActionBar(dom.toolbar)
          activity.getSupportActionBar.setDisplayHomeAsUpEnabled(true)
          activity.getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)
        case _ =>
      }
    } ~
      (dom.root <~ vBackgroundColor(backgroundColor)) ~
      systemBarsTint.initSystemStatusBarTint() ~
      updateToolbarColor(initialColor) ~
      (dom.icon <~ ivSrc(iconCollection.getIconDetail)) ~
      (if (isStateChanged) Ui.nop else dom.toolbar <~ enterToolbar)).toService()

  def pullCloseScrollY(scroll: Int, close: Boolean): TaskService[Unit] = {
    val displacement         = scroll * resistanceDisplacement
    val distanceToValidClose = resGetDimension(R.dimen.distance_to_valid_action)
    val scale                = 1f + ((scroll / distanceToValidClose) * resistanceScale)
    ((dom.iconContent <~ vScaleX(scale) <~ vScaleY(scale) <~ vTranslationY(displacement)) ~
      Ui {
        val newIcon = if (close) IconTypes.CLOSE else IconTypes.BACK
        statuses.iconHome match {
          case Some(icon) if icon.currentTypeIcon != newIcon && !icon.isRunning =>
            icon.setToTypeIcon(newIcon)
            icon.start()
          case _ =>
        }
      }).toService()
  }

  private[this] def updateToolbarColor(color: Int): Ui[Any] =
    (dom.toolbar <~ vBackgroundColor(color)) ~
      systemBarsTint.updateStatusColor(color)

}
