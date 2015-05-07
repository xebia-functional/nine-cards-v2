package com.fortysevendeg.ninecardslauncher.ui.collections

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{AppContext, Snail}

import scala.concurrent.Promise

object Snails {

  def changeIcon(resDrawable: Int, fromLeft: Boolean)(implicit appContext: AppContext): Snail[ImageView] = Snail[ImageView] {
    view ⇒
      val distance = if (fromLeft) -resGetDimensionPixelSize(R.dimen.padding_default) else resGetDimensionPixelSize(R.dimen.padding_default)
      val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.animate.translationX(-distance).alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(duration).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setTranslationX(distance)
          view.setImageResource(resDrawable)
          view.animate.translationX(0).alpha(1).scaleX(1).scaleY(1).setDuration(duration).setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator) {
              super.onAnimationEnd(animation)
              view.setRotation(0)
              view.setLayerType(View.LAYER_TYPE_NONE, null)
              animPromise.success()
            }
          }).start()
        }
      }).start()
      animPromise.future
  }

}
