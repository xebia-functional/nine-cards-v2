package cards.nine.services.intents

import android.content.Intent
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.services.intents.models.IntentAction

trait LauncherIntentServices {

  /**
    * This method try to execute an intent based on the provided action
    * @param intentAction specifies the intent to be executed
    * @return a TaskService[Unit] that will contain an Unit if the intent has been executed successfully
    *         an IntentLauncherServicesPermissionException if there are insufficient permissions to execute the intent or
    *         an IntentLauncherServicesException if the service can't access to the activity or the execution throw a different exception
    */
  def launchIntentAction(intentAction: IntentAction)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * This method try to execute directly an intent
    * @param intent the intent to be executed
    * @return a TaskService[Unit] that will contain an Unit if the intent has been executed successfully
    *         an IntentLauncherServicesPermissionException if there are insufficient permissions to execute the intent or
    *         an IntentLauncherServicesException if the service can't access to the activity or the execution throw a different exception
    */
  def launchIntent(intent: Intent)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

}
