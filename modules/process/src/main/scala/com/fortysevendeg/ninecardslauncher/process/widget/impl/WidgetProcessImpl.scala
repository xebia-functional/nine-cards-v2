package com.fortysevendeg.ninecardslauncher.process.widget.impl

import cards.nine.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.widget.{AddWidgetRequest, _}
import com.fortysevendeg.ninecardslauncher.process.widget.models.AppWidget
import com.fortysevendeg.ninecardslauncher.services.persistence.{DeleteWidgetRequest => ServicesDeleteWidgetRequest, _}
import cards.nine.commons.services.TaskService._


class WidgetProcessImpl(
  val persistenceServices: PersistenceServices)
  extends WidgetProcess
  with ImplicitsWidgetException
  with ImplicitsPersistenceServiceExceptions
  with WidgetConversions {

  override def getWidgets = (persistenceServices.fetchWidgets map toWidgetSeq).resolve[AppWidgetException]

  override def getWidgetById(widgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId)
    } yield widget map toWidget).resolve[AppWidgetException]

  override def getWidgetByAppWidgetId(appWidgetId: Int) =
    (for {
      widget <- persistenceServices.fetchWidgetByAppWidgetId(appWidgetId)
    } yield widget map toWidget).resolve[AppWidgetException]

  override def getWidgetsByMoment(momentId: Int) =
    (for {
      widgets <- persistenceServices.fetchWidgetsByMoment(momentId)
    } yield widgets map toWidget).resolve[AppWidgetException]

  override def addWidget(addWidgetRequest: AddWidgetRequest) =
    (for {
      widget <- persistenceServices.addWidget(toAddWidgetRequest(addWidgetRequest))
    } yield toWidget(widget)).resolve[AppWidgetException]

  override def addWidgets(request: Seq[AddWidgetRequest]) =
    (for {
      widgets <- persistenceServices.addWidgets(request map toAddWidgetRequest)
    } yield widgets map toWidget).resolve[AppWidgetException]

  override def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest) =
    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(toWidget(widget), moveWidgetRequest)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]

  override def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest) =
    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(toWidget(widget), resizeWidgetRequest)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]

  override def updateAppWidgetId(widgetId: Int, appWidgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(toWidget(widget), appWidgetId)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]

  override def deleteAllWidgets() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
    } yield ()).resolve[AppWidgetException]

  override def deleteWidget(widgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      _ <- persistenceServices.deleteWidget(ServicesDeleteWidgetRequest(widget))
    } yield ()).resolve[AppWidgetException]

  override def deleteWidgetsByMoment(momentId: Int) =
    (for {
      _ <- persistenceServices.deleteWidgetsByMoment(momentId: Int)
    } yield ()).resolve[AppWidgetException]

  private[this] def findWidgetById(widgetId: Int) =
    (for {
      widget <- persistenceServices.findWidgetById(widgetId)
    } yield widget).resolve[AppWidgetException]

  private[this] def updateWidget(widget: AppWidget) =
    (for {
      _ <- persistenceServices.updateWidget(toServicesUpdateWidgetRequest(widget))
    } yield ()).resolve[AppWidgetException]

}
