package cards.nine.commons.test.data

import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.WidgetValues._
import cards.nine.models.types.WidgetType
import cards.nine.models.{NineCardsIntentConversions, Widget, WidgetArea, WidgetData}

trait WidgetTestData extends NineCardsIntentConversions {

  def widget(num: Int = 0) =
    Widget(
      id = widgetId + num,
      momentId = widgetMomentId,
      packageName = widgetPackageName,
      className = widgetClassName,
      appWidgetId = Option(appWidgetId),
      area = WidgetArea(startX = startX, startY = startY, spanX = spanX, spanY = spanY),
      widgetType = WidgetType(widgetType),
      label = Option(label),
      imagePath = Option(widgetImagePath),
      intent = Option(jsonToNineCardIntent(intent)))

  val widget: Widget = widget(0)
  val seqWidget      = Seq(widget(0), widget(1), widget(2))

  val widgetData: WidgetData = widget.toData
  val seqWidgetData          = seqWidget map (_.toData)

}
