package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object Snails {

  def pagerAppear(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view ⇒
      val duration = resGetInteger(R.integer.anim_duration_pager_appear)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.setScaleX(.7f)
      view.setScaleY(.7f)
      view.setAlpha(.7f)
      view.animate.alpha(1).scaleX(1).scaleY(1).setDuration(duration).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animPromise.success()
        }
      }).start()
      animPromise.future
  }

  def revealInAppDrawer(source: View)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view ⇒
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()

      Lollipop.ifSupportedThen {
        reveal(source, view)(animPromise.success())
      } getOrElse {
        fadeIn(view)(animPromise.success())
      }

      animPromise.future
  }

  def revealOutAppDrawer(source: View)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view ⇒
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()

      Lollipop.ifSupportedThen {
        reveal(source, view, in = false)(animPromise.success())
      } getOrElse {
        fadeOut(view)(animPromise.success())
      }

      animPromise.future
  }

  private[this] def reveal(source: View, view: View, in: Boolean = true)(animationEnd: => Unit = ())(implicit context: ContextWrapper): Unit = {
    val sb = resGetDimensionPixelSize("status_bar_height") getOrElse 25 dp
    // TODO - Use the PositionUtils
    val (drawerButtonSize, cx, cy) = {
      val location = new Array[Int](2)
      source.getLocationOnScreen(location)
      (source.getWidth, location(0) + source.getWidth / 2, location(1) + source.getHeight / 2 - sb)
    }

    val fromRadius = drawerButtonSize / 2
    val toRadius = SnailsUtils.calculateRadius(width = cx, height = cy)

    val (startRadius, endRadius) = if (in) (fromRadius, toRadius) else (toRadius, fromRadius)

    val reveal: Animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, endRadius)
    reveal.addListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = {
        super.onAnimationStart(animation)
        if (in) view.setVisibility(View.VISIBLE)
      }

      override def onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        if (!in) view.setVisibility(View.GONE)
        view.setLayerType(View.LAYER_TYPE_NONE, null)
        animationEnd
      }
    })
    reveal.setInterpolator(new DecelerateInterpolator)
    reveal.start()
  }

  private[this] def fadeIn(view: View)(animationEnd: => Unit = ()): Unit = {
    view.setAlpha(0f)
    view.animate()
      .setInterpolator(new DecelerateInterpolator)
      .alpha(1f)
      .setListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = {
        super.onAnimationStart(animation)
        view.setVisibility(View.VISIBLE)
      }

      override def onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        view.setLayerType(View.LAYER_TYPE_NONE, null)
        animationEnd
      }
    }).start()
  }

  private[this] def fadeOut(view: View)(animationEnd: => Unit = ()): Unit = {
    view.setAlpha(1f)
    view.animate()
      .setInterpolator(new DecelerateInterpolator)
      .alpha(0f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setVisibility(View.GONE)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animationEnd
        }
    }).start()
  }

}
