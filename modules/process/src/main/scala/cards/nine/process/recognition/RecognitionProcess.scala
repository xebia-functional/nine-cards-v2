package cards.nine.process.recognition

import android.content.BroadcastReceiver
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Headphones, Location, ProbablyActivity, WeatherState}

trait RecognitionProcess {

  /**
    * Get most probably activity: in vehicle, walking, running, etc
    *
    * @return ProbablyActivity
    */
  def getMostProbableActivity: TaskService[ProbablyActivity]

  /**
    * Register a pending intent for fence updates
    * @param action the action for the intent
    * @param receiver that will receive the updates
    */
  def registerFenceUpdates(action: String, receiver: BroadcastReceiver)(implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
    * Register a pending intent for fence updates
    * @param action the action for the intent
    */
  def unregisterFenceUpdates(action: String)(implicit contextSupport: ContextSupport): TaskService[Unit]

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
    * @return WeatherState
    */
  def getWeather: TaskService[WeatherState]

}
