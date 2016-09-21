package com.fortysevendeg.ninecardslauncher.process.trackevent

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.trackevent.models.{Category, MomentCategory}

trait TrackEventProcess {

  /**
    * Track when the user opens an application from app drawer
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromAppDrawer(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user opens an application from collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user adds an app to collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def addAppToCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user removes an application in collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def removeFromCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user adds a widget in moment
    *
    * @param packageName package name of app
    * @param className class of the widget
    * @param moment moment where it's added
    */
  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory): TaskService[Unit]
}
