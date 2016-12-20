package cards.nine.services.awareness

import android.content.BroadcastReceiver
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.AwarenessFenceUpdate
import cards.nine.models.{Headphones, Location, ProbablyActivity, WeatherState}

trait AwarenessServices {

  /**
   * Return the most probably activity
   *
   * @return activity
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getTypeActivity: TaskService[ProbablyActivity]

  /**
   * Register a pending intent for fence updates
   * @param action the action for the intent
   * @param fences fences to register for
   * @param receiver that will receive the updates
   */
  def registerFenceUpdates(
      action: String,
      fences: Seq[AwarenessFenceUpdate],
      receiver: BroadcastReceiver)(implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
   * Register a pending intent for fence updates
   * @param action the action for the intent
   */
  def unregisterFenceUpdates(action: String)(
      implicit contextSupport: ContextSupport): TaskService[Unit]

  /**
   * Return headphone state
   *
   * @return if headphone is connected
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getHeadphonesState: TaskService[Headphones]

  /**
   * Return information about current location
   *
   * @return current location
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getLocation(implicit contextSupport: ContextSupport): TaskService[Location]

  /**
   * Return information about current weather
   *
   * @return current weather
   * @throws AwarenessException if there was an error with the request GoogleDrive api
   */
  def getWeather: TaskService[WeatherState]

}
