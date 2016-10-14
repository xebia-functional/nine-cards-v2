package cards.nine.process.widget

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Widget

trait WidgetProcess {

  /**
    * Gets the existing widgets
    *
    * @return the Seq[Widget] of existing widgets
    * @throws AppWidgetException if there was an error getting the existing widgets
    */
  def getWidgets: TaskService[Seq[Widget]]

  /**
    * Gets a widget by a given Id
    *
    * @param widgetId the Id of the Widget
    * @return the Option[Widget] corresponding with the Id
    * @throws AppWidgetException if there was an error getting the widget
    */
  def getWidgetById(widgetId: Int): TaskService[Option[Widget]]

  /**
    * Gets a widget by a given appWidgetId
    *
    * @param appWidgetId the appWidgetId of the Widget
    * @return the Option[Widget] corresponding with the appWidgetId
    * @throws AppWidgetException if there was an error getting the widget
    */
  def getWidgetByAppWidgetId(appWidgetId: Int): TaskService[Option[Widget]]

  /**
    * Gets the existing widgets related with a given moment
    *
    * @param momentId id of thw Moment
    * @return the Seq[Widget] of existing widgets related with the moment
    * @throws AppWidgetException if there was an error getting the existing widgets
    */
  def getWidgetsByMoment(momentId: Int): TaskService[Seq[Widget]]

  /**
    * Adds a new widget
    *
    * @param addWidgetRequest includes the necessary data to create a new widget
    * @return the new [[Widget]] added
    * @throws AppWidgetException if there was an error adding the new widget
    */
  def addWidget(addWidgetRequest: AddWidgetRequest): TaskService[Widget]

  /**
    * Adds a sequence of new widgets
    *
    * @param request a sequence including the necessary data to create a new widget
    * @return the Seq[Widget] of new widgets added
    * @throws AppWidgetException if there was an error adding the new widget
    */
  def addWidgets(request: Seq[AddWidgetRequest]): TaskService[Seq[Widget]]

  /**
    * Moves an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param moveWidgetRequest includes the new startX and startY coordenates
    * @return the [[Widget]] with the new position
    * @throws AppWidgetException if there was an error finding the widget or moving it
    */
  def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest): TaskService[Widget]

  /**
    * Resizes an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param resizeWidgetRequest includes the new spanX and spanY coordenates
    * @return the [[Widget]] with the new position
    * @throws AppWidgetException if there was an error finding the widget or resizing it
    */
  def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest): TaskService[Widget]

  /**
    * Update app widget id of Android SDK in database
    *
    * @param widgetId the Id of the Widget
    * @param appWidgetId app widget id in Android SDK
    * @return the [[Widget]] with the new position
    * @throws AppWidgetException if there was an error finding the widget or resizing it
    */
  def updateAppWidgetId(widgetId: Int, appWidgetId: Int): TaskService[Widget]

  /**
    * Delete all widgets in database
    *
    * @throws AppWidgetException if exist some problem deleting the widgets
    */
  def deleteAllWidgets(): TaskService[Unit]

  /**
    * Deletes a widget
    *
    * @param widgetId the Id of the Widget
    * @throws AppWidgetException if there was an error finding the widget or resizing it
    */
  def deleteWidget(widgetId: Int): TaskService[Unit]

  /**
    * Delete all widgets in database with a given momentId
    *
    * @param momentId the Id of the moment associated with the widgets
    * @throws AppWidgetException if exist some problem deleting the widgets
    */
  def deleteWidgetsByMoment(momentId: Int): TaskService[Unit]


}
