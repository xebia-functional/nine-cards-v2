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

  override def getWidgets = persistenceServices.fetchWidgets.resolve[AppWidgetException]

  override def getWidgetById(widgetId: Int) =
    findWidgetById(widgetId).resolve[AppWidgetException]

  override def getWidgetByAppWidgetId(appWidgetId: Int) =
    persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).resolve[AppWidgetException]

  override def getWidgetsByMoment(momentId: Int) =
    persistenceServices.fetchWidgetsByMoment(momentId).resolve[AppWidgetException]

  override def addWidget(addWidgetRequest: WidgetData) =
    persistenceServices.addWidget(addWidgetRequest).resolve[AppWidgetException]

  override def addWidgets(request: Seq[WidgetData]) =
    persistenceServices.addWidgets(request).resolve[AppWidgetException]

  override def updateWidgets(widgets: Seq[Widget]) =
    (for {
      _ <- persistenceServices.updateWidgets(widgets)
    } yield ()).resolve[AppWidgetException]

  override def moveWidget(widgetId: Int, displaceX: Int, displaceY: Int) = {

    def toUpdatedWidget(widget: Widget, displaceX: Int, displaceY: Int): Widget =
      widget.copy(
        area = widget.area.copy(
          startX = widget.area.startX + displaceX,
          startY = widget.area.startY + displaceY))

    (for {
      widget <- fetchWidgetById(widgetId)
      updatedWidget = toUpdatedWidget(widget, displaceX, displaceY)
      _ <- updateWidgets(Seq(updatedWidget))
    } yield updatedWidget).resolve[AppWidgetException]

  }

  override def resizeWidget(widgetId: Int, increaseX: Int, increaseY: Int) = {

    def toUpdatedWidget(widget: Widget, increaseX: Int, increaseY: Int): Widget =
      widget.copy(
        area = widget.area.copy(
          spanX = widget.area.spanX + increaseX,
          spanY = widget.area.spanY + increaseY))

    (for {
      widget <- fetchWidgetById(widgetId)
      updatedWidget = toUpdatedWidget(widget, increaseX, increaseY)
      _ <- updateWidgets(Seq(updatedWidget))
    } yield updatedWidget).resolve[AppWidgetException]

  }

  override def updateAppWidgetId(widgetId: Int, appWidgetId: Int) = {

    def toUpdatedWidget(widget: Widget, appWidgetId: Int): Widget =
      widget.copy(appWidgetId = Option(appWidgetId))

    (for {
      widget <- fetchWidgetById(widgetId)
      updatedWidget = toUpdatedWidget(widget, appWidgetId)
      _ <- updateWidgets(Seq(updatedWidget))
    } yield updatedWidget).resolve[AppWidgetException]
  }

  override def deleteAllWidgets() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
    } yield ()).resolve[AppWidgetException]

  override def deleteWidget(widgetId: Int) =
    (for {
      widget <- fetchWidgetById(widgetId)
      _ <- persistenceServices.deleteWidget(widget)
    } yield ()).resolve[AppWidgetException]

  override def deleteWidgetsByMoment(momentId: Int) =
    (for {
      _ <- persistenceServices.deleteWidgetsByMoment(momentId: Int)
    } yield ()).resolve[AppWidgetException]

  private[this] def findWidgetById(widgetId: Int) =
    persistenceServices.findWidgetById(widgetId).resolve[AppWidgetException]

  private[this] def fetchWidgetById(widgetId: Int) =
    findWidgetById(widgetId).resolveOption(s"Can't find widget with id $widgetId")


}
