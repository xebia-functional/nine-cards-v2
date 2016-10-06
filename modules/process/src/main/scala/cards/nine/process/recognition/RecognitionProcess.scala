package cards.nine.process.recognition

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Location, Headphones, ProbablyActivity, WeatherState}

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
  def getWeather: TaskService[WeatherState]

}
