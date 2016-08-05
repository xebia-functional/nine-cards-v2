package com.fortysevendeg.ninecardslauncher.process.widget.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.widget.{AddWidgetRequest, _}
import com.fortysevendeg.ninecardslauncher.process.widget.models.Widget
import com.fortysevendeg.ninecardslauncher.services.persistence.{DeleteWidgetRequest => ServicesDeleteWidgetRequest, _}

class WidgetProcessImpl(
  val persistenceServices: PersistenceServices)
  extends WidgetProcess
  with ImplicitsWidgetException
  with ImplicitsPersistenceServiceExceptions
  with WidgetConversions {

  override def getWidgets = (persistenceServices.fetchWidgets map toWidgetSeq).resolve[WidgetException]

  override def getWidgetById(widgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId)
    } yield widget map toWidget).resolve[WidgetException]

  override def getWidgetByAppWidgetId(appWidgetId: Int) =
    (for {
      widget <- persistenceServices.fetchWidgetByAppWidgetId(appWidgetId)
    } yield widget map toWidget).resolve[WidgetException]

  override def getWidgetsByMoment(momentId: Int) =
    (for {
      widgets <- persistenceServices.fetchWidgetsByMoment(momentId)
    } yield widgets map toWidget).resolve[WidgetException]

  override def addWidget(addWidgetRequest: AddWidgetRequest) =
    (for {
      widget <- persistenceServices.addWidget(toAddWidgetRequest(addWidgetRequest))
    } yield toWidget(widget)).resolve[WidgetException]

  override def addWidgets(request: Seq[AddWidgetRequest]) =
    (for {
      widgets <- persistenceServices.addWidgets(request map toAddWidgetRequest)
    } yield widgets map toWidget).resolve[WidgetException]

  override def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest) =
    (for {
      Some(widget) <- findWidgetById(widgetId)
      updatedWidget = toUpdatedWidget(toWidget(widget), moveWidgetRequest)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[WidgetException]

  override def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest) =
    (for {
      Some(widget) <- findWidgetById(widgetId)
      updatedWidget = toUpdatedWidget(toWidget(widget), resizeWidgetRequest)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[WidgetException]

  override def deleteAllWidgets() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
    } yield ()).resolve[WidgetException]

  override def deleteWidget(widgetId: Int) =
    (for {
      Some(widget) <- findWidgetById(widgetId)
      _ <- persistenceServices.deleteWidget(ServicesDeleteWidgetRequest(widget))
    } yield ()).resolve[WidgetException]

  override def deleteWidgetsByMoment(momentId: Int) =
    (for {
      _ <- persistenceServices.deleteWidgetsByMoment(momentId: Int)
    } yield ()).resolve[WidgetException]

  private[this] def findWidgetById(widgetId: Int) =
    (for {
      widget <- persistenceServices.findWidgetById(widgetId)
    } yield widget).resolve[WidgetException]

  private[this] def updateWidget(widget: Widget) =
    (for {
      _ <- persistenceServices.updateWidget(toServicesUpdateWidgetRequest(widget))
    } yield ()).resolve[WidgetException]

}
