package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.view.View._
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.RippleBackgroundView
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Snail}

import scala.concurrent.Promise

object RippleBackgroundSnails {
  def ripple(color: Int, forceFade: Boolean)(implicit contextWrapper: ContextWrapper) = Snail[RippleBackgroundView] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val end = () => {
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        view.rippleView.setVisibility(INVISIBLE)
        view.setBackgroundColor(color)
        animPromise.trySuccess()
      }

      if (forceFade) fadeIn(view, color)(end())
      else Lollipop.ifSupportedThen {
        reveal(view, color)(end())
      } getOrElse {
        fadeIn(view, color)(end())
      }

      animPromise.future
  }

  private[this] def reveal(view: RippleBackgroundView, color: Int)
    (animationEnd: => Unit = ())
    (implicit contextWrapper: ContextWrapper) = {
    val duration = resGetInteger(R.integer.wizard_anim_ripple_duration)
    val x = view.getWidth / 2
    val y = view.getHeight / 2
    val radius = SnailsUtils.calculateRadius(x, y)
    view.rippleView.setVisibility(View.VISIBLE)
    view.rippleView.setBackgroundColor(color)
    val anim = ViewAnimationUtils.createCircularReveal(view.rippleView, x, y, 0, radius)
    anim.setInterpolator(new DecelerateInterpolator())
    anim.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        animationEnd
      }
    })
    anim.setDuration(duration)
    anim.start()
  }

  private[this] def fadeIn(view: RippleBackgroundView, color: Int)
    (animationEnd: => Unit = ())
    (implicit contextWrapper: ContextWrapper): Unit = {
    view.rippleView.setAlpha(0f)
    view.rippleView.setVisibility(View.VISIBLE)
    view.rippleView.setBackgroundColor(color)
    view.rippleView.animate()
      .setInterpolator(new DecelerateInterpolator)
      .alpha(1f)
      .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animationEnd
        }
      }).start()
  }

}
