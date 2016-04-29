package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.{DragEvent, View}
import android.view.View.OnDragListener
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

object CommonsTweak {

  def vBackgroundBoxWorkspace(color: Int, horizontalPadding: Int = 0)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    val radius = resGetDimensionPixelSize(R.dimen.radius_default)
    Lollipop.ifSupportedThen {
      vBackgroundColor(color) +
        vClipBackground(radius, horizontalPadding = horizontalPadding) +
        vElevation(resGetDimensionPixelSize(R.dimen.elevation_box_workspaces))
    } getOrElse {
      val s = 0 until 8 map (_ => radius.toFloat)
      val drawable = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
      drawable.getPaint.setColor(color)
      // TODO We should include horizontal padding
      vBackground(drawable)
    }
  }

  def vSetPosition(position: Int): Tweak[View] = vTag(R.id.position, position)

  def vSetType(t: String) = vTag(R.id.view_type, t)

  def vAddField[T](key: String, value: T) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap + ((key, value)))
  }

  def vRemoveField(key: String) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap - key)
  }

  def vUseLayerHardware = vTag(R.id.use_layer_hardware, "")

  def vLayerHardware(activate: Boolean) = Transformer {
    case v: View if v.hasLayerHardware => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }

}
