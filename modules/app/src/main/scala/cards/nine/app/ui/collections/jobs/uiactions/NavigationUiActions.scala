/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.collections.jobs.uiactions

import android.os.Bundle
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.support.v7.app.{AppCompatActivity, AppCompatDialogFragment}
import cards.nine.app.ui.commons.dialogs.apps.AppsFragment
import cards.nine.app.ui.commons.dialogs.contacts.ContactsFragment
import cards.nine.app.ui.commons.dialogs.recommendations.RecommendationsFragment
import cards.nine.app.ui.commons.dialogs.shortcuts.ShortcutDialogFragment
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.collections.dialog.EditCardDialogFragment
import cards.nine.app.ui.collections.dialog.publishcollection.PublishCollectionFragment
import cards.nine.app.ui.collections.jobs.{
  GroupCollectionsJobs,
  SharedCollectionJobs,
  SingleCollectionJobs
}
import cards.nine.app.ui.commons.UiContext
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.Collection
import macroid.{ActivityContextWrapper, FragmentManagerContext, Ui}

class NavigationUiActions(val dom: GroupCollectionsDOM)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  val tagDialog = "dialog"

  def openApps(args: Bundle)(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] =
    launchDialog(new AppsFragment, args).toService()

  def openContacts(args: Bundle)(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] =
    launchDialog(new ContactsFragment, args).toService()

  def openShortcuts(args: Bundle)(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] =
    launchDialog(new ShortcutDialogFragment, args).toService()

  def openRecommendations(args: Bundle)(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] =
    launchDialog(new RecommendationsFragment, args).toService()

  def openPublishCollection(collection: Collection)(
      implicit sharedCollectionJobs: SharedCollectionJobs): TaskService[Unit] =
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

  private[this] def launchDialog[F <: DialogFragment](fragment: F, args: Bundle): Ui[Any] =
    Ui {
      fragment.setArguments(args)
      fragment.show(fragmentManagerContext.manager, tagDialog)
    }

}
