package cards.nine.process.widget

import cards.nine.models.types.WidgetType
import cards.nine.models.{PersistenceWidget, Widget, WidgetArea}
import cards.nine.services.persistence.{AddWidgetRequest => ServicesAddWidgetRequest, UpdateWidgetRequest => ServicesUpdateWidgetRequest}

trait WidgetConversions {

  def toWidgetSeq(servicesWidgetSeq: Seq[PersistenceWidget]) = servicesWidgetSeq map toWidget

  def toAddWidgetRequest(addWidgetRequest: AddWidgetRequest): ServicesAddWidgetRequest = ServicesAddWidgetRequest(
    momentId = addWidgetRequest.momentId,
    packageName = addWidgetRequest.packageName,
    className = addWidgetRequest.className,
    appWidgetId = addWidgetRequest.appWidgetId,
    startX = addWidgetRequest.startX,
    startY = addWidgetRequest.startY,
    spanX = addWidgetRequest.spanX,
    spanY = addWidgetRequest.spanY,
    widgetType = addWidgetRequest.widgetType.name,
    label = addWidgetRequest.label,
    imagePath = addWidgetRequest.imagePath,
    intent = addWidgetRequest.intent)

  def toWidget(servicesWidget: PersistenceWidget): Widget = Widget(
    id = servicesWidget.id,
    momentId = servicesWidget.momentId,
    packageName = servicesWidget.packageName,
    className = servicesWidget.className,
    appWidgetId = servicesWidget.appWidgetId,
    area = WidgetArea(
      startX = servicesWidget.startX,
      startY = servicesWidget.startY,
      spanX = servicesWidget.spanX,
      spanY = servicesWidget.spanY),
    widgetType = WidgetType(servicesWidget.widgetType),
    label = servicesWidget.label,
    imagePath = servicesWidget.imagePath,
    intent = servicesWidget.intent)

  def toUpdatedWidget(widget: Widget, moveWidgetRequest: MoveWidgetRequest): Widget =
    widget.copy(
      area = widget.area.copy(
        startX = widget.area.startX + moveWidgetRequest.displaceX,
        startY = widget.area.startY + moveWidgetRequest.displaceY))

  def toUpdatedWidget(widget: Widget, resizeWidgetRequest: ResizeWidgetRequest): Widget =
    widget.copy(
      area = widget.area.copy(
        spanX = widget.area.spanX + resizeWidgetRequest.increaseX,
        spanY = widget.area.spanY + resizeWidgetRequest.increaseY))

  def toUpdatedWidget(widget: Widget, appWidgetId: Int): Widget =
    widget.copy(appWidgetId = Option(appWidgetId))

  def toServicesUpdateWidgetRequest(widget: Widget): ServicesUpdateWidgetRequest = ServicesUpdateWidgetRequest(
    id = widget.id,
    momentId = widget.momentId,
    packageName = widget.packageName,
    className = widget.className,
    appWidgetId = widget.appWidgetId,
    startX = widget.area.startX,
    startY = widget.area.startY,
    spanX = widget.area.spanX,
    spanY = widget.area.spanY,
    widgetType = widget.widgetType.name,
    label = widget.label,
    imagePath = widget.imagePath,
    intent = widget.intent)
}
