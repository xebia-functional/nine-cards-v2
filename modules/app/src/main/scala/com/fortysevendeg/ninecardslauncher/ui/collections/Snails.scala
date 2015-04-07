package com.fortysevendeg.ninecardslauncher.ui.collections

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import android.widget.ImageView
import macroid.Snail

import scala.concurrent.Promise
import scala.util.Success

object Snails {

  def changeIcon(resDrawable: Int): Snail[ImageView] = Snail[ImageView] {
    view ⇒
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.animate.rotation(180).alpha(0.5f).scaleX(0.7f).scaleY(0.7f).setDuration(100).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setImageResource(resDrawable)
          view.animate.rotation(360).alpha(1).scaleX(1).scaleY(1).setDuration(100).setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator) {
              super.onAnimationEnd(animation)
              view.setRotation(0)
              view.setLayerType(View.LAYER_TYPE_NONE, null)
              animPromise.complete(Success(()))
            }
          }).start()
        }
      }).start()
      animPromise.future
  }

}
