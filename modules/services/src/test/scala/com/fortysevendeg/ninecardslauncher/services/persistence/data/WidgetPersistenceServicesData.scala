package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.WidgetValues._
import cards.nine.repository.model.{Widget, WidgetData}

trait WidgetPersistenceServicesData {

  def widgetData(num: Int = 0) = WidgetData(
    momentId = widgetMomentId,
    packageName = widgetPackageName,
    className = widgetClassName,
    appWidgetId = appWidgetId,
    startX = startX,
    startY = startY,
    spanX = spanX,
    spanY = spanY,
    widgetType = widgetType,
    label = Option(label),
    imagePath = Option(widgetImagePath),
    intent = Option(widgetIntent))

  val repoWidgetData: WidgetData = widgetData(0)
  val seqRepoWidgetData = Seq(widgetData(0), widgetData(1), widgetData(2))

  def widget(num: Int = 0) = Widget(
    id = widgetId + num,
    data = widgetData(num))

  val repoWidget: Widget = widget(0)
  val seqRepoWidget = Seq(widget(0), widget(1), widget(2))

}
