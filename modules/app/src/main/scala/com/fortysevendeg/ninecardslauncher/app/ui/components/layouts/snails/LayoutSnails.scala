package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object SwipeAnimatedDrawerViewSnails {

  def iconFadeOut(duration: Int)(implicit contextWrapper: ContextWrapper) = Snail[View] { view =>
    val animPromise = Promise[Unit]()
    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val translation = resGetDimensionPixelSize(R.dimen.displacement_vertical_animation_app_drawer)
    view.animate()
      .setInterpolator(new DecelerateInterpolator)
      .translationY(-translation)
      .alpha(0f)
      .setDuration(duration)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) = {
          super.onAnimationEnd(animation)
          runUi(view <~ vUseLayerHardware <~ vTranslationY(0) <~ vAlpha(1) <~ vGone)
          animPromise.trySuccess()
        }
      }).start()
    animPromise.future
  }

}
