package com.fortysevendeg.ninecardslauncher.services.persistence.data

import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.WidgetValues._
import cards.nine.repository.model.{Widget => RepositoryWidget, WidgetData => RepositoryWidgetData}

trait WidgetPersistenceServicesData {

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

  val repoWidgetData: RepositoryWidgetData = createRepoWidgetData()
  val seqRepoWidget: Seq[RepositoryWidget] = createSeqRepoWidget(data = repoWidgetData)
  val repoWidgetDataNone: RepositoryWidgetData = createRepoWidgetData(appWidgetId = 0)
  val seqRepoWidgetNone: Seq[RepositoryWidget] = createSeqRepoWidget(data = repoWidgetDataNone)

  val repoWidget = seqRepoWidget(0)
  val repoWidgetNone = seqRepoWidgetNone(0)

}
