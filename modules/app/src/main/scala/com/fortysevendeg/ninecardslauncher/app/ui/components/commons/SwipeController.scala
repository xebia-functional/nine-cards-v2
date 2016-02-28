package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent._
import android.view.{MotionEvent, VelocityTracker, ViewConfiguration}

trait SwipeController {

  def getContext: Context

  val computeUnitsTracker = 1000

  val (maximumVelocity, minimumVelocity) = {
    val configuration: ViewConfiguration = ViewConfiguration.get(getContext)
    (configuration.getScaledMaximumFlingVelocity,
      configuration.getScaledMinimumFlingVelocity)
  }

  var velocityTracker: Option[VelocityTracker] = None

  def updateSwipe(event: MotionEvent): Unit = {
    val action = MotionEventCompat.getActionMasked(event)
    action match {
      case ACTION_DOWN =>
        if (velocityTracker.isEmpty) {
          velocityTracker = Some(VelocityTracker.obtain())
        } else {
          velocityTracker foreach (_.clear())
        }
        velocityTracker foreach (_.addMovement(event))
      case ACTION_MOVE =>
        velocityTracker foreach (_.addMovement(event))
      case _ =>
    }
  }

  def currentSwiping: Swiping = velocityTracker map { tracker =>
    tracker.computeCurrentVelocity(computeUnitsTracker, maximumVelocity)
    tracker.getXVelocity match {
      case v if v > minimumVelocity => SwipeLeft(v)
      case v if v < -minimumVelocity => SwipeRight(v)
      case _ => NoSwipe()
    }
  } getOrElse NoSwipe()

  def recycleSwipe(): Unit = velocityTracker foreach(_.recycle())

  def resetSwipe(): Unit = velocityTracker = None

}


sealed trait Swiping {
  def getVelocity: Float = 0
}

case class SwipeLeft(velocity: Float) extends Swiping {
  override def getVelocity: Float = velocity
}

case class SwipeRight(velocity: Float) extends Swiping {
  override def getVelocity: Float = velocity
}

case class NoSwipe() extends Swiping