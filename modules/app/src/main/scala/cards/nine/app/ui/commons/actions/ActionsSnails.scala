package cards.nine.app.ui.commons.actions

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import cards.nine.commons._
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object ActionsSnails {

  def scaleToToolbar(radioScale: Float)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val duration = SpeedAnimations.getDuration
      view.setPivotY(0)
      view
        .animate
        .setDuration(duration)
        .setInterpolator(new DecelerateInterpolator())
        .scaleY(radioScale)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def showContent()(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val duration = SpeedAnimations.getDuration
      view.setVisibility(View.VISIBLE)
      view.setAlpha(0)
      view
        .animate
        .setStartDelay(duration)
        .setDuration(duration)
        .setInterpolator(new DecelerateInterpolator())
        .alpha(1f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def showFab()(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val duration = SpeedAnimations.getDuration
      view.setVisibility(View.VISIBLE)
      view.setScaleX(0)
      view.setScaleY(0)
      view
        .animate
        .setStartDelay(duration)
        .setDuration(duration)
        .setInterpolator(new DecelerateInterpolator())
        .scaleX(1f)
        .scaleY(1f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def circularReveal(
    view: View,
    x: Int,
    y: Int,
    w: Int,
    h: Int,
    duration: Int,
    startRadius: Int,
    endRadius: Int,
    animationEnd: => Unit) = {
    val reveal: Animator = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius)
    reveal.setInterpolator(new DecelerateInterpolator())
    reveal.setDuration(duration)
    reveal.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animationEnd
      }
    })
    reveal.start()
  }

}
