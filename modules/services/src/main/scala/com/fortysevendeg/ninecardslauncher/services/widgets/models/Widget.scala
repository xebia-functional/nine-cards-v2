package com.fortysevendeg.ninecardslauncher.services.widgets.models

import android.graphics.drawable.Drawable

case class Widget (
  userHashCode: Option[Int],
  autoAdvanceViewId: Int,
  initialLayout: Int,
  minHeight: Int,
  minResizeHeight: Int,
  minResizeWidth: Int,
  minWidth: Int,
  className: String,
  packageName: String,
  resizeMode: Int,
  updatePeriodMillis: Int,
  label: String,
  icon: Drawable,
  preview: Drawable)



