package cards.nine.commons.test.data

import cards.nine.commons.test.data.AppWidgetValues._
import cards.nine.commons.test.data.ApplicationValues._
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
    className = appWidgetClassName + num,
    packageName = appWidgetPackageName + num,
    resizeMode = WidgetResizeMode(resizeMode),
    updatePeriodMillis = updatePeriodMillis,
    label = label,
    preview = preview)

  val appWidget: AppWidget = appWidget(0)
  val seqAppWidget: Seq[AppWidget] = Seq(appWidget(0), appWidget(1), appWidget(2))

  case class AppsWithWidgets(
    packageName: String,
    name: String,
    widgets: Seq[AppWidget])

  def appsWithWidgets(num: Int = 0) = AppsWithWidgets(
    packageName = applicationPackageName + num,
    name = applicationName + num,
    widgets = seqAppWidget)

  val seqAppsWithWidgets: Seq[AppsWithWidgets] = Seq(appsWithWidgets(0), appsWithWidgets(1), appsWithWidgets(2))

}
