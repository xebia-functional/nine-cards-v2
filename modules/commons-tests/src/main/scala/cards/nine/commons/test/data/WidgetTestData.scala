package cards.nine.commons.test.data

import cards.nine.commons.test.data.WidgetValues._
import cards.nine.models.types.WidgetType
import cards.nine.models.{NineCardsIntentConversions, Widget, WidgetArea, WidgetData}

trait WidgetTestData extends NineCardsIntentConversions {

  def widget(num: Int = 0) = Widget(
    id = widgetId + num,
    momentId = widgetMomentId,
    packageName = widgetPackageName,
    className = widgetClassName,
    appWidgetId = Option(appWidgetId),
    area =
      WidgetArea(
        startX = startX,
        startY = startY,
        spanX = spanX,
        spanY = spanY),
    widgetType = WidgetType(widgetType),
    label = Option(label),
    imagePath = Option(widgetImagePath),
    intent = Option(jsonToNineCardIntent(widgetIntent)))

  val widget: Widget = widget(0)
  val seqWidget = Seq(widget(0), widget(1), widget(2))

  def widgetData(num: Int = 0) = WidgetData(
    momentId = widgetMomentId,
    packageName = widgetPackageName,
    className = widgetClassName,
    appWidgetId = Option(appWidgetId),
    area =
      WidgetArea(
        startX = startX,
        startY = startY,
        spanX = spanX,
        spanY = spanY),
    widgetType = WidgetType(widgetType),
    label = Option(label),
    imagePath = Option(widgetImagePath),
    intent = Option(jsonToNineCardIntent(widgetIntent)))

  val widgetData: WidgetData = widgetData(0)
  val seqWidgetData = Seq(widgetData(0), widgetData(1), widgetData(2))

}
