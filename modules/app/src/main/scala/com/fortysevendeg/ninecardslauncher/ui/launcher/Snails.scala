package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import macroid.Snail

import scala.concurrent.Promise
import scala.util.Success

object Snails {

  def pagerAppear(): Snail[View] = Snail[View] {
    view ⇒
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.setScaleX(.7f)
      view.setScaleY(.7f)
      view.setAlpha(.7f)
      view.animate.alpha(1).scaleX(1).scaleY(1).setDuration(175).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

}
