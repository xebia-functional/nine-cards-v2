package cards.nine.app.ui.commons

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation._
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.{BaseInterpolator, DecelerateInterpolator}
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.extras.DeviceVersion.KitKat
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewTweaks._
import macroid.{ContextWrapper, Snail, Ui}

import scala.concurrent.Promise

object SnailsCommons {

  val defaultDelay = 30

  val noDelay = 0

  def showFabMenu(implicit context: ContextWrapper): Snail[View] = {
    vVisible + vScaleX(0) + vScaleY(0) ++
      applyAnimation(
        scaleX = Option(1),
        scaleY = Option(1))
  }

  def hideFabMenu(implicit context: ContextWrapper): Snail[View] =
    applyAnimation(scaleX = Option(0), scaleY = Option(0)) + vGone

  def animFabMenuItem(position: Option[Int])(implicit context: ContextWrapper): Snail[View] = {
    val translationY = resGetDimensionPixelSize(R.dimen.padding_default)
    vVisible + vTranslationY(translationY) ++
      applyAnimation(y = Option(0), startDelay = Option(calculateDelay(position)))
  }

  def animFabMenuTitleItem(position: Option[Int])(implicit context: ContextWrapper): Snail[View] =
    vVisible + vAlpha(0) ++ applyAnimation(alpha = Option(1), startDelay = Option(calculateDelay(position)))

  def animFabMenuIconItem(position: Option[Int])(implicit context: ContextWrapper): Snail[View] = {
    val size = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
    vVisible + vAlpha(0) + vScaleX(0) + vScaleY(0) + vPivotX(size / 2) + vPivotY(size) ++
      applyAnimation(
        alpha = Option(1),
        scaleX = Option(1),
        scaleY = Option(1),
        startDelay = Option(calculateDelay(position)))
  }

  def applyFadeIn(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] =
    vVisible + vAlpha(0) ++ applyAnimation(alpha = Some(1), duration = duration)

  def applyFadeOut(duration: Option[Long] = None)(implicit context: ContextWrapper): Snail[View] =
    applyAnimation(alpha = Some(0), duration = duration) + vInvisible + vAlpha(1)

  @SuppressLint(Array("NewApi"))
  def applyAnimation(
    startDelay: Option[Long] = None,
    x: Option[Float] = None,
    y: Option[Float] = None,
    xBy: Option[Float] = None,
    yBy: Option[Float] = None,
    alpha: Option[Float] = None,
    scaleX: Option[Float] = None,
    scaleY: Option[Float] = None,
    interpolator: Option[BaseInterpolator] = Option(new DecelerateInterpolator()),
    duration: Option[Long] = None,
    onUpdate: (Float) => Ui[_] = (_) => Ui.nop)(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setRunningAnimation(true)
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()

      val animator = view
        .animate
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            view.setRunningAnimation(false)
            animPromise.trySuccess(())
          }
        })
      KitKat.ifSupportedThen(animator
        .setUpdateListener(new AnimatorUpdateListener {
          override def onAnimationUpdate(animation: ValueAnimator): Unit =
            onUpdate(animation.getAnimatedFraction).run
        }))
      animator.setDuration(duration getOrElse SpeedAnimations.getDuration)
      interpolator foreach animator.setInterpolator
      startDelay foreach animator.setStartDelay
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

  private[this] def calculateDelay(position: Option[Int]): Int = position match {
    case Some(p) => defaultDelay * p
    case _ => noDelay
  }

}
