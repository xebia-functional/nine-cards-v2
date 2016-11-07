package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.commons.{Conversions, AppNineCardsIntentConversions}
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService._
import cards.nine.models.{CardData, ApplicationData, TermCounter}
import cards.nine.models.types._
import cards.nine.process.device.models.IterableApps
import macroid.ActivityContextWrapper
import cards.nine.app.ui.collections.actions.apps.AppsFragment._

case class AppsJobs(actions: AppsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardsIntentConversions
  with Conversions {

  def initialize(selectedApps: Set[String]): TaskService[Unit] =
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

  def getAddedAndRemovedApps: TaskService[(Seq[CardData], Seq[CardData])] = {

    val initialPackages = appStatuses.initialPackages
    val selectedPackages = appStatuses.selectedPackages

    def getCardsFromPackages(packageNames: Set[String], apps: Seq[ApplicationData]): Seq[CardData] =
      (packageNames flatMap { packageName =>
        apps.find(_.packageName == packageName)
      } map toCardData).toSeq

    for {
      allApps <- di.deviceProcess.getSavedApps(GetByName)
    } yield {
      val cardsToAdd = getCardsFromPackages(selectedPackages.diff(initialPackages), allApps)
      val cardsToRemove = getCardsFromPackages(initialPackages.diff(selectedPackages), allApps)
      (cardsToAdd, cardsToRemove)
    }
  }

  def updateSelectedApps(packages: Set[String]): TaskService[Unit] = actions.showUpdateSelectedApps(packages)

  def showErrorLoadingApps(): TaskService[Unit] = actions.showErrorLoadingAppsInScreen()

  def showError(): TaskService[Unit] = actions.showError()

  def close(): TaskService[Unit] = actions.close()

}
