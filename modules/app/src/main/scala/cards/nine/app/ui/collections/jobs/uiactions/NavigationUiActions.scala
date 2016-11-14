package cards.nine.app.ui.collections.jobs.uiactions

import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.support.v7.app.{AppCompatActivity, AppCompatDialogFragment}
import cards.nine.app.ui.collections.actions.apps.AppsFragment
import cards.nine.app.ui.collections.actions.contacts.ContactsFragment
import cards.nine.app.ui.collections.actions.recommendations.RecommendationsFragment
import cards.nine.app.ui.collections.actions.shortcuts.ShortcutFragment
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.collections.dialog.EditCardDialogFragment
import cards.nine.app.ui.collections.dialog.publishcollection.PublishCollectionFragment
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SharedCollectionJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.UiContext
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Collection
import macroid.{ActivityContextWrapper, FragmentManagerContext, Ui}

class NavigationUiActions
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  val tagDialog = "dialog"

  def openApps(args: Bundle)
    (implicit
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = launchDialog(new AppsFragment, args).toService

  def openContacts(args: Bundle)
    (implicit
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = launchDialog(new ContactsFragment, args).toService

  def openShortcuts(args: Bundle)
    (implicit
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = launchDialog(new ShortcutFragment, args).toService

  def openRecommendations(args: Bundle)
    (implicit
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = launchDialog(new RecommendationsFragment, args).toService

  def openPublishCollection(collection: Collection)
    (implicit
      sharedCollectionJobs: SharedCollectionJobs): TaskService[Unit] =
    TaskService.right(showDialog(PublishCollectionFragment(collection)))

  def openEditCard(cardName: String, onChangeName: (Option[String]) => Unit): TaskService[Unit] =
    TaskService.right(showDialog(new EditCardDialogFragment(cardName, onChangeName)))

  private[this] def showDialog(dialog: DialogFragment): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  private[this] def launchDialog[F <: DialogFragment]
  (fragment: F, args: Bundle): Ui[Any] = Ui {
    fragment.setArguments(args)
    fragment.show(fragmentManagerContext.manager, tagDialog)
  }

}
