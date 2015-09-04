package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import android.animation.{Animator, AnimatorListenerAdapter}
import android.annotation.TargetApi
import android.os.Build
import android.view.animation.{AccelerateDecelerateInterpolator, DecelerateInterpolator}
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object ActionsSnails {

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  def revealIn(x: Int, y: Int, w: Int, h: Int)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      val duration = resGetInteger(R.integer.anim_duration_normal)

      Lollipop ifSupportedThen {
        val startRadius = resGetDimensionPixelSize(R.dimen.size_fab_menu_item) / 2
        val endRadius = SnailsUtils.calculateRadius(x, y, w, h)
        circularReveal(view, x, y, w, h, duration, startRadius, endRadius, animPromise.success())
      } getOrElse {
        fadeIn(view, duration, animPromise.success())
      }
      animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  def revealOut(x: Int, y: Int, w: Int, h: Int)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      val duration = resGetInteger(R.integer.anim_duration_normal)

      Lollipop ifSupportedThen {
        val startRadius = SnailsUtils.calculateRadius(x, y, w, h)
        circularReveal(view, x, y, w, h, duration, startRadius, 0, {
          view.setVisibility(View.GONE)
          animPromise.success()
        })
      } getOrElse {
        fadeOut(view, duration, animPromise.success())
      }

      animPromise.future
  }

  def scaleToToolbar(radioScale: Float)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      val duration = resGetInteger(R.integer.anim_duration_normal)
      view.setPivotY(0)
      view
        .animate
        .setDuration(duration)
        .setInterpolator(new DecelerateInterpolator())
        .scaleY(radioScale)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def showContent()(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      val duration = resGetInteger(R.integer.anim_duration_normal)
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
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

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
        view.setLayerType(View.LAYER_TYPE_NONE, null)
        animationEnd
      }
    })
    reveal.start()
  }

  private[this] def fadeIn(view: View, duration: Int, animationEnd: => Unit) = {
    view.setAlpha(0)
    view
      .animate
      .setDuration(duration)
      .setInterpolator(new DecelerateInterpolator())
      .alpha(1f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animationEnd
        }
      }).start()
  }

  private[this] def fadeOut(view: View, duration: Int, animationEnd: => Unit) = {
    view
      .animate
      .setDuration(duration)
      .setInterpolator(new AccelerateDecelerateInterpolator())
      .alpha(0f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animationEnd
        }
      }).start()
  }

}
