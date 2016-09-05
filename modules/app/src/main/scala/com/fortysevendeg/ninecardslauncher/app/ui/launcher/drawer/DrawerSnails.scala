package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.ninecardslauncher.app.commons.{AppDrawerAnimationCircle, AppDrawerAnimationValue, SpeedAnimations}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object DrawerSnails {

  def openAppDrawer(animation: AppDrawerAnimationValue, source: View)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      animation match {
        case anim @ AppDrawerAnimationCircle if anim.isSupported => reveal(source, view)(animPromise.trySuccess(()))
        case _ => fadeIn(view)(animPromise.trySuccess(()))
      }

      animPromise.future
  }

  def closeAppDrawer(animation: AppDrawerAnimationValue, source: View)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      animation match {
        case anim @ AppDrawerAnimationCircle if anim.isSupported => reveal(source, view, in = false)(animPromise.trySuccess(()))
        case _ => fadeOut(view)(animPromise.trySuccess(()))
      }

      animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def reveal(source: View, view: View, in: Boolean = true)(animationEnd: => Unit = ())(implicit context: ContextWrapper): Unit = {
    val (cx, cy) = calculateAnchorViewPosition(source)
    val fromRadius = source.getWidth / 2
    val toRadius = SnailsUtils.calculateRadius(width = cx + fromRadius, height = cy + fromRadius)

    val (startRadius, endRadius) = if (in) (fromRadius, toRadius) else (toRadius, fromRadius)

    val reveal: Animator = ViewAnimationUtils.createCircularReveal(view, cx + fromRadius, cy + fromRadius, startRadius, endRadius)
    reveal.setDuration(SpeedAnimations.getDuration)
    reveal.addListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = {
        super.onAnimationStart(animation)
        if (in) {
          view.setAlpha(1)
          view.setVisibility(View.VISIBLE)
        }
      }

      override def onAnimationEnd(animation: Animator) = {
        super.onAnimationEnd(animation)
        if (!in) view.setVisibility(View.GONE)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animationEnd
      }
    })
    reveal.setInterpolator(new DecelerateInterpolator)
    reveal.start()
  }

  private[this] def fadeIn(view: View)(animationEnd: => Unit = ())(implicit context: ContextWrapper): Unit = {
    view.setAlpha(0f)
    view.animate()
      .setDuration(SpeedAnimations.getDuration)
      .setInterpolator(new DecelerateInterpolator)
      .alpha(1f)
      .setListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = {
        super.onAnimationStart(animation)
        view.setVisibility(View.VISIBLE)
      }

      override def onAnimationEnd(animation: Animator) = {
        super.onAnimationEnd(animation)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animationEnd
      }
    }).start()
  }

  private[this] def fadeOut(view: View)(animationEnd: => Unit = ())(implicit context: ContextWrapper): Unit = {
    view.setAlpha(1f)
    view.animate()
      .setDuration(SpeedAnimations.getDuration)
      .setInterpolator(new DecelerateInterpolator)
      .alpha(0f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setVisibility(View.GONE)
          view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
          animationEnd
        }
    }).start()
  }

}
