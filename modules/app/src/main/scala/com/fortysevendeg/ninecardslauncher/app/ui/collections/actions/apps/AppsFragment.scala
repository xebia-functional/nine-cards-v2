package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{UiExtensions, FragmentUiContext, NineCardIntentConversions, UiContext}
import com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AllAppsCategory, NineCardCategory, Game}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.device.GetByName
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.types.AppCardType
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._

import scalaz.concurrent.Task

class AppsFragment
  extends BaseActionFragment
  with AppsComposer
  with UiExtensions
  with NineCardIntentConversions {

  val allApps = AllAppsCategory

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val category = NineCardCategory(getString(Seq(getArguments), AppsFragment.categoryKey, AllAppsCategory.name))

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    runUi(initUi(category == allApps, checked => loadApps(if (checked) {
      AppsByCategory
    } else {
      AllApps
    }, reload = true)))

    loadApps(if (category == allApps) AllApps else AppsByCategory)
  }

  private[this] def loadApps(
    filter: AppsFilter,
    reload: Boolean = false) = Task.fork(di.deviceProcess.getSavedApps(GetByName).run).resolveAsyncUi(
    onPreTask = () => showLoading,
    onResult = (apps: Seq[App]) => if (reload) {
      reloadAppsAdapter(getAppsByFilter(apps, filter), filter, category)
    } else {
      generateAppsAdapter(getAppsByFilter(apps, filter), filter, category, (app: App) => {
        val card = AddCardRequest(
          term = app.name,
          packageName = Option(app.packageName),
          cardType = AppCardType,
          intent = toNineCardIntent(app),
          imagePath = app.imagePath
        )
        activity[CollectionsDetailsActivity] foreach (_.addCards(Seq(card)))
        runUi(unreveal())
      })
    },
    onException = (ex: Throwable) => showGeneralError
  )

  def getAppsByFilter(apps: Seq[App], filter: AppsFilter) = filter match {
    case AllApps => apps
    case AppsByCategory =>
      category match {
        case Game => apps filter(app => gamesCategories contains app.category)
        case c => apps filter(_.category.name.contains(c))
      }
  }

}

object AppsFragment {
  val categoryKey = "category"
}

sealed trait AppsFilter

case object AllApps extends AppsFilter

case object AppsByCategory extends AppsFilter
