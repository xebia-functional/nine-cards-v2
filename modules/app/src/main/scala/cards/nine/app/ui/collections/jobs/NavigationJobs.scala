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

package cards.nine.app.ui.collections.jobs

import android.os.Bundle
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.commons.dialogs.apps.AppsFragment
import cards.nine.app.ui.commons.dialogs.recommendations.RecommendationsFragment
import cards.nine.app.ui.collections.jobs.uiactions.{
  GroupCollectionsUiActions,
  NavigationUiActions
}
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardsCategory
import macroid.ActivityContextWrapper

class NavigationJobs(
    val groupCollectionsUiActions: GroupCollectionsUiActions,
    val navigationUiActions: NavigationUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs {

  def showAppDialog()(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val collection = navigationUiActions.dom.getCurrentCollection
    val category   = collection flatMap (_.appsCategory)
    val map        = category map (cat => Map(AppsFragment.categoryKey -> cat)) getOrElse Map.empty
    val packages =
      (collection map (_.cards flatMap (_.packageName))).toSeq.flatten
    val args = createBundle(map, packages)
    navigationUiActions.openApps(args)
  }

  def showRecommendationDialog()(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val collection = navigationUiActions.dom.getCurrentCollection
    val packages   = collection map (_.cards flatMap (_.packageName)) getOrElse Seq.empty
    val category   = collection flatMap (_.appsCategory)
    val map = category map (cat =>
                              Map(RecommendationsFragment.categoryKey -> cat)) getOrElse Map.empty
    if (category.isEmpty && packages.isEmpty) {
      groupCollectionsUiActions.showContactUsError()
    } else {
      val args = createBundle(map)
      navigationUiActions.openRecommendations(args)
    }
  }

  def showContactsDialog()(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] = {
    val args = createBundle()
    navigationUiActions.openContacts(args)
  }

  def showShortcutDialog()(
      implicit groupCollectionsJobs: GroupCollectionsJobs,
      singleCollectionJobs: Option[SingleCollectionJobs]): TaskService[Unit] =
    for {
      _ <- singleCollectionJobs match {
        case Some(job) => job.saveCollectionIdForShortcut()
        case _         => TaskService.empty
      }
      args <- TaskService(CatchAll[JobException](createBundle()))
      _    <- navigationUiActions.openShortcuts(args)
    } yield ()

  private[this] def createBundle(
      map: Map[String, NineCardsCategory] = Map.empty,
      packages: Seq[String] = Seq.empty): Bundle = {
    val args = new Bundle()
    args.putStringArray(BaseActionFragment.packages, packages.toArray)
    map foreach (item => {
                   val (categoryKey, category) = item
                   args.putString(categoryKey, category.name)
                 })
    navigationUiActions.dom.getCurrentCollection foreach (c =>
                                                            args.putInt(
                                                              BaseActionFragment.colorPrimary,
                                                              statuses.theme.getIndexColor(
                                                                c.themedColorIndex)))
    args
  }

}
