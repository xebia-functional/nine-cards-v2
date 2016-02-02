package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import com.fortysevendeg.ninecardslauncher2.R

object ViewOps {

  val positionId = R.id.position

  val viewTypeId = R.id.view_type

  val useLayerHardwareId = R.id.use_layer_hardware

  implicit class ViewExtras(view: View) {

    def isPosition(item: Int): Boolean =
      Option(view.getTag(positionId)).isDefined && Int.unbox(view.getTag(positionId)).equals(item)

    def getType: Option[String] = Option(view.getTag(viewTypeId)) map (_.toString)

    def isType(t: String): Boolean =
      Option(view.getTag(viewTypeId)).isDefined && view.getTag(viewTypeId).equals(t)

    def getPosition: Option[Int] = Option(view.getTag(positionId)) map (pos => Int.unbox(pos))

    def hasLayerHardware: Boolean = Option(view.getTag(useLayerHardwareId)).isDefined

  }

}
