package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.graphics._
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TryOps._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

import scala.util.Try

class ReorderItemTouchHelperCallback(
  accentColor: Int,
  onChanged: (ActionStateReorder, Action, Int) => Unit)(implicit contextWrapper: ContextWrapper)
  extends Callback {

  val actionsSize = 3

  val editPosition = 0

  val movePosition = 1

  val deletePosition = 2

  var statuses = ReorderStatuses()

  val removeActionBitmap = BitmapFactory.decodeResource(contextWrapper.bestAvailable.getResources, R.drawable.icon_action_delete)

  val moveActionBitmap = BitmapFactory.decodeResource(contextWrapper.bestAvailable.getResources, R.drawable.icon_action_move)

  val editActionBitmap = BitmapFactory.decodeResource(contextWrapper.bestAvailable.getResources, R.drawable.icon_action_edit)

  val padding = resGetDimensionPixelSize(R.dimen.padding_default)

  val removeText = resGetString(R.string.remove)

  val moveToText = resGetString(R.string.moveTo)

  val editText = resGetString(R.string.edit)

  val heightActions = resGetDimensionPixelSize(R.dimen.size_actions_drag_drop)

  val defaultColor = resGetColor(R.color.actions_bar_default)

  val backgroundColor = resGetColor(R.color.actions_bar_background)

  val radius = resGetDimensionPixelSize(R.dimen.radius_default)

  val shadowPaint = new Paint()
  shadowPaint.setColor(accentColor.alpha(.2f))

  val barPaint = new Paint()
  barPaint.setColor(backgroundColor)

  val bitmapPaint = new Paint()

  val textPaint = new Paint()
  textPaint.setAntiAlias(true)
  textPaint.setTextSize(resGetDimension(R.dimen.text_default))

  val strokePaint = new Paint()
  strokePaint.setStrokeWidth(resGetDimension(R.dimen.divider_default))
  strokePaint.setColor(defaultColor.alpha(.4f))

  val tagBackgroundPaint = new Paint()
  tagBackgroundPaint.setColor(defaultColor)

  val tagTextPaint = new Paint()
  tagTextPaint.setColor(backgroundColor)
  tagTextPaint.setAntiAlias(true)
  tagTextPaint.setTextSize(resGetDimension(R.dimen.text_default))

  override def isLongPressDragEnabled: Boolean = false

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

      val actionLeft = 0
      val actionTop = h - heightActions
      val actionRight = w
      val actionBottom = h

      val barActionRect = new Rect(actionLeft, actionTop, actionRight, actionBottom)

      val editActionRect = calculateRect(barActionRect, editPosition)
      val moveActionRect = calculateRect(barActionRect, movePosition)
      val removeActionRect = calculateRect(barActionRect, deletePosition)

      // Draw card if the item is over an action
      val actionSelected = if (itemRect.contains(removeActionRect.centerX(), removeActionRect.bottom)) { // Over delete action
        ActionRemove
      } else if (itemRect.contains(moveActionRect.centerX(), moveActionRect.bottom)) { // Over move action
        ActionMove
      } else if (itemRect.contains(editActionRect.centerX(), editActionRect.bottom)) { // Over edit action
        ActionEdit
      } else {
        NoAction
      }

      drawTag(c, itemRect, actionSelected)
      statuses = statuses.copy(action = actionSelected)

      // Draw bar
      c.drawRect(barActionRect, barPaint)
      c.drawLine(actionLeft, actionTop, actionRight, actionTop, strokePaint)

      // Draw actions
      drawAction(c, editActionRect, ActionEdit, ActionEdit == actionSelected)
      drawAction(c, moveActionRect, ActionMove, ActionMove == actionSelected)
      drawAction(c, removeActionRect, ActionRemove, ActionRemove == actionSelected)

    }
    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }

  private[this] def drawTag(c: Canvas, itemRect: Rect, action: Action) = {
    val maybeText = action match {
      case ActionRemove => Some(removeText)
      case ActionEdit => Some(editText)
      case ActionMove => Some(moveToText)
      case NoAction => None
    }

    maybeText foreach { text =>
      c.drawRect(itemRect, shadowPaint)

      val textBounds = new Rect
      tagTextPaint.getTextBounds(text, 0, text.length, textBounds)
      val ascent = textPaint.getFontMetrics.ascent

      val top = itemRect.top - (padding * 3) - textBounds.height()
      val bottom = top + textBounds.height() + (padding * 2)
      val left = itemRect.centerX() - textBounds.centerX() - padding
      val right = left + textBounds.width() + (padding * 2)

      val rect = new RectF(left, top, right, bottom)

      c.drawRoundRect(rect, radius, radius, tagBackgroundPaint)

      c.drawText(text, rect.centerX() - textBounds.centerX(), rect.centerY() - ascent - padding, tagTextPaint)
    }

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

  private[this] def calculateRect(actionRectF: Rect, position: Int): Rect = {
    val width = actionRectF.width() / actionsSize
    val left = width * position
    val right = left + width
    new Rect(left, actionRectF.top, right, actionRectF.bottom)
  }

  private[this] def drawAction(c: Canvas, barActionRect: Rect, action: Action, selected: Boolean) = {

    val data = action match {
      case ActionRemove => Some((removeActionBitmap, removeText))
      case ActionEdit => Some((editActionBitmap, editText))
      case ActionMove => Some((moveActionBitmap, moveToText))
      case _ => None
    }

    data foreach {
      case (bitmap, text) =>
        val actionRect = new Rect(0, 0, bitmap.getWidth, bitmap.getHeight)

        val textBounds = new Rect
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val ascent = textPaint.getFontMetrics.ascent

        val h = bitmap.getHeight + textBounds.height()
        val padding = (barActionRect.height() - h) / 2

        val bitmapLeft = barActionRect.centerX() - (actionRect.width() / 2)
        val bitmapTop = barActionRect.top + padding
        val bitmapRight = bitmapLeft + actionRect.width()
        val bitmapBottom = bitmapTop + actionRect.height()

        val bitmapRect = new Rect(bitmapLeft, bitmapTop, bitmapRight, bitmapBottom)

        val filter = if (selected) {
          new LightingColorFilter(accentColor, 1)
        } else {
          new LightingColorFilter(defaultColor, 1)
        }

        bitmapPaint.setColorFilter(filter)

        c.drawBitmap(bitmap, actionRect, bitmapRect, bitmapPaint)

        val xText = barActionRect.centerX() - textBounds.exactCenterX()

        val yText = bitmapBottom - ascent

        if (selected) textPaint.setColor(accentColor) else textPaint.setColor(defaultColor)
        c.drawText(text, xText, yText, textPaint)

      case _ =>
    }

  }

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

case object ActionEdit extends Action

case object ActionMove extends Action

sealed trait ActionStateReorder

case object ActionStateReordering extends ActionStateReorder

case object ActionStateIdle extends ActionStateReorder

object ActionStateReorder {
  def apply(action: Int): ActionStateReorder = action match {
    case ACTION_STATE_DRAG => ActionStateReordering
    case _ => ActionStateIdle
  }
}