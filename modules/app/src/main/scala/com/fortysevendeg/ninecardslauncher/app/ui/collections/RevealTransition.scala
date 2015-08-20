package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.animation.Animator.AnimatorListener
import android.animation.{Animator, AnimatorListenerAdapter, TimeInterpolator}
import android.content.Context
import android.transition.{TransitionValues, Visibility}
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.{View, ViewAnimationUtils, ViewGroup}
import com.fortysevendeg.ninecardslauncher2.R

class RevealTransition(context: Context, attrs: AttributeSet)
  extends Visibility(context, attrs) {

  val sizeIcon = context.getResources.getDimensionPixelOffset(R.dimen.size_icon_collection_detail)

  val paddingDefault = context.getResources.getDimensionPixelOffset(R.dimen.padding_default)

  val positionStartRevealYAxis = paddingDefault + (sizeIcon / 2)

  override def onAppear(
    sceneRoot: ViewGroup,
    view: View,
    startValues: TransitionValues,
    endValues: TransitionValues): Animator = {
    val radius = calculateMaxRadius(view)
    val originalAlpha = view.getAlpha
    view.setAlpha(0f)

    val reveal = createAnimatorAppear(view, 0, radius)
    reveal.addListener(new AnimatorListenerAdapter {
      override def onAnimationStart(animation: Animator): Unit = view.setAlpha(originalAlpha)
    })
    reveal
  }

  override def onDisappear(
    sceneRoot: ViewGroup,
    view: View,
    startValues: TransitionValues,
    endValues: TransitionValues): Animator = createAnimatorDisappear(view, calculateMaxRadius(view), 0)

  private[this] def calculateMaxRadius(view: View): Float = {
    val cat1 = view.getWidth / 2
    val cat2 = view.getHeight - positionStartRevealYAxis
    val widthSquared = cat1 * cat1
    val heightSquared = cat2 * cat2
    math.sqrt(widthSquared + heightSquared).toFloat
  }

  private[this] def createAnimatorAppear(view: View, startRadius: Float, endRadius: Float) = {
    val centerX = view.getWidth / 2
    val centerY = positionStartRevealYAxis
    val reveal = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
    reveal.setInterpolator(new AccelerateDecelerateInterpolator())
    new WrapperAnimator(reveal)
  }

  private[this] def createAnimatorDisappear(view: View, startRadius: Float, endRadius: Float) = {
    val centerX = view.getWidth / 2
    val centerY = view.getHeight / 2
    val reveal = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius)
    reveal.setInterpolator(new AccelerateDecelerateInterpolator())
    new WrapperAnimator(reveal)
  }

}

class WrapperAnimator(wrappedAnimator:  Animator) extends Animator {
  override def getStartDelay: Long = wrappedAnimator.getStartDelay

  override def setStartDelay(startDelay: Long): Unit = wrappedAnimator.setStartDelay(startDelay)

  override def setInterpolator(value: TimeInterpolator): Unit = wrappedAnimator.setInterpolator(value)

  override def isRunning: Boolean = wrappedAnimator.isRunning

  override def getDuration: Long = wrappedAnimator.getDuration

  override def setDuration(duration: Long): Animator = wrappedAnimator.setDuration(duration)

  override def start(): Unit = wrappedAnimator.start()

  override def cancel(): Unit = wrappedAnimator.cancel()

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def addListener(listener: AnimatorListener): Unit = wrappedAnimator.addListener(listener)

  override def removeAllListeners(): Unit = wrappedAnimator.removeAllListeners()

  override def removeListener(listener: AnimatorListener): Unit = wrappedAnimator.removeListener(listener)
}