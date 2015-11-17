package com.fortysevendeg.ninecardslauncher.process.device.types

import android.appwidget.AppWidgetProviderInfo

sealed trait WidgetResizeMode

case object WidgetResizeNone extends WidgetResizeMode

case object WidgetResizeVertical extends WidgetResizeMode

case object WidgetResizeHorizontal extends WidgetResizeMode

case object WidgetResizeBoth extends WidgetResizeMode

object WidgetResizeMode {

  def apply(mode: Int): WidgetResizeMode = mode match {
    case AppWidgetProviderInfo.RESIZE_VERTICAL => WidgetResizeVertical
    case AppWidgetProviderInfo.RESIZE_HORIZONTAL => WidgetResizeHorizontal
    case AppWidgetProviderInfo.RESIZE_BOTH => WidgetResizeBoth
    case _ => WidgetResizeNone
  }

}
