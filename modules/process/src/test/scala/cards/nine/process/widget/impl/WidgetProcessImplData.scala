package cards.nine.process.widget.impl

import cards.nine.models.types.WidgetType
import cards.nine.models.types.WidgetType._
import cards.nine.models.{Widget, WidgetArea}
import cards.nine.process.widget.{AddWidgetRequest, MoveWidgetRequest, ResizeWidgetRequest}
import cards.nine.services.persistence.models.{Widget => ServicesWidget}

import scala.util.Random

trait WidgetProcessImplData {

  val items: Int = Random.nextInt(10)
  val widgetId: Int = Random.nextInt(10)
  val nonExistentWidgetId: Int = Random.nextInt(10) + 100
  val momentId: Int = Random.nextInt(10)
  val nonExistentMomentId: Int = Random.nextInt(10) + 100
  val packageName: String = Random.nextString(5)
  val className: String = Random.nextString(5)
  val appWidgetId: Int = Random.nextInt(10)
  val nonExistentAppWidgetId: Int = Random.nextInt(10) + 100
  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)
  val widgetType: WidgetType = widgetTypes(Random.nextInt(widgetTypes.length))
  val label: String = Random.nextString(5)
  val widgetImagePath: String = Random.nextString(5)
  val widgetIntent: String = Random.nextString(5)
  val labelOption = Option(label)
  val widgetImagePathOption = Option(widgetImagePath)
  val widgetIntentOption = Option(widgetIntent)

  val seqWidget = createSeqWidget()
  val widgetOption = seqWidget.headOption
  val widget = seqWidget(0)
  val seqServicesWidget = createSeqServicesWidget()
  val servicesWidgetOption = seqServicesWidget.headOption
  val servicesWidget = seqServicesWidget(0)
  val seqAddWidgetRequest = createSeqAddWidgetRequest(0)
  val addWidgetRequest = seqAddWidgetRequest(0)

  def createSeqWidget(
   id: Int = widgetId,
   momentId: Int = momentId,
   packageName: String = packageName,
   className: String = className,
   appWidgetId: Int = appWidgetId,
   startX: Int = startX,
   startY: Int = startY,
   spanX: Int = spanX,
   spanY: Int = spanY,
   widgetType: WidgetType = widgetType,
   label: Option[String] = labelOption,
   imagePath: Option[String] = widgetImagePathOption,
   intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (
      item =>
        Widget(
          id = id,
          momentId = momentId,
          packageName = packageName,
          className = className,
          appWidgetId = Option(appWidgetId),
          WidgetArea(
            startX = startX,
            startY = startY,
            spanX = spanX,
            spanY = spanY),
          widgetType = widgetType,
          label = label,
          imagePath = imagePath,
          intent = intent))

  def createSeqServicesWidget(
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
    widgetType: WidgetType = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (item =>
      ServicesWidget(
        id = id,
        momentId = momentId,
        packageName = packageName,
        className = className,
        appWidgetId = Option(appWidgetId),
        startX = startX,
        startY = startY,
        spanX = spanX,
        spanY = spanY,
        widgetType = widgetType.name,
        label = label,
        imagePath = imagePath,
        intent = intent))

  def createSeqAddWidgetRequest(
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
    widgetType: WidgetType = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) =
    (0 until 5) map (item =>
      AddWidgetRequest(
        momentId = momentId,
        packageName = packageName,
        className = className,
        appWidgetId = appWidgetId,
        startX = startX,
        startY = startY,
        spanX = spanX,
        spanY = spanY,
        widgetType = widgetType,
        label = label,
        imagePath = imagePath,
        intent = intent))

  val moveStartX: Int = Random.nextInt(8)
  val moveStartY: Int = Random.nextInt(8)

  val moveWidgetRequest = MoveWidgetRequest(
    displaceX = moveStartX,
    displaceY = moveStartY)

  val moveWidgetResponse = widget.copy(
    area = widget.area.copy(
      startX = widget.area.startX + moveWidgetRequest.displaceX,
      startY = widget.area.startY + moveWidgetRequest.displaceY))


  val resizeSpanX: Int = Random.nextInt(8)
  val resizeSpanY: Int = Random.nextInt(8)

  val resizeWidgetRequest = ResizeWidgetRequest(
    increaseX = resizeSpanX,
    increaseY = resizeSpanY)

  val resizeWidgetResponse = widget.copy(
    area = widget.area.copy(
      spanX = widget.area.spanX + resizeWidgetRequest.increaseX,
      spanY = widget.area.spanY + resizeWidgetRequest.increaseY))
}
