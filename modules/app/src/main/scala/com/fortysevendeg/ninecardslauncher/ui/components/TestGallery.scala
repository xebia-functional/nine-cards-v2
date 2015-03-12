package com.fortysevendeg.ninecardslauncher.ui.components

import android.animation.{ValueAnimator, Animator, AnimatorListenerAdapter, ObjectAnimator}
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.{Gravity, ViewGroup}
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view.animation.DecelerateInterpolator
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import android.widget.{LinearLayout, TextView}
import macroid.{ActivityContext, AppContext}
import macroid.FullDsl._
import FrameLayoutGallery._

class TestGallery(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends FrameLayoutGallery(context, attr, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext, activityContext: ActivityContext) = this(context, attr, 0)

  val durationAnimation = appContext.get.getResources.getInteger(android.R.integer.config_shortAnimTime)

  val mainAnimator: ObjectAnimator = new ObjectAnimator

  val hideAfterAnimationListener = new AnimatorListenerAdapter() {
    override def onAnimationEnd(animation: Animator) {
      new Handler().postDelayed(new Runnable() {
        def run() {
          swapViews()
        }
      }, 1)
      super.onAnimationEnd(animation)
    }
  }

  var previousView = slot[TestGalleryHolder]
  var nextView = slot[TestGalleryHolder]
  var frontView = slot[TestGalleryHolder]
  var displacement: Float = 0

  var data = List("1", "2", "3", "4", "5", "6")

  var currentItem = 0

  createViews()

  private def createViews() = {
    val previous = new TestGalleryHolder(Color.RED)
    val next = new TestGalleryHolder(Color.GREEN)
    val front = new TestGalleryHolder(Color.BLUE)
    previousView = Some(previous)
    nextView = Some(next)
    frontView = Some(front)
    val params = new LayoutParams(MATCH_PARENT, MATCH_PARENT)
    runUi(
      (this <~ vgAddView(previous, params)) ~
        (this <~ vgAddView(next, params)) ~
        (this <~ vgAddView(front, params))
    )
    reset()
  }

  private def getSizeWidget = if (horizontalGallery) getWidth else getHeight

  override def isFirst: Boolean = currentItem == 0

  override def isLast: Boolean = currentItem == data.length - 1

  override def snap(velocity: Float): Unit = {
    mainAnimator.cancel()
    val destiny = velocity match {
      case v if v > 0 && displacement > 0 => getSizeWidget
      case v if v <= 0 && displacement < 0 => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
  }

  override def snapDestination(): Unit = {
    val destiny = displacement match {
      case d if d > getSizeWidget * .6f => getSizeWidget
      case d if d < -getSizeWidget * .6f => -getSizeWidget
      case _ => 0
    }
    animateViews(destiny, durationAnimation)
    invalidate()
  }

  override def performScroll(delta: Float): Unit = {
    mainAnimator.removeAllListeners()
    mainAnimator.cancel()
    displacement = math.max(-getSizeWidget, Math.min(getSizeWidget, displacement - delta))
    if (displacement > 0) {
      runUi((previousView <~ vVisible) ~ (nextView <~ vGone))
    } else {
      runUi((previousView <~ vGone) ~ (nextView <~ vVisible))
    }
    applyTranslation(frontView, displacement)
    transformPanelCanvas()
  }

  private def applyTranslation(view: Option[ViewGroup], translate: Float) =
    runUi(view <~ (if (horizontalGallery) vTranslationX(translate) else vTranslationY(translate)))


  private def transformPanelCanvas() = {
    val percent = math.abs(displacement) / getSizeWidget
    val fromLeft = displacement > 0
    applyTransformer(if (fromLeft) previousView else nextView, percent, fromLeft)
  }

  private def applyTransformer(view: Option[ViewGroup], percent: Float, fromLeft: Boolean) = {
    val ratio = (percent * .6f) + .4f
    runUi(
      view <~ vAlpha(ratio) <~ vScaleX(ratio) <~ vScaleY(ratio)
    )
    val translate = {
      val start = if (fromLeft) -(getSizeWidget * .4f) else getSizeWidget - (getSizeWidget * .4f)
      start - (start * percent)
    }
    applyTranslation(view, translate)
  }

  private def animateViews(dest: Int, duration: Int) = {
    mainAnimator.setFloatValues(displacement, dest)
    mainAnimator.setDuration(duration)
    if (dest != 0) {
      mainAnimator.addListener(hideAfterAnimationListener)
    } else {
      mainAnimator.removeAllListeners()
    }
    mainAnimator.start()
  }

  private def next(): Unit = {
    val auxFrom = frontView
    val auxLeft = previousView
    frontView = nextView
    previousView = auxFrom
    nextView = auxLeft
    currentItem = currentItem + 1
    if (currentItem > data.size - 1) currentItem = 0
  }

  private def previous(): Unit = {
    val auxFrom = frontView
    val auxRight = nextView
    frontView = previousView
    nextView = auxFrom
    previousView = auxRight
    currentItem = currentItem - 1
    if (currentItem < 0) currentItem = data.length - 1
  }

  private def swapViews(): Unit = {
    if (displacement < 0) next() else previous()
    reset()
  }

  private def reset(): Unit = {
    if (data.length > currentItem) {
      populateView(frontView, data(currentItem), currentItem)

      val positionLeft: Int = if (currentItem - 1 < 0) data.length - 1 else currentItem - 1
      populateView(previousView, data(positionLeft), positionLeft)

      val positionRight: Int = if (currentItem + 1 > data.length - 1) 0 else currentItem + 1
      populateView(nextView, data(positionRight), positionRight)
    }

    runUi(
      (this <~ flgEnabled(data.nonEmpty && data.length > 1)) ~
        (frontView <~ vClearAnimation <~ vVisible <~ vBringToFront) ~
        (nextView <~ vGone <~ vBringToFront) ~
        (previousView <~ vGone)
    )

    mainAnimator.removeAllListeners()
    mainAnimator.cancel()

    applyTranslation(frontView, displacement)
    applyTranslation(nextView, getSizeWidget)
    applyTranslation(previousView, -getSizeWidget)

    frontView map {
      front =>
        mainAnimator.setTarget(front)
        mainAnimator.setPropertyName(if (horizontalGallery) "translationX" else "translationY")
        mainAnimator.setFloatValues(0, 0)
        mainAnimator.setInterpolator(new DecelerateInterpolator())
        mainAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          override def onAnimationUpdate(arg0: ValueAnimator) {
            displacement = arg0.getAnimatedValue.asInstanceOf[Float]
            transformPanelCanvas()
          }
        })
    }
  }

  private def populateView(view: Option[TestGalleryHolder], data: String, position: Int) = {
    view map {
      v =>
        runUi(v.text <~ tvText(data))
    }
  }

}

class TestGalleryHolder(color: Int)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LinearLayout(activityContext.get) {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(80) <~ vBackgroundColor(color) <~ tvColor(Color.WHITE) <~ tvText("-") <~ tvGravity(Gravity.CENTER)
    )
  )

}