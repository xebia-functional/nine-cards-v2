package com.fortysevendeg.ninecardslauncher.process.trackevent

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

trait TrackEventProcess {

  /**
    * Track when the user opens an application in category collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromAppDrawer(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user opens an application in moment collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user opens an application in moment collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def addToCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Track when the user opens an application in moment collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def removedInCollection(packageName: String, category: Category): TaskService[Unit]
}
