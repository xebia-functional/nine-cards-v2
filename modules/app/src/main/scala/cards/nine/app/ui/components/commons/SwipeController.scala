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

package cards.nine.app.ui.components.commons

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent._
import android.view.{MotionEvent, VelocityTracker, ViewConfiguration}
import cards.nine.commons.javaNull

trait SwipeController {

  def getContext: Context

  val computeUnitsTracker = 1000

  lazy val (maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (configuration.getScaledMaximumFlingVelocity, configuration.getScaledMinimumFlingVelocity)
  }

  // We need to use "null" instead of Option because we must assign "null" when we recycle
  // the velocityTracker for avoid IllegalStateException in Android SDK
  private[this] var velocityTracker: VelocityTracker = javaNull

  def updateSwipe(event: MotionEvent): Unit = {
    val action = MotionEventCompat.getActionMasked(event)
    action match {
      case ACTION_DOWN =>
        if (velocityTracker == javaNull) {
          velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker.addMovement(event)
      case ACTION_MOVE =>
        velocityTracker.addMovement(event)
      case _ =>
    }
  }

  def currentSwiping: Swiping = {
    velocityTracker.computeCurrentVelocity(computeUnitsTracker, maximumVelocity)
    velocityTracker.getXVelocity match {
      case v if v > minimumVelocity  => SwipeLeft(v)
      case v if v < -minimumVelocity => SwipeRight(v)
      case _                         => NoSwipe()
    }
  }

  def recycleSwipe(): Unit = {
    velocityTracker.recycle()
    velocityTracker = javaNull
  }

}

sealed trait Swiping {
  def getVelocity: Float = 0
}

case class SwipeLeft(velocity: Float) extends Swiping {
  override def getVelocity: Float = velocity
}

case class SwipeRight(velocity: Float) extends Swiping {
  override def getVelocity: Float = velocity
}

case class NoSwipe() extends Swiping
