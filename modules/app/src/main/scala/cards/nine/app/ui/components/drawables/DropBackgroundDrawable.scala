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

package cards.nine.app.ui.components.drawables

import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.graphics._
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import macroid.extras.ResourcesExtras._
import macroid.extras.SnailsUtils
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.{Future, Promise}

class DropBackgroundDrawable(implicit contextWrapper: ContextWrapper) extends Drawable {

  private[this] var percentage: Float = 0

  private[this] val duration = SpeedAnimations.getDuration

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(resGetColor(R.color.collection_workspace_feedback_drop))
    paint.setAntiAlias(true)
    paint
  }

  private[this] val animator: ValueAnimator = {
    val valueAnimation = new ValueAnimator
    valueAnimation.setInterpolator(new DecelerateInterpolator())
    valueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      override def onAnimationUpdate(value: ValueAnimator) =
        update(value.getAnimatedValue.asInstanceOf[Float])
    })
    valueAnimation
  }

  override def setColorFilter(cf: ColorFilter): Unit =
    circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val x      = bounds.width() / 2
    val y      = bounds.height() / 2
    canvas.drawCircle(x, y, x * percentage, circlePaint)
  }

  def start(): Ui[Future[Any]] = animation(0f, 1f)

  def end(): Ui[Future[Any]] = {
    if (animator.isRunning) animator.cancel()
    animation(percentage, 0f)
  }

  private[this] def animation(from: Float, to: Float): Ui[Future[Any]] = Ui {
    val promise = Promise[Unit]()
    animator.removeAllListeners()
    animator.addListener(new AnimatorListenerAdapter() {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        promise.trySuccess(())
      }
    })
    animator.setFloatValues(from, to)
    animator.setDuration(duration)
    animator.start()
    promise.future
  }

  private[this] def update(p: Float) = {
    percentage = p
    invalidateSelf()
  }
}
