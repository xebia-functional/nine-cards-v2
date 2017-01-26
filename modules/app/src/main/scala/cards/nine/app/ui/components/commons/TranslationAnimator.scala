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

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.view.View
import android.view.animation.DecelerateInterpolator
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import macroid.{ContextWrapper, Snail, Ui}

import scala.concurrent.Promise

class TranslationAnimator(
    translation: Translation = NoTranslation,
    update: (Float) => Ui[_],
    end: () => Ui[_] = () => Ui.nop)(implicit context: ContextWrapper) {

  val duration = SpeedAnimations.getDuration

  private[this] val animator: ValueAnimator = translation match {
    case NoTranslation => new ValueAnimator
    case _ =>
      val objectAnimator = new ObjectAnimator
      objectAnimator.setPropertyName(translation.name)
      objectAnimator
  }
  animator.setInterpolator(new DecelerateInterpolator())
  animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    override def onAnimationUpdate(value: ValueAnimator) =
      update(value.getAnimatedValue.asInstanceOf[Float]).run
  })

  def move(
      from: Float,
      to: Float,
      duration: Int = duration,
      attachTarget: Boolean = false): Snail[View] = Snail[View] { view =>
    val promise = Promise[Unit]()
    animator.removeAllListeners()
    animator.addListener(new AnimatorListenerAdapter() {
      override def onAnimationEnd(animation: Animator) = {
        super.onAnimationEnd(animation)
        promise.trySuccess(())
        end().run
      }
    })
    if (attachTarget) animator.setTarget(view)
    animator.setFloatValues(from, to)
    animator.setDuration(duration)
    animator.start()
    promise.future
  }

  def cancel(): Unit = animator.cancel()

  def isRunning: Boolean = animator.isRunning

}

sealed trait Translation {
  val name: String
}

case object TranslationX extends Translation {
  override val name: String = "translationX"
}

case object TranslationY extends Translation {
  override val name: String = "translationY"
}

case object NoTranslation extends Translation {
  override val name: String = ""
}
