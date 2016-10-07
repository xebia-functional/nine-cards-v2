package cards.nine.app.ui.commons.actions

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SnailsUtils
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object ActionsSnails {

  def revealIn(x: Int, y: Int, w: Int, h: Int, sizeIcon: Int)(implicit context: ContextWrapper): Snail[View] =
    Lollipop ifSupportedThen {
      val startRadius = sizeIcon / 2
      revealIn(x, y, w, h, startRadius, SpeedAnimations.getDuration)
    } getOrElse {
      applyFadeIn()
    }

  def revealOut(x: Int, y: Int, w: Int, h: Int)(implicit context: ContextWrapper): Snail[View] =
    Lollipop ifSupportedThen {
      revealOut(x, y, w, h, SpeedAnimations.getDuration)
    } getOrElse {
      applyFadeOut()
    }

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

  private[this] def revealIn(x: Int, y: Int, w: Int, h: Int, startRadius: Int, duration: Int): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val endRadius = SnailsUtils.calculateRadius(x, y, w, h)
      circularReveal(view, x, y, w, h, duration, startRadius, endRadius, animPromise.trySuccess(()))
      animPromise.future
  }

  private[this] def revealOut(x: Int, y: Int, w: Int, h: Int, duration: Int): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val startRadius = SnailsUtils.calculateRadius(x, y, w, h)
      circularReveal(view, x, y, w, h, duration, startRadius, 0, {
        view.setVisibility(View.GONE)
        animPromise.trySuccess(())
      })
      animPromise.future
  }

}
