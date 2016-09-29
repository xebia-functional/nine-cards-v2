package cards.nine.app.ui.commons

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation._
import cards.nine.app.ui.commons.ops.ViewOps._
import android.view.View
import android.view.animation.{AccelerateDecelerateInterpolator, AccelerateInterpolator, DecelerateInterpolator}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import cards.nine.app.ui.commons.ops.ColorOps._
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Snail, Ui}

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
            animPromise.trySuccess(())
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
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def animFabMenuItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = SpeedAnimations.getDuration
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
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def animFabMenuTitleItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = SpeedAnimations.getDuration
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
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def animFabMenuIconItem(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = SpeedAnimations.getDuration
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
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def fadeBackground(color: Int)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      val duration = SpeedAnimations.getDuration
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val colorFrom = color.alpha(0f)
      val colorTo = color.alpha(1f)

      val valueAnimator = ValueAnimator.ofInt(0, 100)
      valueAnimator.setDuration(duration)
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        override def onAnimationUpdate(value: ValueAnimator) = {
          val color = (colorFrom, colorTo).interpolateColors(value.getAnimatedFraction)
          view.setBackgroundColor(color)
        }
      })
      valueAnimator.addListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          super.onAnimationEnd(animation)
          view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
          animPromise.trySuccess(())
        }
      })
      valueAnimator.start()
      animPromise.future
  }

  def applyFadeIn(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] =
    vVisible + vAlpha(0) ++ applyAnimation(alpha = Some(1), duration = duration)

  def applyFadeOut(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] =
    applyAnimation(alpha = Some(0), duration = duration) + vInvisible + vAlpha(1)

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
            animPromise.trySuccess(())
          }
        })

      animator.setDuration(duration getOrElse SpeedAnimations.getDuration)
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
