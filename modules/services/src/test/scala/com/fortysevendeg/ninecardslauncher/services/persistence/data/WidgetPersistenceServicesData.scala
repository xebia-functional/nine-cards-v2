package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.WidgetValues._
import cards.nine.repository.model.{Widget, WidgetData}

trait WidgetPersistenceServicesData {

  def repoWidgetData(num: Int = 0) = WidgetData(
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

  val repoWidgetData: WidgetData = repoWidgetData(0)
  val seqRepoWidgetData = Seq(repoWidgetData(0), repoWidgetData(1), repoWidgetData(2))

  def repoWidget(num: Int = 0) = Widget(
    id = widgetId + num,
    data = repoWidgetData(num))

  val repoWidget: Widget = repoWidget(0)
  val seqRepoWidget = Seq(repoWidget(0), repoWidget(1), repoWidget(2))

}
