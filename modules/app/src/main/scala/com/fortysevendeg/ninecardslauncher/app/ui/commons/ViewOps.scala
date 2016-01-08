package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import com.fortysevendeg.ninecardslauncher2.R

object ViewOps {

  implicit class ViewExtras(view: View) {

    def isPosition(item: Int): Boolean =
      Option(view.getTag(R.id.position)).isDefined && Int.unbox(view.getTag(R.id.position)).equals(item)

    def getPosition: Option[Int] = Option(view.getTag(R.id.position)) map (pos => Int.unbox(pos))

    def hasLayerHardware: Boolean = Option(view.getTag(R.id.use_layer_hardware)).isDefined

  }

}
