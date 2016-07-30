package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget

trait WidgetConversions {

  def toWidgetSeq(widget: Seq[RepositoryWidget]): Seq[Widget] = widget map toWidget

  def toWidget(widget: RepositoryWidget): Widget =
    Widget(
      id = widget.id,
      momentId = widget.data.momentId,
      packageName = widget.data.packageName,
      className = widget.data.className,
      appWidgetId = widget.data.appWidgetId,
      spanX = widget.data.spanX,
      spanY = widget.data.spanY,
      startX = widget.data.startX,
      startY = widget.data.startY)

  def toRepositoryWidget(widget: Widget): RepositoryWidget =
    RepositoryWidget(
      id = widget.id,
      data = RepositoryWidgetData(
        momentId = widget.momentId,
        packageName = widget.packageName,
        className = widget.className,
        appWidgetId = widget.appWidgetId,
        spanX = widget.spanX,
        spanY = widget.spanY,
        startX = widget.startX,
        startY = widget.startY))

  def toRepositoryWidget(request: UpdateWidgetRequest): RepositoryWidget =
    RepositoryWidget(
      id = request.id,
      data = RepositoryWidgetData(
        momentId = request.momentId,
        packageName = request.packageName,
        className = request.className,
        appWidgetId = request.appWidgetId,
        spanX = request.spanX,
        spanY = request.spanY,
        startX = request.startX,
        startY = request.startY))

  def toRepositoryWidgetData(request: AddWidgetRequest): RepositoryWidgetData =
    RepositoryWidgetData(
      momentId = request.momentId,
      packageName = request.packageName,
      className = request.className,
      appWidgetId = request.appWidgetId,
      spanX = request.spanX,
      spanY = request.spanY,
      startX = request.startX,
      startY = request.startY)
}
