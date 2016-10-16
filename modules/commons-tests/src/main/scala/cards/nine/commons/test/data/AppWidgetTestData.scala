package cards.nine.commons.test.data

import cards.nine.commons.test.data.AppWidgetValues._
import cards.nine.models.AppWidget
import cards.nine.models.types.WidgetResizeMode

trait AppWidgetTestData {

  def appWidget(num: Int = 0) = AppWidget(
    userHashCode = Option(userHashCode),
    autoAdvanceViewId = autoAdvanceViewId,
    initialLayout = initialLayout,
    minHeight = minHeight,
    minResizeHeight = minResizeHeight,
    minResizeWidth = minResizeWidth,
    minWidth = minWidth,
    className = appWidgetClassName,
    packageName = appWidgetPackageName,
    resizeMode = WidgetResizeMode(resizeMode),
    updatePeriodMillis = updatePeriodMillis,
    label = label,
    preview = preview)

  val appWidget: AppWidget = appWidget(0)
  val seqAppWidget: Seq[AppWidget] = Seq(appWidget(0), appWidget(1), appWidget(2))

}
