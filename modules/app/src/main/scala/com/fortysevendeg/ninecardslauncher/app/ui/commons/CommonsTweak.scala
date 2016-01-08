package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.View
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import ExtraTweaks._
import ViewOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Transformer, Tweak}

object CommonsTweak {

  def vBackgroundBoxWorkspace(color: Int)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    val radius = resGetDimensionPixelSize(R.dimen.radius_default)
    Lollipop.ifSupportedThen {
      vBackgroundColor(color) +
        ExtraTweaks.vClipBackground(radius) +
        vElevation(resGetDimensionPixelSize(R.dimen.elevation_box_workspaces))
    } getOrElse {
      val s = 0 until 8 map (_ => radius.toFloat)
      val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
      d.getPaint.setColor(color)
      vBackground(d)
    }
  }

  def vSetPosition(position: Int) = vTag2(R.id.position, position)

  def vUseLayerHardware = vTag(R.id.use_layer_hardware, "")

  def vLayerHardware(activate: Boolean) = Transformer {
    case v: View if v.hasLayerHardware => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }

}
