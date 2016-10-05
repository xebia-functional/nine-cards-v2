package cards.nine.app.ui.collections.actions.apps

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.UiExtensions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.ApplicationData
import cards.nine.models.types.{AllAppsCategory, NineCardCategory}
import com.fortysevendeg.ninecardslauncher2.R

class AppsFragment(implicit groupCollectionsJobs: GroupCollectionsJobs, singleCollectionJobs: Option[SingleCollectionJobs])
  extends BaseActionFragment
  with AppsIuActions
  with AppsDOM
  with AppsUiListener
  with Conversions
  with UiExtensions { self =>

  val allApps = AllAppsCategory

  lazy val appsJobs = AppsJobs(
    category = NineCardCategory(getString(Seq(getArguments), AppsFragment.categoryKey, AllAppsCategory.name)),
    actions = self)

  override def getLayoutId: Int = R.layout.list_action_with_scroller_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    appsJobs.initialize().resolveAsync()
  }

  override def onDestroy(): Unit = {
    appsJobs.destroy().resolveAsync()
    super.onDestroy()
  }

  override def loadApps(filter: AppsFilter): Unit =
    appsJobs.loadApps(filter).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps(filter))

  override def addApp(app: ApplicationData): Unit =
    (for {
      cards <- groupCollectionsJobs.addCards(Seq(toAddCardRequest(app)))
      _ <- singleCollectionJobs match {
        case Some(job) => job.addCards(cards)
        case _ => TaskService.empty
      }
      _ <- appsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => appsJobs.showError())

  override def swapFilter(): Unit = appsJobs.swapFilter().resolveAsync()
}

object AppsFragment {
  val categoryKey = "category"
}

sealed trait AppsFilter

case object AllApps extends AppsFilter

case object AppsByCategory extends AppsFilter
