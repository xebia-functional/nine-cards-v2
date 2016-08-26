package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.widget.models._

trait WidgetProcess {

  /**
    * Gets the existing widgets
    *
    * @return the Seq[Widget] of existing widgets
    * @throws AppWidgetException if there was an error getting the existing widgets
    */
  def getWidgets: TaskService[Seq[AppWidget]]

  /**
    * Gets a widget by a given Id
    *
    * @param widgetId the Id of the Widget
    * @return the Option[Widget] corresponding with the Id
    * @throws AppWidgetException if there was an error getting the widget
    */
  def getWidgetById(widgetId: Int): TaskService[Option[AppWidget]]

  /**
    * Gets a widget by a given appWidgetId
    *
    * @param appWidgetId the appWidgetId of the Widget
    * @return the Option[Widget] corresponding with the appWidgetId
    * @throws AppWidgetException if there was an error getting the widget
    */
  def getWidgetByAppWidgetId(appWidgetId: Int): TaskService[Option[AppWidget]]

  /**
    * Gets the existing widgets related with a given moment
    *
    * @param momentId id of thw Moment
    * @return the Seq[Widget] of existing widgets related with the moment
    * @throws AppWidgetException if there was an error getting the existing widgets
    */
  def getWidgetsByMoment(momentId: Int): TaskService[Seq[AppWidget]]

  /**
    * Adds a new widget
    *
    * @param addWidgetRequest includes the necessary data to create a new widget
    * @return the new [[AppWidget]] added
    * @throws AppWidgetException if there was an error adding the new widget
    */
  def addWidget(addWidgetRequest: AddWidgetRequest): TaskService[AppWidget]

  /**
    * Adds a sequence of new widgets
    *
    * @param request a sequence including the necessary data to create a new widget
    * @return the Seq[Widget] of new widgets added
    * @throws AppWidgetException if there was an error adding the new widget
    */
  def addWidgets(request: Seq[AddWidgetRequest]): TaskService[Seq[AppWidget]]

  /**
    * Moves an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param moveWidgetRequest includes the new startX and startY coordenates
    * @return the [[AppWidget]] with the new position
    * @throws AppWidgetException if there was an error finding the widget or moving it
    */
  def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest): TaskService[AppWidget]

  /**
    * Resizes an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param resizeWidgetRequest includes the new spanX and spanY coordenates
    * @return the [[AppWidget]] with the new position
    * @throws AppWidgetException if there was an error finding the widget or resizing it
    */
  def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest): TaskService[AppWidget]

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
