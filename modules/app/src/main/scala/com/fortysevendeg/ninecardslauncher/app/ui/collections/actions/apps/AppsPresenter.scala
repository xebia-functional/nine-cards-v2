package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, AllAppsCategory, Misc, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetAppOrder, GetByName}
import macroid.{ActivityContextWrapper, Ui}
import com.fortysevendeg.ninecardslauncher.process.device.models.{TermCounter, App, IterableApps}

import scalaz.concurrent.Task

case class AppsPresenter(
  category: NineCardCategory,
  actions: AppsIuActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Presenter
  with NineCardIntentConversions {

  def initialize(): Unit = {
    val onlyAllApps = showOnlyAllApps
    actions.initialize(onlyAllApps, category).run
    loadApps(if (onlyAllApps) AllApps else AppsByCategory, reload = false)
  }

  def loadApps(
    filter: AppsFilter,
    reload: Boolean = true): Unit = {
    val task = filter match {
      case AllApps => getLoadApps(GetByName)
      case AppsByCategory => getLoadAppsByCategory(category)
    }
    Task.fork(task.run).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = {
        case (apps: IterableApps, counters: Seq[TermCounter]) =>
          actions.showApps(category, filter, apps, counters, reload) ~
            (if (actions.isTabsOpened) {
              actions.closeTabs()
            } else {
              Ui.nop
            })
      },
      onException = (ex: Throwable) => actions.showLoadingAppsError(filter)
    )
  }

  def addApp(app: App): Unit = {
    val card = AddCardRequest(
      term = app.name,
      packageName = Option(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = app.imagePath
    )
    actions.appAdded(card).run
  }

  private[this] def getLoadApps(order: GetAppOrder): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  private[this] def getLoadAppsByCategory(category: NineCardCategory): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
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

  def showLoadingAppsError(filter: AppsFilter): Ui[Any]

  def showApps(
    category: NineCardCategory,
    filter: AppsFilter,
    apps: IterableApps,
    counters: Seq[TermCounter],
    reload: Boolean): Ui[Any]

  def appAdded(card: AddCardRequest): Ui[Any]

  def isTabsOpened: Boolean
}
