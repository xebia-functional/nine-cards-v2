package cards.nine.commons.test.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.WidgetValues._
import cards.nine.models.types.WidgetType
import cards.nine.models.{NineCardsIntentConversions, Widget, WidgetArea, WidgetData}

trait WidgetTestData extends NineCardsIntentConversions {

  def createSeqWidget(
    num: Int = 5,
    id: Int = widgetId,
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (
      item =>
        Widget(
          id = id + item,
          momentId = momentId,
          packageName = packageName,
          className = className,
          appWidgetId = Option(appWidgetId),
          area =
            WidgetArea(
              startX = startX,
              startY = startY,
              spanX = spanX,
              spanY = spanY),
          widgetType = WidgetType(widgetType),
          label = label,
          imagePath = imagePath,
          intent = intent map jsonToNineCardIntent))

  def createSeqWidgetData(
    num: Int = 5,
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (
      item =>
        WidgetData(
          momentId = momentId,
          packageName = packageName,
          className = className,
          appWidgetId = Option(appWidgetId),
          area =
            WidgetArea(
              startX = startX,
              startY = startY,
              spanX = spanX,
              spanY = spanY),
          widgetType = WidgetType(widgetType),
          label = label,
          imagePath = imagePath,
          intent = intent map jsonToNineCardIntent))

  val seqWidget: Seq[Widget] = createSeqWidget()
  val seqWidgetData: Seq[WidgetData] = createSeqWidgetData()

  val widget = seqWidget(0)
  val widgetData = seqWidgetData(0)
}
