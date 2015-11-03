package com.fortysevendeg.ninecardslauncher.services.widgets.models

import android.appwidget.{AppWidgetProviderInfo => AndroidAppWidgetProviderInfo}
import android.graphics.drawable.Drawable

trait Conversions {

  def toWidget(androidAppWidgetProviderInfo: AndroidAppWidgetProviderInfo, label: String, iconImage: Drawable,
    previewImageView: Drawable, userProfile: Option[Int]): Widget =
    Widget(
      userHashCode = Option(userProfile.hashCode),
      autoAdvanceViewId = androidAppWidgetProviderInfo.autoAdvanceViewId,
      initialLayout = androidAppWidgetProviderInfo.initialLayout,
      minHeight = androidAppWidgetProviderInfo.minHeight,
      minResizeHeight = androidAppWidgetProviderInfo.minResizeHeight,
      minResizeWidth = androidAppWidgetProviderInfo.minResizeWidth,
      minWidth = androidAppWidgetProviderInfo.minWidth,
      className = androidAppWidgetProviderInfo.provider.getClassName,
      packageName = androidAppWidgetProviderInfo.provider.getPackageName,
      resizeMode = androidAppWidgetProviderInfo.autoAdvanceViewId,
      updatePeriodMillis = androidAppWidgetProviderInfo.autoAdvanceViewId,
      label = label,
      icon = iconImage,
      preview = previewImageView)
}
