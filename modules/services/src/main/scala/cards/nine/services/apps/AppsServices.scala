package cards.nine.services.apps

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.ApplicationData

trait AppsServices {

  /**
   * Obtains a sequence with all the installed apps
   *
   * @return the Seq[cards.nine.models.ApplicationData] with the data of the apps
   * @throws AppsInstalledException if exist some problem obtaining the installed apps
   */
  def getInstalledApplications(implicit context: ContextSupport): TaskService[Seq[ApplicationData]]

  /**
   * Obtains an installed app by the package name
   *
   * @param packageName the package name of the app to get
   * @return the [cards.nine.models.ApplicationData] with the data of the app
   * @throws AppsInstalledException if exist some problem obtaining the installed app
   */
  def getApplication(packageName: String)(
      implicit context: ContextSupport): TaskService[ApplicationData]

  /**
   * Return a sequence with the default apps for ten predefined actions
   *
   * @return the Seq[cards.nine.models.ApplicationData] with the data of the apps
   * @throws AppsInstalledException if there was an error with trying to get the default apps
   */
  def getDefaultApps(implicit context: ContextSupport): TaskService[Seq[ApplicationData]]
}
