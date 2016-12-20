package cards.nine.models

import android.appwidget.{AppWidgetProviderInfo => AndroidAppWidgetProviderInfo}
import cards.nine.models.types.WidgetResizeMode

trait Conversions {

  def toWidget(
      androidAppWidgetProviderInfo: AndroidAppWidgetProviderInfo,
      widgetLabel: String,
      userHashCode: Option[Int]): AppWidget = {

    import androidAppWidgetProviderInfo._

    AppWidget(
      userHashCode = userHashCode,
      autoAdvanceViewId = autoAdvanceViewId,
      initialLayout = initialLayout,
      minHeight = minHeight,
      minResizeHeight = minResizeHeight,
      minResizeWidth = minResizeWidth,
      minWidth = minWidth,
      className = provider.getClassName,
      packageName = provider.getPackageName,
      resizeMode = WidgetResizeMode(resizeMode),
      updatePeriodMillis = updatePeriodMillis,
      label = widgetLabel,
      preview = previewImage)
  }
}
