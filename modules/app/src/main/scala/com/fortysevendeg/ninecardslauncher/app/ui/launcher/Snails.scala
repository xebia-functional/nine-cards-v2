package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import android.view.animation.Animation.AnimationListener
import android.view.animation.{AlphaAnimation, Animation}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object Snails {

  def pagerAppear(implicit context: ContextWrapper): Snail[View] = Snail[View] { view =>
    val duration = resGetInteger(R.integer.anim_duration_pager_appear)
    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val animPromise = Promise[Unit]()
    view.setScaleX(.7f)
    view.setScaleY(.7f)
    view.setAlpha(.7f)
    view.animate.alpha(1).scaleX(1).scaleY(1).setDuration(duration).setListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator) = {
        super.onAnimationEnd(animation)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animPromise.success()
      }
    }).start()
    animPromise.future
  }

  def fade(out: Boolean = false)(implicit context: ContextWrapper): Snail[View] = Snail[View] { view =>
    val duration = resGetInteger(R.integer.anim_duration_pager_appear)
    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val animPromise = Promise[Unit]()
    val animation = if (out) {
      new AlphaAnimation(1, 0)
    } else {
      view.setVisibility(View.VISIBLE)
      new AlphaAnimation(0, 1)
    }
    animation.setDuration(duration)
    animation.setAnimationListener(new AnimationListener {
      override def onAnimationEnd(animation: Animation): Unit = {
        if (out) view.setVisibility(View.INVISIBLE)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animPromise.success()
      }
      override def onAnimationStart(animation: Animation): Unit = {}
      override def onAnimationRepeat(animation: Animation): Unit = {}
    })
    view.startAnimation(animation)
    animPromise.future
  }

}
