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

  override def addWidget(addWidgetRequest: AddWidgetRequest) =
    (for {
      widget <- persistenceServices.addWidget(toAddWidgetRequest(addWidgetRequest))
      } yield toWidget(widget)).resolve[WidgetException]

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

  override def deleteWidget(widgetId: Int) =
    (for {
    Some(widget) <- findWidgetById(widgetId)
    _ <- persistenceServices.deleteWidget(ServicesDeleteWidgetRequest(widget))
  } yield ()).resolve[WidgetException]

  private[this] def findWidgetById(id: Int) =
    (for {
      widget <- persistenceServices.findWidgetById(toFindWidgetByIdRequest(id))
    } yield widget).resolve[WidgetException]

  private[this] def updateWidget(widget: Widget) =
    (for {
      _ <- persistenceServices.updateWidget(toServicesUpdateWidgetRequest(widget))
    } yield ()).resolve[WidgetException]

}
