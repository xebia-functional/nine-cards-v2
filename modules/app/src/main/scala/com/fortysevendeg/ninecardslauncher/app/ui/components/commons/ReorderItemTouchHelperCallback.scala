package com.fortysevendeg.ninecardslauncher.app.ui.components.commons

import java.util
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper._
import android.util.Log

class ReorderItemTouchHelperCallback(onChanged: (ActionStateReorder) => Unit)
  extends Callback {

  override def isLongPressDragEnabled: Boolean = true

  override def chooseDropTarget(selected: ViewHolder, dropTargets: util.List[ViewHolder], curX: Int, curY: Int): ViewHolder = {
    Log.d("9cards", s"chooseDropTarget -- curX: $curX -- curY: $curY")
    super.chooseDropTarget(selected, dropTargets, curX, curY)
  }

  override def onSelectedChanged(viewHolder: ViewHolder, actionState: Int): Unit = {
    onChanged(ActionStateReorder(actionState))
    super.onSelectedChanged(viewHolder, actionState)
  }

  override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
    val dragFlags = UP | DOWN | LEFT | RIGHT
    Callback.makeMovementFlags(dragFlags, 0)
  }

  override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = {
    Option(recyclerView.getAdapter) match {
      case Some(listener: ReorderItemTouchListener) =>
        listener.onItemMove(viewHolder.getAdapterPosition, target.getAdapterPosition)
    }
    true
  }

  override def onSwiped(viewHolder: ViewHolder, i: Int): Unit = {}

}

trait ReorderItemTouchListener {
  def onItemMove(from: Int, to: Int): Unit
}

trait ActionStateReorder

case object ActionStateReordering extends ActionStateReorder

case object ActionStateIdle extends ActionStateReorder

object ActionStateReorder {
  def apply(action: Int): ActionStateReorder = action match {
    case ACTION_STATE_DRAG => ActionStateReordering
    case _ => ActionStateIdle
  }
}