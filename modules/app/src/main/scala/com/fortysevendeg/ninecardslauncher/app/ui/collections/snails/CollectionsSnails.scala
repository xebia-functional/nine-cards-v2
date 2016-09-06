package com.fortysevendeg.ninecardslauncher.app.ui.collections.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.graphics.Point
import android.view.View
import android.view.animation.{AccelerateDecelerateInterpolator, DecelerateInterpolator}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, ContextWrapper, Snail}

import scala.concurrent.Promise

object CollectionsSnails {

  def changeIcon(resDrawable: Int, fromLeft: Boolean)(implicit context: ContextWrapper): Snail[ImageView] = Snail[ImageView] {
    view =>
      val distance = if (fromLeft) -resGetDimensionPixelSize(R.dimen.padding_default) else resGetDimensionPixelSize(R.dimen.padding_default)
      val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.animate.translationX(-distance).alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(duration).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setTranslationX(distance)
          view.setImageResource(resDrawable)
          view.animate.translationX(0).alpha(1).scaleX(1).scaleY(1).setDuration(duration).setListener(new AnimatorListenerAdapter {
            override def onAnimationEnd(animation: Animator): Unit = {
              super.onAnimationEnd(animation)
              view.setRotation(0)
              view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
              animPromise.trySuccess(())
            }
          }).start()
        }
      }).start()
      animPromise.future
  }

  def enterToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] = {
    val display = activityContextWrapper.getOriginal.getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    val height = size.y
    val times = height.toFloat / resGetDimension(R.dimen.height_toolbar_collection_details)
    vScaleY(times) ++ applyAnimation(scaleY = Some(1))
  }

  def exitToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] = {
    val display = activityContextWrapper.getOriginal.getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    val height = size.y
    val times = (height.toFloat / resGetDimension(R.dimen.height_toolbar_collection_details)) * 2
    applyAnimation(scaleY = Some(times))
  }

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
