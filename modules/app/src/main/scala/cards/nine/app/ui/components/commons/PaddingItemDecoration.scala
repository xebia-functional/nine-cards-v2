package cards.nine.app.ui.components.commons

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View
import cards.nine.app.ui.preferences.commons.CardPadding
import macroid.ContextWrapper

class PaddingItemDecoration (implicit contextWrapper: ContextWrapper)
  extends RecyclerView.ItemDecoration {

  val padding = CardPadding.getPadding

  override def getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State): Unit = {
    outRect.top = padding
    outRect.bottom = padding
    outRect.left = padding
    outRect.right = padding
  }

}