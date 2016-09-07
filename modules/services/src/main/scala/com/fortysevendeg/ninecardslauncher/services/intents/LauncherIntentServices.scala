package com.fortysevendeg.ninecardslauncher.services.intents

import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.intents.models.IntentAction

trait LauncherIntentServices {

  /**
    * This method try to execute an intent based on the provided action
    * @param intentAction specifies the intent to be executed
    * @return a TaskService[Unit] that will contain an Unit if the intent has been executed successfully
    *         an IntentLauncherServicesPermissionException if there are insufficient permissions to execute the intent or
    *         an IntentLauncherServicesException if the service can't access to the activity or the execution throw a different exception
    */
  def launchIntent(intentAction: IntentAction)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

}
