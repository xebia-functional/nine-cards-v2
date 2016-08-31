package com.fortysevendeg.ninecardslauncher.services.persistence.data

import com.fortysevendeg.ninecardslauncher.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget

import scala.util.Random

trait WidgetPersistenceServicesData extends PersistenceServicesData {

  val nonExistentWidgetId: Int = Random.nextInt(10) + 100
  val nonExistentAppWidgetId: Int = Random.nextInt(10) + 100

  def createSeqWidget(
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
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption) = List.tabulate(num)(
    item =>
      Widget(
        id = id + item,
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

  val seqWidget: Seq[Widget] = createSeqWidget()
  val servicesWidget: Widget = seqWidget(0)
  val repoWidget: RepositoryWidget = seqRepoWidget(0)

  def createAddWidgetRequest(
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption): AddWidgetRequest =
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
      intent = intent)

  def createDeleteWidgetRequest(widget: Widget): DeleteWidgetRequest = DeleteWidgetRequest(widget = widget)

  def createUpdateWidgetsRequest(
    num: Int = 5,
    id: Int = widgetId) =
    UpdateWidgetsRequest(
      List.tabulate(num)(item => createUpdateWidgetRequest(id = id + item)))

  def createUpdateWidgetRequest(
    id: Int = widgetId,
    momentId: Int = momentId,
    packageName: String = packageName,
    className: String = className,
    appWidgetId: Int = appWidgetId,
    startX: Int = startX,
    startY: Int = startY,
    spanX: Int = spanX,
    spanY: Int = spanY,
    widgetType: String = widgetType,
    label: Option[String] = labelOption,
    imagePath: Option[String] = widgetImagePathOption,
    intent: Option[String] = widgetIntentOption): UpdateWidgetRequest =
    UpdateWidgetRequest(
      id = id,
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
      intent = intent)
}
