package cards.nine.services.persistence.conversions

import cards.nine.models.types.WidgetType
import cards.nine.models.{Widget, WidgetArea, WidgetData}
import cards.nine.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}

trait WidgetConversions {

  def toWidget(widget: RepositoryWidget): Widget =
    Widget(
      id = widget.id,
      momentId = widget.data.momentId,
      packageName = widget.data.packageName,
      className = widget.data.className,
      appWidgetId = if (widget.data.appWidgetId == 0) None else Some(widget.data.appWidgetId),
      area = WidgetArea(
        startX = widget.data.startX,
        startY = widget.data.startY,
        spanX = widget.data.spanX,
        spanY = widget.data.spanY),
      widgetType = WidgetType(widget.data.widgetType),
      label = widget.data.label,
      imagePath = widget.data.imagePath,
      intent = widget.data.intent)

  def toRepositoryWidget(widget: Widget): RepositoryWidget =
    RepositoryWidget(
      id = widget.id,
      data = RepositoryWidgetData(
        momentId = widget.momentId,
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = widget.appWidgetId getOrElse 0,
        startX = widget.area.startX,
        startY = widget.area.startY,
        spanX = widget.area.spanX,
        spanY = widget.area.spanY,
        widgetType = widget.widgetType.name,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent))

  def toRepositoryWidgetData(widget: WidgetData): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = widget.momentId,
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = widget.appWidgetId getOrElse 0,
      startX = widget.area.startX,
      startY = widget.area.startY,
      spanX = widget.area.spanX,
      spanY = widget.area.spanY,
      widgetType = widget.widgetType.name,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

}
