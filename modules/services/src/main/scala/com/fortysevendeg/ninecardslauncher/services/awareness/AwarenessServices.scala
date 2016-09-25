package com.fortysevendeg.ninecardslauncher.services.awareness

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

trait AwarenessServices {

  /**
    * Return the most probably activity
    *
    * @return activity
    * @throws AwarenessException if there was an error with the request GoogleDrive api
    */
  def getTypeActivity: TaskService[TypeActivity]

  /**
    * Return headphone state
    *
    * @return if headphone is connected
    * @throws AwarenessException if there was an error with the request GoogleDrive api
    */
  def getHeadphonesState: TaskService[HeadphonesState]

  /**
    * Return information about current location
    *
    * @return current location
    * @throws AwarenessException if there was an error with the request GoogleDrive api
    */
  def getLocation(implicit contextSupport: ContextSupport): TaskService[AwarenessLocation]

  /**
    * Return information about current weather
    *
    * @return current weather
    * @throws AwarenessException if there was an error with the request GoogleDrive api
    */
  def getWeather: TaskService[WeatherState]

}
