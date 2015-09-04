package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.animation._
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Snail, ContextWrapper}

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

object SnailsCommons {

  val maxFadeBackground = 0.7f

  val defaultDelay = 60

  val noDelay = 0

  def showFabMenu(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
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
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def hideFabMenu(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.animate.
        scaleX(0).
        scaleY(0).
        setInterpolator(new AccelerateDecelerateInterpolator()).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            view.setVisibility(View.GONE)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def showFabMenuItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = resGetDimensionPixelSize(R.dimen.padding_large)
      val translationY = resGetDimensionPixelSize(R.dimen.padding_large)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.setTranslationY(translationY)
      view.setAlpha(0)
      view.setVisibility(View.VISIBLE)
      view.animate.
        setStartDelay(extractDelay(view)).
        setInterpolator(new AccelerateDecelerateInterpolator()).
        alpha(1).
        translationY(0).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def hideFabMenuItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val translationY = resGetDimensionPixelSize(R.dimen.padding_large)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()
      view.setVisibility(View.VISIBLE)
      view.animate.
        setStartDelay(0).
        alpha(0).
        setInterpolator(new AccelerateDecelerateInterpolator()).
        translationY(translationY).
        setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator) = {
            super.onAnimationEnd(animation)
            view.setVisibility(View.GONE)
            view.setLayerType(View.LAYER_TYPE_NONE, null)
            animPromise.success()
          }
        }).start()
      animPromise.future
  }

  def fadeBackground(in: Boolean)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      val animPromise = Promise[Unit]()

      val (fadeStart, fadeEnd) = if (in) {
        (0f, maxFadeBackground)
      } else {
        (maxFadeBackground, 0f)
      }

      val colorFrom = ColorsUtils.setAlpha(Color.BLACK, fadeStart)
      val colorTo = ColorsUtils.setAlpha(Color.BLACK, fadeEnd)

      val valueAnimator = ValueAnimator.ofInt(0, 100)
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        override def onAnimationUpdate(value: ValueAnimator) = {
          val color = interpolateColors(value.getAnimatedFraction, colorFrom, colorTo)
          view.setBackgroundColor(color)
        }
      })
      valueAnimator.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, null)
          animPromise.success()
        }
      })
      valueAnimator.start()
      animPromise.future
  }

  private[this] def extractDelay(view: View): Int = Option(view.getTag(R.id.fab_menu_position)) match {
    case Some(position) => Try(defaultDelay * Int.unbox(position)) match {
      case Success(delay) => delay
      case Failure(_) => noDelay
    }
    case _ => noDelay
  }

}
