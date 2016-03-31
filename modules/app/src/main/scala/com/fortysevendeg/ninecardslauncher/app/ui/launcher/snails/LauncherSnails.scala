package com.fortysevendeg.ninecardslauncher.app.ui.launcher.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object LauncherSnails {

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

  def fade(out: Boolean = false)(implicit context: ContextWrapper): Snail[View] = {
    val duration = resGetInteger(R.integer.anim_duration_pager_appear)
    if (out) {
      fadeOut(Some(duration))
    } else {
      fadeIn(Some(duration))
    }
  }

}
