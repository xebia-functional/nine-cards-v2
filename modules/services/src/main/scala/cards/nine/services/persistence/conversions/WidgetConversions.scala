package cards.nine.services.persistence.conversions

import cards.nine.models.{PersistenceWidgetData, PersistenceWidget}
import cards.nine.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import cards.nine.services.persistence._

trait WidgetConversions {

  def toWidget(widget: RepositoryWidget): PersistenceWidget =
    PersistenceWidget(
      id = widget.id,
      momentId = widget.data.momentId,
      packageName = widget.data.packageName,
      className = widget.data.className,
      appWidgetId = if (widget.data.appWidgetId == 0) None else Some(widget.data.appWidgetId),
      startX = widget.data.startX,
      startY = widget.data.startY,
      spanX = widget.data.spanX,
      spanY = widget.data.spanY,
      widgetType = widget.data.widgetType,
      label = widget.data.label,
      imagePath = widget.data.imagePath,
      intent = widget.data.intent)

  def toRepositoryWidget(widget: PersistenceWidget): RepositoryWidget =
    RepositoryWidget(
      id = widget.id,
      data = RepositoryWidgetData(
        momentId = widget.momentId,
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = widget.appWidgetId getOrElse 0,
        startX = widget.startX,
        startY = widget.startY,
        spanX = widget.spanX,
        spanY = widget.spanY,
        widgetType = widget.widgetType,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent))

  def toRepositoryWidgetData(widget: PersistenceWidgetData): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = widget.momentId,
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = widget.appWidgetId getOrElse 0,
      startX = widget.startX,
      startY = widget.startY,
      spanX = widget.spanX,
      spanY = widget.spanY,
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

}
