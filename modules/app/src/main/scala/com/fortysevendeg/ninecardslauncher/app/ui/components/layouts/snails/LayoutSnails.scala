package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.SwipeAnimatedDrawerView
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Snail}
import macroid.FullDsl._

import scala.concurrent.Promise

object SwipeAnimatedDrawerViewSnails {

  def animatedClose(duration: Int) = Snail[SwipeAnimatedDrawerView] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val end = () => {
        runUi(
          (view <~ vUseLayerHardware <~ vGone) ~
            (view.rippleView <~ vInvisible))
        animPromise.trySuccess()
      }

      Lollipop.ifSupportedThen {
        unreveal(view, duration)(end())
      } getOrElse {
        fadeOut(view, duration)(end())
      }

      animPromise.future
  }

  private[this] def unreveal(view: SwipeAnimatedDrawerView, duration: Int)
    (animationEnd: => Unit = ()) = {
    val x = view.getWidth / 2
    val y = view.getHeight / 2
    val radius = SnailsUtils.calculateRadius(x, y)
    runUi(view.rippleView <~ vVisible)
    view.rippleView map { rippleView =>
      val anim = ViewAnimationUtils.createCircularReveal(rippleView, x, y, 0, radius)
      anim.setInterpolator(new DecelerateInterpolator())
      anim.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) = {
          super.onAnimationEnd(animation)
          animationEnd
        }
      })
      anim.setDuration(duration)
      anim.start()
    } getOrElse animationEnd

  }

  private[this] def fadeOut(view: SwipeAnimatedDrawerView, duration: Int)
    (animationEnd: => Unit = ()): Unit = {
    view.animate()
      .setInterpolator(new DecelerateInterpolator)
      .alpha(0f)
      .setDuration(duration)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) = {
          super.onAnimationEnd(animation)
          animationEnd
        }
      }).start()
  }

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
