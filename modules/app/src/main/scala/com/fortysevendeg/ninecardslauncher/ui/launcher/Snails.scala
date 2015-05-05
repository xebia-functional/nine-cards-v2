package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{AppContext, Snail}

import scala.concurrent.Promise
object Snails {

  def pagerAppear(implicit appContext: AppContext): Snail[View] = Snail[View] {
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

}
