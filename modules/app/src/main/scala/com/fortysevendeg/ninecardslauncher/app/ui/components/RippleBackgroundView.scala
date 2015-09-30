package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.animation.DecelerateInterpolator
import android.view.{View, ViewAnimationUtils, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.SnailsUtils
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.components.RippleBackgroundSnails._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Snail, Tweak}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

class RippleBackgroundView(context: Context, attr: AttributeSet, defStyleAttr: Int)
  extends FrameLayout(context, attr, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val rippleView: View = {
    val rippleView = new View(context)
    rippleView.setVisibility(View.INVISIBLE)
    addView(rippleView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    rippleView
  }

}

object RippleBackgroundSnails {
  def ripple(color: Int, forceFade: Boolean)(implicit contextWrapper: ContextWrapper) = Snail[RippleBackgroundView] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()

      val end = () => {
        view.setLayerType(View.LAYER_TYPE_NONE, null)
        view.rippleView.setVisibility(INVISIBLE)
        view.setBackgroundColor(color)
        animPromise.success()
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

object RippleBackgroundViewTweaks {

  def rbvColor(color: Int, forceFade: Boolean = false)(implicit contextWrapper: ContextWrapper) = Tweak[RippleBackgroundView] { view =>
    runUi(view <~~ ripple(color, forceFade))
  }

}
