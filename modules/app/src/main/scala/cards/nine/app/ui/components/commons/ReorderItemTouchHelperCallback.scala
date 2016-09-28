package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper._

class ReorderItemTouchHelperCallback(onChanged: (ActionStateReorder, Int) => Unit)
  extends Callback {

  var statuses = ReorderStatuses()

  override def isLongPressDragEnabled: Boolean = true

  override def onSelectedChanged(viewHolder: ViewHolder, actionState: Int): Unit = {
    val action = ActionStateReorder(actionState)
    action match {
      case ActionStateReordering =>
        statuses = statuses.copy(from = viewHolder.getAdapterPosition, to = viewHolder.getAdapterPosition)
        onChanged(action, viewHolder.getAdapterPosition)
      case ActionStateIdle =>
        onChanged(action, statuses.to)
    }
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

case class ReorderStatuses(from: Int = 0, to: Int = 0)

trait ActionStateReorder

case object ActionStateReordering extends ActionStateReorder

case object ActionStateIdle extends ActionStateReorder

object ActionStateReorder {
  def apply(action: Int): ActionStateReorder = action match {
    case ACTION_STATE_DRAG => ActionStateReordering
    case _ => ActionStateIdle
  }
}