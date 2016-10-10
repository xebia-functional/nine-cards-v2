package cards.nine.process.widget.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.{WidgetData, Widget}
import cards.nine.process.widget._
import cards.nine.services.persistence._

class WidgetProcessImpl(
  val persistenceServices: PersistenceServices)
  extends WidgetProcess
  with ImplicitsWidgetException {

  override def getWidgets = (persistenceServices.fetchWidgets).resolve[AppWidgetException]

  override def getWidgetById(widgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId)
    } yield widget).resolve[AppWidgetException]

  override def getWidgetByAppWidgetId(appWidgetId: Int) =
    (for {
      widget <- persistenceServices.fetchWidgetByAppWidgetId(appWidgetId)
    } yield widget).resolve[AppWidgetException]

  override def getWidgetsByMoment(momentId: Int) =
    (for {
      widgets <- persistenceServices.fetchWidgetsByMoment(momentId)
    } yield widgets).resolve[AppWidgetException]

  override def addWidget(addWidgetRequest: WidgetData) =
    (for {
      widget <- persistenceServices.addWidget(addWidgetRequest)
    } yield widget).resolve[AppWidgetException]

  override def addWidgets(request: Seq[WidgetData]) =
    (for {
      widgets <- persistenceServices.addWidgets(request)
    } yield widgets).resolve[AppWidgetException]

  override def moveWidget(widgetId: Int, displaceX: Int, displaceY: Int) = {

    def toUpdatedWidget(widget: Widget, displaceX: Int, displaceY: Int): Widget =
      widget.copy(
        area = widget.area.copy(
          startX = widget.area.startX + displaceX,
          startY = widget.area.startY + displaceY))

    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(widget, displaceX, displaceY)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]

  }

  override def resizeWidget(widgetId: Int, increaseX: Int, increaseY: Int) = {

    def toUpdatedWidget(widget: Widget, increaseX: Int, increaseY: Int): Widget =
      widget.copy(
        area = widget.area.copy(
          spanX = widget.area.spanX + increaseX,
          spanY = widget.area.spanY + increaseY))

    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(widget, increaseX, increaseY)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]

  }

  override def updateAppWidgetId(widgetId: Int, appWidgetId: Int) = {

    def toUpdatedWidget(widget: Widget, appWidgetId: Int): Widget =
      widget.copy(appWidgetId = Option(appWidgetId))

    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      updatedWidget = toUpdatedWidget(widget, appWidgetId)
      _ <- updateWidget(updatedWidget)
    } yield updatedWidget).resolve[AppWidgetException]
  }

  override def deleteAllWidgets() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
    } yield ()).resolve[AppWidgetException]

  override def deleteWidget(widgetId: Int) =
    (for {
      widget <- findWidgetById(widgetId).resolveOption()
      _ <- persistenceServices.deleteWidget(widget)
    } yield ()).resolve[AppWidgetException]

  override def deleteWidgetsByMoment(momentId: Int) =
    (for {
      _ <- persistenceServices.deleteWidgetsByMoment(momentId: Int)
    } yield ()).resolve[AppWidgetException]

  private[this] def findWidgetById(widgetId: Int) =
    (for {
      widget <- persistenceServices.findWidgetById(widgetId)
    } yield widget).resolve[AppWidgetException]

  private[this] def updateWidget(widget: Widget) =
    (for {
      _ <- persistenceServices.updateWidget(widget)
    } yield ()).resolve[AppWidgetException]

}
