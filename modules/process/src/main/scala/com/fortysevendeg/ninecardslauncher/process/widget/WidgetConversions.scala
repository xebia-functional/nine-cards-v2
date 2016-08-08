package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.process.commons.types.WidgetType
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
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

  def toWidget(servicesWidget: ServicesWidget): AppWidget = AppWidget(
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

  def toUpdatedWidget(widget: AppWidget, moveWidgetRequest: MoveWidgetRequest): AppWidget =
    widget.copy(
      startX = widget.startX + moveWidgetRequest.displaceX,
      startY = widget.startY + moveWidgetRequest.displaceY)

  def toUpdatedWidget(widget: AppWidget, resizeWidgetRequest: ResizeWidgetRequest): AppWidget =
    widget.copy(
      spanX = widget.spanX + resizeWidgetRequest.increaseX,
      spanY = widget.spanY + resizeWidgetRequest.increaseY)

  def toServicesUpdateWidgetRequest(widget: AppWidget): ServicesUpdateWidgetRequest = ServicesUpdateWidgetRequest(
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
