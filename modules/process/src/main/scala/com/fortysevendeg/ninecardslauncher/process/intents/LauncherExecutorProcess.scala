package com.fortysevendeg.ninecardslauncher.process.intents

import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent

trait LauncherExecutorProcess {

  /**
    * Executes a NineCardIntent
    * @param intent the intent
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception or
    *         an IntentLauncherServicesPermissionException if this exception is a SecurityException
    */
  def execute(intent: NineCardIntent)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the contact preview based on a lookup key
    * @param contactLookupKey the lookup key
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def executeContact(contactLookupKey: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch a share intent with the title and text specified
    * @param text the text
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchShare(text: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the search intent
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the Google weather intent
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchGoogleWeather(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the voice search intent
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchVoiceSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the settings for a specific application
    * @param packageName the application package
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchSettings(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the intent for uninstall for a specific application
    * @param packageName the application package
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchUninstall(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the phone dial
    * @param phoneNumber an optional phone number that will be sent to the dial activity
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchDial(phoneNumber: Option[String] = None)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the intent for the Google Play Store
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchPlayStore(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch a specific application
    * @param packageName the application package
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchApp(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the Google Play Store view for a specific application
    * @param packageName the application package
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchGooglePlay(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

  /**
    * Launch the intent for an url
    * @param url the web url
    * @return A TaskService[Unit] with an IntentLauncherServicesException if the internal API throws an Exception
    */
  def launchUrl(url: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit]

}
