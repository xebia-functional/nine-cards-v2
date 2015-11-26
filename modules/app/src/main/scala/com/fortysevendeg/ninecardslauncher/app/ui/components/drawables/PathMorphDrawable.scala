package com.fortysevendeg.ninecardslauncher.app.ui.components.drawables

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.graphics.Paint.Style
import android.graphics._
import android.graphics.drawable.{Animatable, Drawable}
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.IconTypes._
import macroid.{ContextWrapper, Tweak}

import scala.util.Try

case class PathMorphDrawable(
  defaultIcon: Int = BACK,
  defaultStroke: Int = 3,
  defaultColor: Int = Color.WHITE,
  padding: Int = 0)(implicit context: ContextWrapper)
  extends Drawable
  with Animatable
  with PathMorphDrawableTypes {

  implicit var size: Option[Dim] = None

  lazy val burgerIcon = List(
    new Segment().fromRatios(0.2f, 0.3f, 0.8f, 0.3f),
    new Segment().fromRatios(0.2f, 0.5f, 0.8f, 0.5f),
    new Segment().fromRatios(0.2f, 0.7f, 0.8f, 0.7f)
  )

  lazy val backIcon = List(
    new Segment().fromRatios(0.3f, 0.51f, 0.5f, 0.3f),
    new Segment().fromRatios(0.33f, 0.5f, 0.7f, 0.5f),
    new Segment().fromRatios(0.3f, 0.49f, 0.5f, 0.7f)
  )

  lazy val upIcon = List(
    new Segment().fromRatios(0.49f, 0.3f, 0.7f, 0.5f),
    new Segment().fromRatios(0.5f, 0.33f, 0.5f, 0.7f),
    new Segment().fromRatios(0.51f, 0.3f, 0.3f, 0.5f)
  )

  lazy val downIcon = List(
    new Segment().fromRatios(0.51f, 0.7f, 0.3f, 0.5f),
    new Segment().fromRatios(0.5f, 0.67f, 0.5f, 0.3f),
    new Segment().fromRatios(0.49f, 0.7f, 0.7f, 0.5f)
  )

  lazy val nextIcon = List(
    new Segment().fromRatios(0.7f, 0.49f, 0.5f, 0.7f),
    new Segment().fromRatios(0.67f, 0.5f, 0.3f, 0.5f),
    new Segment().fromRatios(0.7f, 0.51f, 0.5f, 0.3f)
  )

  lazy val checkIcon = List(
    new Segment().fromRatios(0.2f, 0.6f, 0.4f, 0.8f),
    new Segment().fromRatios(0.4f, 0.8f, 0.8f, 0.2f)
  )

  lazy val addIcon = List(
    new Segment().fromRatios(0.5f, 0.2f, 0.5f, 0.8f),
    new Segment().fromRatios(0.2f, 0.5f, 0.8f, 0.5f)
  )

  lazy val closeIcon = List(
    new Segment().fromRatios(0.662f, 0.338f, 0.338f, 0.662f),
    new Segment().fromRatios(0.338f, 0.338f, 0.662f, 0.662f)
  )

  val noIcon = List.empty

  val iconPaint: Paint = {
    val paint = new Paint
    paint.setAntiAlias(true)
    paint.setStyle(Style.STROKE)
    paint.setStrokeWidth(defaultStroke)
    paint.setColor(defaultColor)
    paint
  }

  var currentTypeIcon = defaultIcon

  var running = false

  var currentIcon: Option[Icon] = None

  var toIcon: Option[Icon] = None

  var transformIcon: Option[Icon] = None

  override def onBoundsChange(bounds: Rect): Unit = {
    super.onBoundsChange(bounds)
    size = Some(new Dim(bounds.width(), bounds.height()))
    setTypeIcon(defaultIcon)
  }

  override def draw(canvas: Canvas): Unit = if (running) {
    transformIcon.foreach(drawIcon(canvas, _))
  } else {
    currentIcon.foreach(drawIcon(canvas, _))
  }


  override def setColorFilter(cf: ColorFilter): Unit = iconPaint.setColorFilter(cf)

  override def setAlpha(alpha: Int): Unit = iconPaint.setAlpha(alpha)

  override def getOpacity: Int = PixelFormat.TRANSPARENT

  override def stop(): Unit = {
    toIcon foreach setIcon
    toIcon = None
    running = false
  }

  override def isRunning: Boolean = running

  override def start(): Unit = (toIcon, currentIcon) match {
    case (Some(to), Some(current)) => running = true; moveIcon(current, to)
    case (Some(to), None) => setIcon(to); toIcon = None
    case _ => ()
  }


  def setColor(color: Int): Unit = {
    iconPaint.setColor(color)
    invalidateSelf()
  }

  def setColorResource(color: Int): Unit = {
    iconPaint.setColor(context.application.getResources.getColor(color))
    invalidateSelf()
  }

  def setStroke(stroke: Float) = {
    iconPaint.setStrokeWidth(stroke)
    invalidateSelf()
  }

  def setTransformIcon(icon: Icon) = {
    transformIcon = Some(icon)
    invalidateSelf()
  }

  def setIcon(icon: Icon) = {
    currentIcon = Some(icon)
    invalidateSelf()
  }

  def setToIcon(icon: Icon) = toIcon = Some(icon)

  def setTypeIcon(icon: Int) = {
    currentTypeIcon = icon
    icon match {
      case ADD => setIcon(addIcon)
      case BACK => setIcon(backIcon)
      case BURGER => setIcon(burgerIcon)
      case CHECK => setIcon(checkIcon)
      case CLOSE => setIcon(closeIcon)
      case DOWN => setIcon(downIcon)
      case NEXT => setIcon(nextIcon)
      case NOICON => setIcon(noIcon)
      case UP => setIcon(upIcon)
    }
  }

  def setToTypeIcon(icon: Int) = {
    currentTypeIcon = icon
    icon match {
      case ADD => setToIcon(addIcon)
      case BACK => setToIcon(backIcon)
      case BURGER => setToIcon(burgerIcon)
      case CHECK => setToIcon(checkIcon)
      case CLOSE => setToIcon(closeIcon)
      case DOWN => setToIcon(downIcon)
      case NEXT => setToIcon(nextIcon)
      case NOICON => setToIcon(noIcon)
      case UP => setToIcon(upIcon)
    }
  }

  private[this] def drawIcon(canvas: Canvas, icon: Icon): Unit = icon foreach (drawSegment(canvas, _))

  private[this] def drawSegment(canvas: Canvas, segment: Segment): Unit = {
    iconPaint.setAlpha((segment.alpha * 255).toInt)
    val p1 = recalculatePointByPadding(segment.point1)
    val p2 = recalculatePointByPadding(segment.point2)
    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, iconPaint)
  }

  private[this] def recalculatePointByPadding(pos: Point): Point = size map {
    s =>
      val newW = s.wight - (padding * 2)
      val newH = s.height - (padding * 2)
      val newX = (newW * pos.x) / s.wight
      val newY = (newH * pos.y) / s.height
      new Point(newX + padding, newY + padding)
  } getOrElse pos

  def moveIcon(from: Icon, to: Icon) = {
    val valueAnimator: ValueAnimator = ValueAnimator.ofInt(0, 100)
    valueAnimator.addUpdateListener(new AnimatorUpdateListener {
      override def onAnimationUpdate(animation: ValueAnimator): Unit = {
        val fraction = animation.getAnimatedFraction

        val fromOver = from.drop(to.length)
        val toOver = to.drop(from.length)

        val transform = from.zip(to) map (i => transformSegment(i._1, i._2, fraction))

        val segmentFromOver = fromOver map (_.copy(alpha = 1 - fraction))

        val segmentToOver = toOver map { segment =>
          transformSegment(new Segment(
            Point(segment.point1.x + 1, segment.point1.y + 1),
            Point(segment.point1.x, segment.point1.y)), segment, fraction)
        }

        val list = transform ++ segmentFromOver ++ segmentToOver

        setTransformIcon(list)

      }
    })
    valueAnimator.setInterpolator(new DecelerateInterpolator())
    valueAnimator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        stop()
      }
    })
    valueAnimator.start()
  }

  def transformSegment(from: Segment, to: Segment, fraction: Float): Segment =
    if (from.equals(to)) {
      from
    } else {
      val point1 = calculatePoint(from.point1, to.point1, fraction)
      val point2 = calculatePoint(from.point2, to.point2, fraction)

      Segment(point1, point2)
    }

  def calculatePoint(from: Point, to: Point, fraction: Float): Point = {
    val cathetiX = to.x - from.x
    val cathetiY = to.y - from.y

    val hypotenuse = Math.sqrt((cathetiX * cathetiX) + (cathetiY * cathetiY)).toFloat
    val angle = Math.atan(cathetiY / cathetiX)

    val rFraction = hypotenuse * fraction

    val coordX = rFraction * Math.cos(angle).toFloat
    val coordY = rFraction * Math.sin(angle).toFloat

    if (cathetiX >= 0)
      Point(from.x + coordX, from.y + coordY)
    else
      Point(from.x - coordX, from.y - coordY)
  }
}

