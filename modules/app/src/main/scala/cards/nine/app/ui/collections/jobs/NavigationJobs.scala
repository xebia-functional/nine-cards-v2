package cards.nine.app.ui.collections.jobs

import android.os.Bundle
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.actions.apps.AppsFragment
import cards.nine.app.ui.collections.actions.recommendations.RecommendationsFragment
import cards.nine.app.ui.collections.jobs.uiactions.{GroupCollectionsUiActions, NavigationUiActions}
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.NineCardsCategory
import macroid.ActivityContextWrapper

class NavigationJobs(
  val groupCollectionsUiActions: GroupCollectionsUiActions,
  val navigationUiActions: NavigationUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def showAppDialog()
  (implicit
    groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val collection = navigationUiActions.dom.getCurrentCollection
    val category = collection flatMap (_.appsCategory)
    val map = category map (cat => Map(AppsFragment.categoryKey -> cat)) getOrElse Map.empty
    val packages = (collection map (_.cards flatMap (_.packageName))).toSeq.flatten
    val args = createBundle(map, packages)
    navigationUiActions.openApps(args)
  }

  def showRecommendationDialog()
  (implicit
    groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val collection = navigationUiActions.dom.getCurrentCollection
    val packages = collection map (_.cards flatMap (_.packageName)) getOrElse Seq.empty
    val category = collection flatMap (_.appsCategory)
    val map = category map (cat => Map(RecommendationsFragment.categoryKey -> cat)) getOrElse Map.empty
    if (category.isEmpty && packages.isEmpty) {
      groupCollectionsUiActions.showContactUsError()
    } else {
      val args = createBundle(map)
      navigationUiActions.openRecommendations(args)
    }
  }

  def showContactsDialog()
  (implicit
    groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val args = createBundle()
    navigationUiActions.openContacts(args)
  }

  def showShortcutDialog()
  (implicit
    groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val args = createBundle()
    navigationUiActions.openShortcuts(args)
  }

  private[this] def createBundle(map: Map[String, NineCardsCategory] = Map.empty, packages: Seq[String] = Seq.empty): Bundle = {
    val args = new Bundle()
    args.putStringArray(BaseActionFragment.packages, packages.toArray)
    map foreach (item => {
      val (categoryKey, category) = item
      args.putString(categoryKey, category.name)
    })
    navigationUiActions.dom.getCurrentCollection foreach (c =>
      args.putInt(BaseActionFragment.colorPrimary, statuses.theme.getIndexColor(c.themedColorIndex)))
    args
  }

}
