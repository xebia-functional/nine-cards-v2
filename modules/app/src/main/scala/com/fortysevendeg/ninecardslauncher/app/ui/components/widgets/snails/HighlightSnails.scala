package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Snail, ContextWrapper}

import scala.concurrent.Promise

object HighlightSnails {

  val default = 1f

  def highlight(magnify: Float)(implicit contextWrapper: ContextWrapper) = Snail[View] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_normal)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      view.animate().
        scaleX(magnify).
        scaleY(magnify).
        setDuration(duration).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            animPromise.success()
          }
        })
        .start()

      animPromise.future
  }

}
