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
      val duration = resGetInteger(R.integer.anim_duration_collection_detail)

      Lollipop ifSupportedThen {
        val radius = SnailsUtils.calculateRadius(x, y, w, h)
        val reveal: Animator = ViewAnimationUtils.createCircularReveal(view, x, y, 0, radius)
        reveal.setInterpolator(new DecelerateInterpolator())
        reveal.setDuration(duration)
        reveal.addListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        })
        reveal.start()
      } getOrElse {
        view.setAlpha(0)
        view
          .animate
          .setDuration(duration)
          .setInterpolator(new AccelerateDecelerateInterpolator())
          .alpha(1f)
          .setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator) {
              super.onAnimationEnd(animation)
              view.setLayerType(View.LAYER_TYPE_NONE, null)
              animPromise.success()
            }
          }).start()
      }
      animPromise.future
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  def revealOut(x: Int, y: Int, w: Int, h: Int)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      val duration = resGetInteger(R.integer.anim_duration_collection_detail)

      Lollipop ifSupportedThen {
        val radius = SnailsUtils.calculateRadius(x, y, w, h)
        val reveal: Animator = ViewAnimationUtils.createCircularReveal(view, x, y, radius, 0)
        reveal.setInterpolator(new AccelerateDecelerateInterpolator())
        reveal.setDuration(duration)
        reveal.addListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            view.setVisibility(View.GONE)
            animPromise.success()
          }
        })
        reveal.start()
      } getOrElse {
        view
          .animate
          .setDuration(duration)
          .setInterpolator(new AccelerateDecelerateInterpolator())
          .alpha(0f)
          .setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator) {
              super.onAnimationEnd(animation)
              view.setLayerType(View.LAYER_TYPE_NONE, null)
              animPromise.success()
            }
          }).start()
      }

      animPromise.future
  }

}
