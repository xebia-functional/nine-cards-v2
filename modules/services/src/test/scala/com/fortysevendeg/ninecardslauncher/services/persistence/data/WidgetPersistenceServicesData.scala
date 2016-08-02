package com.fortysevendeg.ninecardslauncher.services.persistence.data

import com.fortysevendeg.ninecardslauncher.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget

import scala.util.Random

trait WidgetPersistenceServicesData extends PersistenceServicesData {

  val widgetId: Int = Random.nextInt(10)
  val nonExistentWidgetId: Int = Random.nextInt(10) + 100
  val appWidgetId: Int = Random.nextInt(10)
  val nonExistentAppWidgetId: Int = Random.nextInt(10) + 100
  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)
  val widgetType: String = Random.nextString(5)
  val label: String = Random.nextString(5)
  val widgetImagePath: String = Random.nextString(5)
  val widgetIntent: String = Random.nextString(5)
  val labelOption = Option(label)
  val widgetImagePathOption = Option(widgetImagePath)
  val widgetIntentOption = Option(widgetIntent)

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

  def createSeqRepoWidget(
    num: Int = 5,
    id: Int = widgetId,
    data: RepositoryWidgetData = createRepoWidgetData()): Seq[RepositoryWidget] =
    List.tabulate(num)(item => RepositoryWidget(id = id + item, data = data))

  def createRepoWidgetData(
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
    intent: Option[String] = widgetIntentOption): RepositoryWidgetData =
    RepositoryWidgetData(
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

  val seqWidget: Seq[Widget] = createSeqWidget()
  val servicesWidget: Widget = seqWidget(0)
  val repoWidgetData: RepositoryWidgetData = createRepoWidgetData()
  val seqRepoWidget: Seq[RepositoryWidget] = createSeqRepoWidget(data = repoWidgetData)
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

  def createFetchWidgetByAppWidgetIdRequest(appWidgetId: Int): FetchWidgetByAppWidgetIdRequest =
    FetchWidgetByAppWidgetIdRequest(appWidgetId = appWidgetId)

  def createFetchWidgetsByMomentRequest(momentId: Int): FetchWidgetsByMomentRequest =
    FetchWidgetsByMomentRequest(momentId = momentId)

  def createFindWidgetByIdRequest(id: Int): FindWidgetByIdRequest = FindWidgetByIdRequest(id = id)

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
