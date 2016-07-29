package com.fortysevendeg.ninecardslauncher.services.widgets.models

import android.appwidget.{AppWidgetProviderInfo => AndroidAppWidgetProviderInfo}

trait Conversions {

  def toWidget(androidAppWidgetProviderInfo: AndroidAppWidgetProviderInfo, widgetLabel: String, userHashCode: Option[Int]): Widget = {

      import androidAppWidgetProviderInfo._

      Widget(
        userHashCode = userHashCode,
        autoAdvanceViewId = autoAdvanceViewId,
        initialLayout = initialLayout,
        minHeight = minHeight,
        minResizeHeight = minResizeHeight,
        minResizeWidth = minResizeWidth,
        minWidth = minWidth,
        className = provider.getClassName,
        packageName = provider.getPackageName,
        resizeMode = resizeMode,
        updatePeriodMillis = updatePeriodMillis,
        label = widgetLabel,
        preview = previewImage)
    }
}
