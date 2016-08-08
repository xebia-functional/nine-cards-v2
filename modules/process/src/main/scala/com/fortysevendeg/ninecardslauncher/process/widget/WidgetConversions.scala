package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.process.commons.types.WidgetType
import com.fortysevendeg.ninecardslauncher.process.widget.models.Widget
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddWidgetRequest => ServicesAddWidgetRequest, UpdateWidgetRequest => ServicesUpdateWidgetRequest}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Widget => ServicesWidget}

trait WidgetConversions {

  def toWidgetSeq(servicesWidgetSeq: Seq[ServicesWidget]) = servicesWidgetSeq map toWidget

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

  def toWidget(servicesWidget: ServicesWidget): Widget = Widget(
    id = servicesWidget.id,
    momentId = servicesWidget.momentId,
    packageName = servicesWidget.packageName,
    className = servicesWidget.className,
    appWidgetId = servicesWidget.appWidgetId,
    startX = servicesWidget.startX,
    startY = servicesWidget.startY,
    spanX = servicesWidget.spanX,
    spanY = servicesWidget.spanY,
    widgetType = WidgetType(servicesWidget.widgetType),
    label = servicesWidget.label,
    imagePath = servicesWidget.imagePath,
    intent = servicesWidget.intent)

  def toUpdatedWidget(widget: Widget, moveWidgetRequest: MoveWidgetRequest): Widget =  Widget(
    id = widget.id,
    momentId = widget.momentId,
    packageName = widget.packageName,
    className = widget.className,
    appWidgetId = widget.appWidgetId,
    startX = moveWidgetRequest.startX,
    startY = moveWidgetRequest.startY,
    spanX = widget.spanX,
    spanY = widget.spanY,
    widgetType = widget.widgetType,
    label = widget.label,
    imagePath = widget.imagePath,
    intent = widget.intent)

  def toUpdatedWidget(widget: Widget, resizeWidgetRequest: ResizeWidgetRequest): Widget =  Widget(
    id = widget.id,
    momentId = widget.momentId,
    packageName = widget.packageName,
    className = widget.className,
    appWidgetId = widget.appWidgetId,
    startX = widget.startX,
    startY = widget.startY,
    spanX = resizeWidgetRequest.spanX,
    spanY = resizeWidgetRequest.spanY,
    widgetType = widget.widgetType,
    label = widget.label,
    imagePath = widget.imagePath,
    intent = widget.intent)

  def toServicesUpdateWidgetRequest(widget: Widget): ServicesUpdateWidgetRequest = ServicesUpdateWidgetRequest(
    id = widget.id,
    momentId = widget.momentId,
    packageName = widget.packageName,
    className = widget.className,
    appWidgetId = widget.appWidgetId,
    startX = widget.startX,
    startY = widget.startY,
    spanX = widget.spanX,
    spanY = widget.spanY,
    widgetType = widget.widgetType.name,
    label = widget.label,
    imagePath = widget.imagePath,
    intent = widget.intent)
}
