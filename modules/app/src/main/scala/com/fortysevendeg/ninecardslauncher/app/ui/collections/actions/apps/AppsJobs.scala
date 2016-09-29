package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.process.commons.types.{AllAppsCategory, Misc, NineCardCategory}
import cards.nine.process.device.models.{IterableApps, TermCounter}
import cards.nine.process.device.{GetAppOrder, GetByName}
import macroid.ActivityContextWrapper

case class AppsJobs(
  category: NineCardCategory,
  actions: AppsIuActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions {

  def initialize(): TaskService[Unit] = {
    val onlyAllApps = category == AllAppsCategory || category == Misc
    for {
      _ <- actions.initialize(onlyAllApps, category)
      _ <- loadApps(if (onlyAllApps) AllApps else AppsByCategory, reload = false)
    } yield ()
  }

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadApps(filter: AppsFilter, reload: Boolean = true): TaskService[Unit] = {

    def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableApps(order)
        counters <- di.deviceProcess.getTermCountersForApps(order)
      } yield (iterableApps, counters)

    def getLoadAppsByCategory(category: NineCardCategory): TaskService[(IterableApps, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableAppsByCategory(category.name)
      } yield (iterableApps, Seq.empty)

    for {
      _ <- actions.showLoading()
      data <- filter match {
        case AllApps => getLoadApps(GetByName)
        case AppsByCategory => getLoadAppsByCategory(category)
      }
      (apps, counters) = data
      _ <- actions.showApps(category, filter, apps, counters, reload)
      isTabsOpened <- actions.isTabsOpened
      _ <- actions.closeTabs().resolveIf(isTabsOpened, ())
    } yield ()
  }

  def showErrorLoadingApps(filter: AppsFilter): TaskService[Unit] = actions.showErrorLoadingAppsInScreen(filter)

  def swapFilter(): TaskService[Unit] =
    for {
      isTabsOpened <- actions.isTabsOpened
      _ <- if (isTabsOpened) actions.closeTabs() else actions.openTabs()
    } yield ()

  def close(): TaskService[Unit] = actions.close()

}
