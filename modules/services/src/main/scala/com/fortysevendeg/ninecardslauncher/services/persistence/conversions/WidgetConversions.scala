package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget

trait WidgetConversions {

  def toWidgetSeq(widget: Seq[RepositoryWidget]): Seq[Widget] = widget map toWidget

  def toAddWidgetRequestSeq(momentId: Int, widgetRequest: Seq[SaveWidgetRequest]) =
    widgetRequest map (widget => toAddWidgetRequest(momentId, widget))

  def toAddWidgetRequest(momentId: Int, widget: SaveWidgetRequest) =
    AddWidgetRequest(
      momentId = momentId,
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = widget.appWidgetId,
      startX = widget.startX,
      startY = widget.startY,
      spanX = widget.spanX,
      spanY = widget.spanY,
      widgetType = widget.widgetType,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

  def toWidget(widget: RepositoryWidget): Widget =
    Widget(
      id = widget.id,
      momentId = widget.data.momentId,
      packageName = widget.data.packageName,
      className = widget.data.className,
      appWidgetId = widget.data.appWidgetId,
      startX = widget.data.startX,
      startY = widget.data.startY,
      spanX = widget.data.spanX,
      spanY = widget.data.spanY,
      widgetType = widget.data.widgetType,
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
        appWidgetId = widget.appWidgetId,
        startX = widget.startX,
        startY = widget.startY,
        spanX = widget.spanX,
        spanY = widget.spanY,
        widgetType = widget.widgetType,
        label = widget.label,
        imagePath = widget.imagePath,
        intent = widget.intent))

  def toRepositoryWidget(request: UpdateWidgetRequest): RepositoryWidget =
    RepositoryWidget(
      id = request.id,
      data = RepositoryWidgetData(
        momentId = request.momentId,
        packageName = request.packageName,
        className = request.className,
        appWidgetId = request.appWidgetId,
        startX = request.startX,
        startY = request.startY,
        spanX = request.spanX,
        spanY = request.spanY,
        widgetType = request.widgetType,
        label = request.label,
        imagePath = request.imagePath,
        intent = request.intent))

  def toRepositoryWidgetData(request: AddWidgetRequest): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = request.momentId,
      packageName = request.packageName,
      className = request.className,
      appWidgetId = request.appWidgetId,
      startX = request.startX,
      startY = request.startY,
      spanX = request.spanX,
      spanY = request.spanY,
      widgetType = request.widgetType,
      label = request.label,
      imagePath = request.imagePath,
      intent = request.intent)

}
