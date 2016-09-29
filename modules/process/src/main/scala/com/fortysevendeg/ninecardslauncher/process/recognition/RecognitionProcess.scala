package com.fortysevendeg.ninecardslauncher.process.recognition

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._

trait RecognitionProcess {

  /**
    * Get most probably activity: in vehicle, walking, running, etc
    *
    * @return ProbablyActivity
    */
  def getMostProbableActivity: TaskService[ProbablyActivity]

  /**
    * Get if the headphones are connected
    *
    * @return Headphones
    */
  def getHeadphone: TaskService[Headphones]

  /**
    * Get the current location
    *
    * @return Location
    */
  def getLocation(implicit contextSupport: ContextSupport): TaskService[Location]

  /**
    * Get the current weather
    *
    * @return Weather
    */
  def getWeather: TaskService[Weather]

}
