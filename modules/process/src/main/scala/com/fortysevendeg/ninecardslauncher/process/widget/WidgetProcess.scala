package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.widget.models._

trait WidgetProcess {

  /**
    * Gets the existing widgets
    *
    * @return the Seq[Widget] of existing widgets
    * @throws WidgetException if there was an error getting the existing widgets
    */
  def getWidgets: ServiceDef2[Seq[AppWidget], WidgetException]

  /**
    * Gets a widget by a given Id
    *
    * @param widgetId the Id of the Widget
    * @return the Option[Widget] corresponding with the Id
    * @throws WidgetException if there was an error getting the widget
    */
  def getWidgetById(widgetId: Int): ServiceDef2[Option[AppWidget], WidgetException]

  /**
    * Gets a widget by a given appWidgetId
    *
    * @param appWidgetId the appWidgetId of the Widget
    * @return the Option[Widget] corresponding with the appWidgetId
    * @throws WidgetException if there was an error getting the widget
    */
  def getWidgetByAppWidgetId(appWidgetId: Int): ServiceDef2[Option[AppWidget], WidgetException]

  /**
    * Gets the existing widgets related with a given moment
    *
    * @param momentId id of thw Moment
    * @return the Seq[Widget] of existing widgets related with the moment
    * @throws WidgetException if there was an error getting the existing widgets
    */
  def getWidgetsByMoment(momentId: Int): ServiceDef2[Seq[AppWidget], WidgetException]

  /**
    * Adds a new widget
    *
    * @param addWidgetRequest includes the necessary data to create a new widget
    * @return the new [[AppWidget]] added
    * @throws WidgetException if there was an error adding the new widget
    */
  def addWidget(addWidgetRequest: AddWidgetRequest): ServiceDef2[AppWidget, WidgetException]

  /**
    * Adds a sequence of new widgets
    *
    * @param request a sequence including the necessary data to create a new widget
    * @return the Seq[Widget] of new widgets added
    * @throws WidgetException if there was an error adding the new widget
    */
  def addWidgets(request: Seq[AddWidgetRequest]): ServiceDef2[Seq[AppWidget], WidgetException]

  /**
    * Moves an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param moveWidgetRequest includes the new startX and startY coordenates
    * @return the [[AppWidget]] with the new position
    * @throws WidgetException if there was an error finding the widget or moving it
    */
  def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest): ServiceDef2[AppWidget, WidgetException]

  /**
    * Resizes an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param resizeWidgetRequest includes the new spanX and spanY coordenates
    * @return the [[AppWidget]] with the new position
    * @throws WidgetException if there was an error finding the widget or resizing it
    */
  def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest): ServiceDef2[AppWidget, WidgetException]

  /**
    * Delete all widgets in database
    *
    * @throws WidgetException if exist some problem deleting the widgets
    */
  def deleteAllWidgets(): ServiceDef2[Unit, WidgetException]

  /**
    * Deletes a widget
    *
    * @param widgetId the Id of the Widget
    * @throws WidgetException if there was an error finding the widget or resizing it
    */
  def deleteWidget(widgetId: Int): ServiceDef2[Unit, WidgetException]

  /**
    * Delete all widgets in database with a given momentId
    *
    * @param momentId the Id of the moment associated with the widgets
    * @throws WidgetException if exist some problem deleting the widgets
    */
  def deleteWidgetsByMoment(momentId: Int): ServiceDef2[Unit, WidgetException]


}
