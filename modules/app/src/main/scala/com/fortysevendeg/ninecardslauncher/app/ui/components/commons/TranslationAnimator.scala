package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.animation.{Animator, AnimatorListenerAdapter, ObjectAnimator, ValueAnimator}
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher2.R
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
    override def onAnimationUpdate(value: ValueAnimator) = update(value.getAnimatedValue.asInstanceOf[Float]).run
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