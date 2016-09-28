package com.fortysevendeg.ninecardslauncher.app.ui.collections.snails

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.graphics.Point
import android.view.View
import android.view.animation.{AccelerateDecelerateInterpolator, DecelerateInterpolator}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

import scala.concurrent.Promise

object CollectionsSnails {

  def animationEnterTitle(implicit context: ContextWrapper) = {
    val distance = resGetDimensionPixelSize(R.dimen.padding_large)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    vTranslationY(distance) +
      vVisible +
      vAlpha(0) ++
      applyAnimation(
      duration = Option(duration),
      y = Option(0),
      alpha = Option(1))
  }

  def animationOutTitle(implicit context: ContextWrapper) = {
    val distance = resGetDimensionPixelSize(R.dimen.padding_large)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    applyAnimation(
      duration = Option(duration),
      y = Option(distance),
      alpha = Option(1)) +
      vGone
  }

  def animationIcon(
    fromLeft: Boolean,
    resIcon: Int)(implicit context: ContextWrapper) = {
    val distance = if (fromLeft) -resGetDimensionPixelSize(R.dimen.padding_default) else resGetDimensionPixelSize(R.dimen.padding_default)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    applyAnimation(
        duration = Option(duration),
        x = Option(-distance),
        alpha = Option(0),
        scaleX = Option(0.7f),
        scaleY = Option(0.7f)) +
      vTranslationX(distance) +
      ivSrc(resIcon) ++
      applyAnimation(
        duration = Option(duration),
        x = Option(0),
        alpha = Option(1),
        scaleX = Option(1f),
        scaleY = Option(1f)) +
      vRotation(0)
  }

  private[this] def createToolbarSnail(in: Boolean)(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] = Snail[View]{ view =>

    val display = activityContextWrapper.getOriginal.getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    val height = size.y
    val heightToolbar = resGetDimensionPixelSize(R.dimen.height_toolbar_collection_details)

    view.clearAnimation()
    view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
    val animPromise = Promise[Unit]()

    val animator = new ValueAnimator()
    if (in) animator.setIntValues(height, heightToolbar) else animator.setIntValues(heightToolbar, height)
    animator.setDuration(SpeedAnimations.getDuration)
    animator.setInterpolator(new DecelerateInterpolator())
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
        animPromise.trySuccess(())
      }
    })
    animator.addUpdateListener(new AnimatorUpdateListener {
      override def onAnimationUpdate(animation: ValueAnimator): Unit =
        (view <~ vResize(height = Option(animation.getAnimatedValue.asInstanceOf[Int]))).run
    })
    animator.start()
    animPromise.future
  }

  def enterToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] =
    createToolbarSnail(in = true)

  def exitToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] =
    createToolbarSnail(in = false)

  def enterViews(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setAlpha(0)
      view
        .animate
        .setDuration(SpeedAnimations.getDuration)
        .setInterpolator(new DecelerateInterpolator())
        .alpha(1f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def exitViews(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val move = resGetDimensionPixelSize(R.dimen.space_enter_views_collection_detail)
      view
        .animate
        .setDuration(SpeedAnimations.getDuration)
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .translationY(move)
        .alpha(0)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

}
