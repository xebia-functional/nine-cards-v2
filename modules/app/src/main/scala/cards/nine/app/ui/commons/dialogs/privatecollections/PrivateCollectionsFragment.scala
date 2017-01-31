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

package cards.nine.app.ui.commons.dialogs.privatecollections

import android.app.Dialog
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.jobs.LauncherJobs
import cards.nine.commons.services.TaskService._
import cards.nine.models.CollectionData
import cards.nine.models.types.theme.CardLayoutBackgroundColor
import com.fortysevendeg.ninecardslauncher.R

class PrivateCollectionsFragment(implicit launcherJobs: LauncherJobs)
    extends BaseActionFragment
    with PrivateCollectionsDOM
    with PrivateCollectionsUiActions
    with PrivateCollectionsListener
    with AppNineCardsIntentConversions { self =>

  lazy val collectionJobs = new PrivateCollectionsJobs(self)

  lazy val packages =
    getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int =
    theme.get(CardLayoutBackgroundColor)

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    collectionJobs.initialize().resolveAsync()
  }

  override def loadPrivateCollections(): Unit =
    collectionJobs
      .loadPrivateCollections()
      .resolveServiceOr(_ => showErrorLoadingCollectionInScreen())

  override def saveCollection(collection: CollectionData): Unit = {
    (for {
      collectionAdded <- collectionJobs.saveCollection(collection)
      _               <- launcherJobs.addCollection(collectionAdded)
    } yield ()).resolveServiceOr(_ => showErrorSavingCollectionInScreen())
  }
}
