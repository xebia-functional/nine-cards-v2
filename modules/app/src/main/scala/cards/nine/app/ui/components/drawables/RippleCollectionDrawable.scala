package cards.nine.app.ui.components.drawables

import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.graphics.drawable.Drawable
import macroid.extras.ResourcesExtras._
import android.graphics._
import android.view.animation.DecelerateInterpolator
import macroid.extras.SnailsUtils
import cards.nine.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher.R
import macroid._

import scala.concurrent.{Future, Promise}

class RippleCollectionDrawable(
  x: Int = 0,
  y: Int = 0,
  circleColor: Int = 0)(implicit contextWrapper: ContextWrapper)
  extends Drawable {

  var percentage: Float = 0

  val duration = SpeedAnimations.getDuration

  lazy val circlePaint = {
    val paint = new Paint
    paint.setColor(circleColor)
    paint
  }

  private[this] val animator: ValueAnimator = {
    val valueAnimation = new ValueAnimator
    valueAnimation.setInterpolator(new DecelerateInterpolator())
    valueAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      override def onAnimationUpdate(value: ValueAnimator) = update(value.getAnimatedValue.asInstanceOf[Float])
    })
    valueAnimation
  }

  override def setColorFilter(cf: ColorFilter): Unit = circlePaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = circlePaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def draw(canvas: Canvas): Unit = {
    val bounds = getBounds
    val radius = SnailsUtils.calculateRadius(x, y, bounds.width(), bounds.height())
    canvas.drawCircle(x, y, radius * percentage, circlePaint)
  }

  def start(): Ui[Future[Any]] = Ui {
    val promise = Promise[Unit]()
    animator.removeAllListeners()
    animator.addListener(new AnimatorListenerAdapter() {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        promise.trySuccess(())
      }
    })
    animator.setFloatValues(0f, 1f)
    animator.setDuration(duration)
    animator.start()
    promise.future
  }

  private[this] def update(p: Float) = {
    percentage = p
    invalidateSelf()
  }
}
