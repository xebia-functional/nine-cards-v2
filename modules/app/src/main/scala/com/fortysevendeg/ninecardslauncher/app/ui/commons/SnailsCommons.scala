package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import android.view.View
import android.view.animation.{DecelerateInterpolator, AccelerateInterpolator, AccelerateDecelerateInterpolator}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, Snail, ContextWrapper}

import scala.concurrent.Promise

object SnailsCommons {

  val defaultDelay = 30

  val noDelay = 0

  def showFabMenu(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setScaleX(0)
      view.setScaleY(0)
      view.setVisibility(View.VISIBLE)
      view.animate.
        scaleX(1).
        scaleY(1).
        setInterpolator(new AccelerateDecelerateInterpolator()).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def hideFabMenu(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.animate.
        scaleX(0).
        scaleY(0).
        setInterpolator(new AccelerateDecelerateInterpolator()).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            view.setVisibility(View.GONE)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def animFabMenuItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_normal)
      val translationY = resGetDimensionPixelSize(R.dimen.padding_default)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setVisibility(View.VISIBLE)
      view.setTranslationY(translationY)
      val delay = extractDelay(view)
      view.animate.
        setStartDelay(delay).
        setDuration(duration).
        translationY(0).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def animFabMenuTitleItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_normal)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setVisibility(View.VISIBLE)
      view.setAlpha(0)
      view.animate.
        setStartDelay(extractDelay(view)).
        setDuration(duration).
        setInterpolator(new AccelerateInterpolator()).
        alpha(1).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def animFabMenuIconItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_normal)
      val size = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setVisibility(View.VISIBLE)
      view.setScaleX(0)
      view.setScaleY(0)
      view.setAlpha(0)
      view.setPivotX(size / 2)
      view.setPivotY(size)
      view.animate.
        setStartDelay(extractDelay(view)).
        setDuration(duration).
        setInterpolator(new DecelerateInterpolator()).
        alpha(1).
        scaleX(1).
        scaleY(1).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def fadeBackground(color: Int)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = resGetInteger(R.integer.anim_duration_normal)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val colorFrom = ColorsUtils.setAlpha(color, 0f)
      val colorTo = ColorsUtils.setAlpha(color, 1f)

      val valueAnimator = ValueAnimator.ofInt(0, 100)
      valueAnimator.setDuration(duration)
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        override def onAnimationUpdate(value: ValueAnimator) = {
          val color = interpolateColors(value.getAnimatedFraction, colorFrom, colorTo)
          view.setBackgroundColor(color)
        }
      })
      valueAnimator.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
          animPromise.success()
        }
      })
      valueAnimator.start()
      animPromise.future
  }

  def fadeIn(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setAlpha(0)
      view.setVisibility(View.VISIBLE)
      val animator = view
        .animate
        .setInterpolator(new DecelerateInterpolator())
        .alpha(1f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess()
          }
        })

      animator.setDuration(duration getOrElse resGetInteger(R.integer.anim_duration_normal))
      animator.start()

      animPromise.future
  }

  def fadeOut(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val animator = view
        .animate
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .alpha(0f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            view.setVisibility(View.INVISIBLE)
            animPromise.trySuccess()
          }
        })

      animator.setDuration(duration getOrElse resGetInteger(R.integer.anim_duration_normal))
      animator.start()

      animPromise.future
  }

  def applyAnimation(
    x: Option[Float] = None,
    y: Option[Float] = None,
    xBy: Option[Float] = None,
    yBy: Option[Float] = None,
    alpha: Option[Float] = None,
    scaleX: Option[Float] = None,
    scaleY: Option[Float] = None,
    duration: Option[Long] = None,
    onUpdate: (Float) => Ui[_] = (_) => Ui.nop)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setRunningAnimation(true)
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val animator = view
        .animate
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .setUpdateListener(new AnimatorUpdateListener {
          override def onAnimationUpdate(animation: ValueAnimator): Unit =
            onUpdate(animation.getAnimatedFraction).run
        })
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            view.setRunningAnimation(false)
            animPromise.trySuccess()
          }
        })

      animator.setDuration(duration getOrElse resGetInteger(R.integer.anim_duration_normal))
      x foreach animator.translationX
      y foreach animator.translationY
      xBy foreach animator.translationXBy
      yBy foreach animator.translationYBy
      alpha foreach animator.alpha
      scaleX foreach animator.scaleX
      scaleY foreach animator.scaleY
      animator.start()

      animPromise.future
  }

  private[this] def extractDelay(view: View): Int = view.getPosition match {
    case Some(position) => defaultDelay * position
    case _ => noDelay
  }

}
