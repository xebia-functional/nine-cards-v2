package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService._
import cards.nine.models.TermCounter
import cards.nine.models.types._
import cards.nine.process.device.models.IterableApps
import macroid.ActivityContextWrapper

case class AppsJobs(actions: AppsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardsIntentConversions {

  def initialize(selectedApps: Seq[String]): TaskService[Unit] =
    for {
      _ <- actions.initialize(selectedApps)
      _ <- loadApps()
    } yield ()

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadApps(): TaskService[Unit] = {

    def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableApps(order)
        counters <- di.deviceProcess.getTermCountersForApps(order)
      } yield (iterableApps, counters)

    for {
      _ <- actions.showLoading()
      data <- getLoadApps(GetByName)
      (apps, counters) = data
      _ <- actions.showApps(apps, counters)
    } yield ()
  }

  def showErrorLoadingApps(): TaskService[Unit] = actions.showErrorLoadingAppsInScreen()

  def showError(): TaskService[Unit] = actions.showError()

  def close(): TaskService[Unit] = actions.close()

}
