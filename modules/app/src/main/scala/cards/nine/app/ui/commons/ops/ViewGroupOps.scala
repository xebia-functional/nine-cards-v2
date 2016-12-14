package cards.nine.app.ui.commons.ops

import android.view.{View, ViewGroup}

object ViewGroupOps {

  implicit class ViewGroupExtras(view: ViewGroup) {

    def children: Seq[View] = (0 until view.getChildCount) map (position => view.getChildAt(position))

  }

}
