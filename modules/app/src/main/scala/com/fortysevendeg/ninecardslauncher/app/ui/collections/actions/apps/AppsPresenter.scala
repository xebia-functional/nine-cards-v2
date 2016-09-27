package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, AllAppsCategory, Misc, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetAppOrder, GetByName}
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, App, IterableApps}


case class AppsPresenter(
  category: NineCardCategory,
  actions: AppsIuActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions {

  def initialize(): Unit = {
    val onlyAllApps = showOnlyAllApps
    actions.initialize(onlyAllApps, category).run
    loadApps(if (onlyAllApps) AllApps else AppsByCategory, reload = false)
  }

  def destroy(): Unit = actions.destroy().run

  def loadApps(
    filter: AppsFilter,
    reload: Boolean = true): Unit = {
    val task = filter match {
      case AllApps => getLoadApps(GetByName)
      case AppsByCategory => getLoadAppsByCategory(category)
    }
    task.resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = {
        case (apps: IterableApps, counters: Seq[TermCounter]) =>
          actions.showApps(category, filter, apps, counters, reload) ~
            (if (actions.isTabsOpened) actions.closeTabs() else Ui.nop)
      },
      onException = (ex: Throwable) => actions.showErrorLoadingAppsInScreen(filter)
    )
  }

  def addApp(app: App): Unit = {
    val card = AddCardRequest(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = None)
    actions.appAdded(card).run
  }

  private[this] def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  private[this] def getLoadAppsByCategory(category: NineCardCategory): TaskService[(IterableApps, Seq[TermCounter])] =
    for {
      iterableApps <- di.deviceProcess.getIterableAppsByCategory(category.name)
    } yield (iterableApps, Seq.empty)

  private[this] def showOnlyAllApps: Boolean =
    category == AllAppsCategory || category == Misc

}

trait AppsIuActions {

  def initialize(onlyAllApps: Boolean, category: NineCardCategory): Ui[Any]

  def showLoading(): Ui[Any]

  def closeTabs(): Ui[Any]

  def destroy(): Ui[Any]

  def showErrorLoadingAppsInScreen(filter: AppsFilter): Ui[Any]

  def showApps(
    category: NineCardCategory,
    filter: AppsFilter,
    apps: IterableApps,
    counters: Seq[TermCounter],
    reload: Boolean): Ui[Any]

  def appAdded(card: AddCardRequest): Ui[Any]

  def isTabsOpened: Boolean
}
