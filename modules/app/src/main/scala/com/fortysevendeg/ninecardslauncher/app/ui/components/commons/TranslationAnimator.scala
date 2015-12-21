package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator, ObjectAnimator}
import android.view.View
import android.view.animation.DecelerateInterpolator
import macroid.Ui
import macroid.FullDsl._

class TranslationAnimator(
  translation: Translation = NoTranslation,
  update: (Float) => Ui[_],
  end: () => Ui[_]) {

  private[this] val animator: ValueAnimator = translation match {
    case NoTranslation => new ValueAnimator
    case _ =>
      val objectAnimator = new ObjectAnimator
      objectAnimator.setPropertyName(translation.name)
      objectAnimator
  }
  animator.setInterpolator(new DecelerateInterpolator())
  animator.addListener(new AnimatorListenerAdapter() {
    override def onAnimationEnd(animation: Animator) = {
      runUi(end())
      super.onAnimationEnd(animation)
    }
  })
  animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    override def onAnimationUpdate(value: ValueAnimator) = runUi(update(value.getAnimatedValue.asInstanceOf[Float]))
  })

  def start(): Unit = animator.start()

  def cancel(): Unit = animator.cancel()

  def setTarget(view: View): Unit = animator.setTarget(view)

  def move(from: Float, to: Float): Unit = animator.setFloatValues(from, to)

  def setDuration(duration: Long): Unit = animator.setDuration(duration)

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