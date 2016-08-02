package com.fortysevendeg.ninecardslauncher.process.widget

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.widget.models._

trait WidgetProcess {

  /**
    * Adds a new Widget
    *
    * @param addWidgetRequest includes the necessary data to create a new widget
    * @return the [[Widget]]
    * @throws WidgetException if there was an error adding the new widget
    */
  def addWidget(addWidgetRequest: AddWidgetRequest): ServiceDef2[Widget, WidgetException]

  /**
    * Moves an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param moveWidgetRequest includes the new startX and startY coordenates
    * @return the [[Widget]] with the new position
    * @throws WidgetException if there was an error finding the widget or moving it
    */
  def moveWidget(widgetId: Int, moveWidgetRequest: MoveWidgetRequest): ServiceDef2[Widget, WidgetException]

  /**
    * Resizes an existing widget in the workspace
    *
    * @param widgetId the Id of the Widget
    * @param resizeWidgetRequest includes the new spanX and spanY coordenates
    * @return the [[Widget]] with the new position
    * @throws WidgetException if there was an error finding the widget or resizing it
    */
  def resizeWidget(widgetId: Int, resizeWidgetRequest: ResizeWidgetRequest): ServiceDef2[Widget, WidgetException]

  /**
    * Deletes a Widget
    *
    * @param widgetId the Id of the Widget
    * @throws WidgetException if there was an error finding the widget or resizing it
    */
  def deleteWidget(widgetId: Int): ServiceDef2[Unit, WidgetException]

}