trait PathMorphDrawableTypes {
  type Icon = List[Segment]
}

object IconTypes {
  val NOICON = 0
  val BURGER = 1
  val BACK = 2
  val CHECK = 3
  val ADD = 4
  val UP = 5
  val DOWN = 6
  val NEXT = 7
  val CLOSE = 8
}

case class Dim(wight: Int, height: Int)

case class Point(x: Float, y: Float)

case class Segment(
  point1: Point = Point(0, 0),
  point2: Point = Point(0, 0),
  alpha: Float = 1) {

  def fromRatios(
    ratioX1: Float,
    ratioY1: Float,
    ratioX2: Float,
    ratioY2: Float)(implicit dim: Option[Dim]): Segment = {
    val (x1: Float, y1: Float, x2: Float, y2: Float) = dim.map {
      value =>
        val x1 = ratioX1 * value.wight
        val y1 = ratioY1 * value.height
        val x2 = ratioX2 * value.wight
        val y2 = ratioY2 * value.height
        (x1, y1, x2, y2)
    }.getOrElse(0f, 0f, 0f, 0f, 0f)
    Segment(Point(x1, y1), Point(x2, y2))
  }
}

object PathMorphDrawableTweaks {
  type W = ImageView

  def pmdAnimIcon(icon: Int) = Tweak[W] {
    view =>
      view.getDrawable.asInstanceOf[PathMorphDrawable].setToTypeIcon(icon)
      view.getDrawable.asInstanceOf[PathMorphDrawable].start()
  }

  def pmdChangeIcon(icon: Int) = Tweak[W](view =>
    Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setTypeIcon(icon)))

  def pmdColor(color: Int) = Tweak[W](view =>
    Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColor(color)))

  def pmdColorResource(color: Int) = Tweak[W](view =>
    Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setColorResource(color)))

  def pmdStroke(stroke: Float) = Tweak[W](view =>
    Try(view.getDrawable.asInstanceOf[PathMorphDrawable].setStroke(stroke)))
}
