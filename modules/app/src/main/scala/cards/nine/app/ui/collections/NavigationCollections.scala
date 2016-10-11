package cards.nine.app.ui.collections

import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import cards.nine.app.ui.collections.actions.apps.AppsFragment
import cards.nine.app.ui.collections.actions.contacts.ContactsFragment
import cards.nine.app.ui.collections.actions.recommendations.RecommendationsFragment
import cards.nine.app.ui.collections.actions.shortcuts.ShortcutFragment
import cards.nine.app.ui.collections.dialog.EditCardDialogFragment
import cards.nine.app.ui.collections.dialog.publishcollection.PublishCollectionFragment
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SharedCollectionJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.commons._
import cards.nine.models.Collection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, FragmentBuilder, FragmentManagerContext, Ui}

class NavigationCollections {

  val tagDialog = "dialog"

  def openApps(args: Bundle)
    (implicit
      contextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): Ui[Any] = launchDialog(f[AppsFragment], args)

  def openContacts(args: Bundle)
    (implicit
      contextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): Ui[Any] = launchDialog(f[ContactsFragment], args)

  def openShortcuts(args: Bundle)
    (implicit
      contextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): Ui[Any] = launchDialog(f[ShortcutFragment], args)

  def openRecommendations(args: Bundle)
    (implicit
      contextWrapper: ActivityContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): Ui[Any] = launchDialog(f[RecommendationsFragment], args)

  def openPublishCollection(collection: Collection)
    (implicit
      activityContextWrapper: ActivityContextWrapper,
      sharedCollectionJobs: SharedCollectionJobs): Unit = showDialog(PublishCollectionFragment(collection))

  def openEditCard(cardName: String, onChangeName: (Option[String]) => Unit)
    (implicit
      activityContextWrapper: ActivityContextWrapper): Unit = showDialog(new EditCardDialogFragment(cardName, onChangeName))

  private[this] def showDialog(dialog: DialogFragment)(implicit activityContextWrapper: ActivityContextWrapper): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  private[this] def launchDialog[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], args: Bundle)
    (implicit fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      groupCollectionsJobs: GroupCollectionsJobs): Ui[Any] = {
    fragmentBuilder.pass(args).framed(R.id.action_fragment_content, ActionsBehaviours.nameActionFragment)
  }

}
