package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.graphics.{BitmapFactory, Canvas, Paint, Rect, RectF}
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TryOps._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

import scala.util.Try

class ReorderItemTouchHelperCallback(
  color: Int,
  onChanged: (ActionStateReorder, Action, Int) => Unit)(implicit contextWrapper: ContextWrapper)
  extends Callback {

  var statuses = ReorderStatuses()

  val deleteActionBitmap = BitmapFactory.decodeResource(contextWrapper.bestAvailable.getResources, R.drawable.icon_action_delete)

  val deleteActionRect = new Rect(0, 0, deleteActionBitmap.getWidth, deleteActionBitmap.getHeight)

  val paddingActions = resGetDimensionPixelSize(R.dimen.padding_large)

  val sizeActions = resGetDimensionPixelSize(R.dimen.size_actions_drag_drop)

  val shadowPaint = new Paint()
  shadowPaint.setColor(ColorsUtils.setAlpha(color, .2f))

  val buttonsPaint = new Paint()
  buttonsPaint.setColor(color)

  override def isLongPressDragEnabled: Boolean = true

  override def clearView(recyclerView: RecyclerView, viewHolder: ViewHolder): Unit = {
    Try(recyclerView.invalidateItemDecorations()).logInfo()
    statuses = statuses.copy(action = NoAction)
    super.clearView(recyclerView, viewHolder)
  }

  override def onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean): Unit = {
    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
      val itemView = viewHolder.itemView

      val w = recyclerView.getWidth
      val h = recyclerView.getHeight

      val itemLeft = itemView.getLeft + dX.toInt
      val itemTop = itemView.getTop + dY.toInt
      val itemRight = itemView.getRight + dX.toInt
      val itemBottom = itemView.getBottom + dY.toInt

      val itemRect = new Rect(itemLeft, itemTop, itemRight, itemBottom)

      val actionLeft = w - sizeActions - paddingActions
      val actionTop = h - sizeActions - paddingActions
      val actionRight = actionLeft + sizeActions
      val actionBottom = actionTop + sizeActions

      val actionRect = new Rect(actionLeft, actionTop, actionRight, actionBottom)
      val actionRectF = new RectF(actionLeft, actionTop, actionRight, actionBottom)

      // Draw card if the item is over an action
      if (itemRect.contains(actionRect)) {
        c.drawRect(itemRect, shadowPaint)
        statuses = statuses.copy(action = ActionRemove)
      } else {
        statuses = statuses.copy(action = NoAction)
      }

      // Draw actions
      c.drawOval(actionRectF, buttonsPaint)

      val bitmapLeft = actionRectF.centerX().toInt - (deleteActionBitmap.getWidth / 2)
      val bitmapTop = actionRectF.centerY().toInt - (deleteActionBitmap.getHeight / 2)
      val bitmapRight = bitmapLeft + deleteActionBitmap.getWidth
      val bitmapBottom = bitmapTop + deleteActionBitmap.getHeight

      val bitmapRect = new Rect(bitmapLeft, bitmapTop, bitmapRight, bitmapBottom)
      c.drawBitmap(deleteActionBitmap, deleteActionRect, bitmapRect, javaNull)

    }
    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }

  override def onSelectedChanged(viewHolder: ViewHolder, actionState: Int): Unit = {
    val actionReorder = ActionStateReorder(actionState)
    onChanged(actionReorder, statuses.action, actionReorder match {
      case ActionStateReordering =>
        val position =  viewHolder.getAdapterPosition
        // Update positions
        statuses = statuses.copy(from = position, to = position)
        position
      case ActionStateIdle => statuses.to
    })
    super.onSelectedChanged(viewHolder, actionState)
  }

  override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
    val dragFlags = UP | DOWN | LEFT | RIGHT
    Callback.makeMovementFlags(dragFlags, 0)
  }

  override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = {
    statuses = statuses.copy(from = viewHolder.getAdapterPosition, to = target.getAdapterPosition)
    Option(recyclerView.getAdapter) match {
      case Some(listener: ReorderItemTouchListener) =>
        listener.onItemMove(statuses.from, statuses.to)
      case _ =>
    }
    true
  }

  override def onSwiped(viewHolder: ViewHolder, i: Int): Unit = {}

}

trait ReorderItemTouchListener {
  def onItemMove(from: Int, to: Int): Unit
}

case class ReorderStatuses(
  from: Int = 0,
  to: Int = 0,
  action: Action = NoAction)

sealed trait Action

case object NoAction extends Action

case object ActionRemove extends Action

sealed trait ActionStateReorder

case object ActionStateReordering extends ActionStateReorder

case object ActionStateIdle extends ActionStateReorder

object ActionStateReorder {
  def apply(action: Int): ActionStateReorder = action match {
    case ACTION_STATE_DRAG => ActionStateReordering
    case _ => ActionStateIdle
  }
}